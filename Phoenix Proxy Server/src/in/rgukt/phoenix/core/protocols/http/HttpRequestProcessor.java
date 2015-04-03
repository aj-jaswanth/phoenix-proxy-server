package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.authentication.Authenticator;
import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolProcessor;
import in.rgukt.phoenix.core.protocols.BufferedStreamReader;
import in.rgukt.phoenix.core.protocols.BufferedStreamReaderWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public final class HttpRequestProcessor extends
		ApplicationLayerProtocolProcessor {

	private Socket clientSocket;
	private Socket serverSocket;
	private OutputStream clientOutputStream;
	private OutputStream serverOutputStream;
	private ByteBuffer headers;
	private ByteBuffer body;
	private BufferedStreamReader bufferedStreamReader;
	private HashMap<String, String> headersMap = new HashMap<String, String>();
	private String[] initialLineArray;
	private String[] serverAddress;
	private HttpHeadersBuilder authorizationHeaders = new HttpHeadersBuilder(
			Constants.HttpProtocol.defaultAuthenticationHeaders);
	private HttpErrorHandler httpErrorHandler = new HttpErrorHandler();

	public HttpRequestProcessor(Socket clientSocket, ByteBuffer message,
			BufferedStreamReader bufferedStreamReader) throws IOException {
		this.clientSocket = clientSocket;
		this.clientOutputStream = clientSocket.getOutputStream();
		this.headers = message;
		this.body = new ByteBuffer(Constants.HttpProtocol.requestBodyBufferSize);
		this.bufferedStreamReader = bufferedStreamReader;
	}

	@Override
	public void processCompleteMessage() throws IOException {
		byte b = 0;
		int state = HttpRequestStates.initialRequestLine;
		int headerStart = 0, headerSemiColon = 0, previousHeaderSemiColon = 0;
		boolean skipRead = false;
		while (true) {
			if (skipRead == false) {
				b = bufferedStreamReader.read();
				if (b == -1)
					return;
				headers.put(b);
			} else
				skipRead = false;
			switch (state) {
			case HttpRequestStates.initialRequestLine:
				if (b == '\n') {
					initialLineArray = new String(headers.getBuffer(), 0,
							headers.getPosition()).trim().split(" ");
					headerStart = headers.getPosition();
					state = HttpRequestStates.headerLine;
					break;
				}
				break;
			case HttpRequestStates.headerLine:
				if (b == '\n') {
					state = HttpRequestStates.headerLineEnd;
					skipRead = true;
					break;
				}
				if (b == ':' && headerSemiColon == previousHeaderSemiColon)
					headerSemiColon = headers.getPosition() - 1;
				break;
			case HttpRequestStates.headerLineEnd:
				byte[] temp = headers.getBuffer();
				if (previousHeaderSemiColon == headerSemiColon) {
					state = HttpRequestStates.headersSectionEnd;
					skipRead = true;
					break;
				}
				headersMap.put(new String(temp, headerStart, headerSemiColon
						- headerStart).trim(), new String(temp,
						headerSemiColon + 1, headers.getPosition()
								- headerSemiColon - 1).trim());
				state = HttpRequestStates.headerLine;
				headerStart = headers.getPosition();
				previousHeaderSemiColon = headerSemiColon;
				break;
			case HttpRequestStates.headersSectionEnd:
				if (isAuthorized()) {
					if (checkSelfConnection() == true
							|| connectToServer() == false)
						return;

					headersMap.remove("Proxy-Authorization");
					HttpHeadersBuilder headersBuilder = new HttpHeadersBuilder(
							new String[] { initialLineArray[0]
									+ " "
									+ removePreceedingHostData(initialLineArray[1])
									+ " " + initialLineArray[2] });
					headersBuilder.addAllHeaders(headersMap);
					headersBuilder.addHeader("Via: "
							+ System.getProperty("os.name"));
					byte[] modifiedHeaders = headersBuilder.getByteArray();
					serverOutputStream.write(modifiedHeaders, 0,
							modifiedHeaders.length);

					if (initialLineArray[0].equals("POST")) {
						String lengthHeaderValue = headersMap
								.get("Content-Length");
						if (lengthHeaderValue == null) {
							String encodingHeaderValue = headersMap
									.get("Transfer-Encoding");
							if (encodingHeaderValue != null
									&& encodingHeaderValue.equals("chunked")) {
								readChunkedData(bufferedStreamReader);
							}
						} else {
							int len = Integer.parseInt(lengthHeaderValue);
							BufferedStreamReaderWriter bufferedStreamReaderWriter = new BufferedStreamReaderWriter(
									serverOutputStream, bufferedStreamReader);
							if (len < Constants.HttpProtocol.inMemoryMaxResponseSaveSize)
								body.put(bufferedStreamReaderWriter
										.readWrite(len));
							else
								bufferedStreamReaderWriter
										.readWriteNoReturn(len);
						}
					}

					HttpResponseProcessor httpResponseProcessor = new HttpResponseProcessor(
							clientSocket, serverSocket);
					httpResponseProcessor.processCompleteMessage();
				} else
					clientOutputStream.write(authorizationHeaders
							.getByteArray());
				if (serverSocket != null)
					serverSocket.close();
				return;
			}
		}
	}

	private void readChunkedData(BufferedStreamReader bufferedStreamReader)
			throws IOException {
		int state = HttpResponseStates.lengthLine, counter = 0;
		StringBuilder length = new StringBuilder();
		boolean skipRead = false, semicolonFound = false;
		BufferedStreamReaderWriter bufferedStreamReaderWriter = new BufferedStreamReaderWriter(
				serverOutputStream, bufferedStreamReader);
		byte b = 0;
		int prevLengthMarker = 0;
		while (true) {
			if (skipRead == false) {
				b = bufferedStreamReader.read();
				if (b == -1)
					return;
				body.put(b);
			} else
				skipRead = true;
			switch (state) {
			case HttpResponseStates.lengthLine:
				if (b == '\n') {
					state = HttpResponseStates.lengthLineEnd;
					skipRead = true;
					semicolonFound = false;
					break;
				} else if (semicolonFound)
					break;
				else if (b == ';') {
					semicolonFound = true;
					break;
				}
				length.append((char) b);
				break;
			case HttpResponseStates.lengthLineEnd:
				clientOutputStream.write(body.getBuffer(), prevLengthMarker,
						body.getPosition() - prevLengthMarker); // TODO: TCP!
				int len = Integer.parseInt(length.toString().trim(), 16);
				length = new StringBuilder();
				if (len == 0) {
					state = HttpResponseStates.readRemainingData;
					counter = 0;
					break;
				}
				if (len < Constants.HttpProtocol.inMemoryMaxResponseSaveSize)
					body.put(bufferedStreamReaderWriter.readWrite(len));
				else
					bufferedStreamReaderWriter.readWriteNoReturn(len);
				prevLengthMarker = body.getPosition();
				while (true) {
					b = bufferedStreamReader.read();
					if (b == -1)
						return;
					body.put(b);
					if (b == '\n')
						break;
				}
				state = HttpResponseStates.lengthLine;
				skipRead = false;
				break;
			case HttpResponseStates.readRemainingData:
				if (b == '\n') {
					if (counter < 2)
						return;
					counter = 0;
				}
				counter++;
				break;
			}
		}
	}

	@Override
	public String getValue(String headerKey) {
		return headersMap.get(headerKey);
	}

	@Override
	public float getVersion() {
		return Float.parseFloat(initialLineArray[2].split("/")[1]);
	}

	@Override
	public String getName() {
		return Constants.HttpProtocol.name;
	}

	@Override
	public String getServer() {
		if (serverAddress == null)
			serverAddress = headersMap.get("Host").split(":");
		return serverAddress[0];
	}

	@Override
	public int getPort() {
		if (serverAddress == null)
			serverAddress = headersMap.get("Host").split(":");
		if (serverAddress.length == 1)
			return 80;
		return Integer.parseInt(serverAddress[1]);
	}

	@Override
	public String getResource() {
		return initialLineArray[1];
	}

	private boolean isAuthorized() throws IOException {
		String authorizationHeaderValue = headersMap.get("Proxy-Authorization");
		if (authorizationHeaderValue != null
				&& Authenticator.isValid(authorizationHeaderValue)) {
			return true;
		}
		return false;
	}

	private boolean checkSelfConnection() throws IOException {
		// Defense against denial-of-service class attack.
		// Otherwise proxy server recursively connects to itself
		// draining resources.

		if (clientSocket.getInetAddress().isLoopbackAddress()
				&& (getPort() == Constants.Server.port)) {
			httpErrorHandler.sendHomePage(clientOutputStream);
			return true;
		}
		return false;
	}

	private boolean connectToServer() throws IOException {
		try {
			serverSocket = new Socket(getServer(), getPort());
			serverOutputStream = serverSocket.getOutputStream();
		} catch (UnknownHostException e) {
			httpErrorHandler.sendUnknownHostError(clientOutputStream,
					getServer());
			return false;
		}
		return true;
	}

	private String removePreceedingHostData(String str) {
		return str.substring(7 + getValue("Host").length(), str.length());
	}
}