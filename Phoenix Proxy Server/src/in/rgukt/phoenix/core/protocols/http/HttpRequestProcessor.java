package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.authentication.Authenticator;
import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolProcessor;
import in.rgukt.phoenix.core.protocols.BufferedStreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class HttpRequestProcessor extends ApplicationLayerProtocolProcessor {

	private ByteBuffer headers;
	private ByteBuffer body;
	private BufferedStreamReader bufferedStreamReader;
	private HashMap<String, String> headersMap = new HashMap<String, String>();
	private String[] initialLineArray;
	private String[] serverAddress;
	private int headersEndingIndex;

	public HttpRequestProcessor(ByteBuffer message,
			BufferedStreamReader bufferedStreamReader) throws IOException {
		super.errorHandler = new HttpErrorHandler();
		this.headers = message;
		this.body = new ByteBuffer(Constants.HttpProtocol.requestBodyBufferSize);
		this.bufferedStreamReader = bufferedStreamReader;
	}

	@Override
	public void processMessage() throws IOException {
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
					int pos = headers.getPosition();
					if (headers.get(pos - 2) == '\r')
						headersEndingIndex = pos - 2;
					else
						headersEndingIndex = pos - 1;
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
				if (initialLineArray[0].equals("POST")) {
					int len = Integer
							.parseInt(headersMap.get("Content-Length"));
					body.put(bufferedStreamReader.read(len));
				}
				return;
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
	public ApplicationLayerProtocolProcessor getComplementaryProcessor(
			InputStream inputStream) throws IOException {
		return new HttpResponseProcessor(new ByteBuffer(
				Constants.HttpProtocol.responseHeadersBufferSize),
				new BufferedStreamReader(inputStream,
						Constants.HttpProtocol.streamBufferSize));
	}

	@Override
	public String getResource() {
		return initialLineArray[1];
	}

	public int getHeadersEndingIndex() {
		return headersEndingIndex;
	}

	@Override
	public void sendMessage(OutputStream outputStream) throws IOException {
		// TODO: Remove Proxy headers

		outputStream.write(headers.getBuffer(), 0, headers.getPosition());
		outputStream.write(body.getBuffer(), 0, body.getPosition());
	}

	@Override
	public boolean isAuthorized(OutputStream outputStream) throws IOException {
		String authorizationHeaderValue = headersMap.get("Proxy-Authorization");
		// TODO: Check whether proper credentials or not.
		if (authorizationHeaderValue != null
				&& Authenticator.isValid(authorizationHeaderValue)) {
			return true;
		}
		HttpHeadersBuilder authorizationHeaders = new HttpHeadersBuilder(
				Constants.HttpProtocol.defaultAuthenticationHeaders);
		outputStream.write(authorizationHeaders.getByteArray());
		return false;
	}
}