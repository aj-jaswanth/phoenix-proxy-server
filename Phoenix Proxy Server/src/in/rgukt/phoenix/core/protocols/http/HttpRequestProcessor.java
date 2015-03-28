package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolMessage;
import in.rgukt.phoenix.core.protocols.BufferedStreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class HttpRequestProcessor extends ApplicationLayerProtocolMessage {

	private ByteBuffer headers;
	private ByteBuffer body;
	// private ByteBuffer message;
	private BufferedStreamReader bufferedReader;
	private HashMap<String, String> map = new HashMap<String, String>();
	private String[] initialLineArray;
	private String[] serverAddress;
	private int headerInsertionPoint;

	public HttpRequestProcessor(ByteBuffer message,
			BufferedStreamReader bufferedReader) throws IOException {
		this.headers = message;
		this.body = new ByteBuffer(Constants.HttpProtocol.requestBodyBufferSize);
		this.bufferedReader = bufferedReader;
		readCompleteStream();
	}

	private void readCompleteStream() throws IOException {
		byte b = 0;
		int state = HttpRequestStates.initialLine;
		int p = 0, q = 0, prevQ = 0;
		boolean noRead = false;
		while (true) {
			if (noRead == false) {
				b = bufferedReader.read();
				if (b == -1)
					return;
				headers.put(b);
			} else
				noRead = false;
			switch (state) {
			case HttpRequestStates.initialLine:
				if (b == '\n') {
					initialLineArray = new String(headers.getBuffer(), 0,
							headers.getPosition()).trim().split(" ");
					p = headers.getPosition();
					state = HttpRequestStates.headerLine;
					break;
				}
				break;
			case HttpRequestStates.headerLine:
				if (b == '\n') {
					state = HttpRequestStates.headerLineEnd;
					noRead = true;
					break;
				}
				if (b == ':' && q == prevQ)
					q = headers.getPosition() - 1;
				break;
			case HttpRequestStates.headerLineEnd:
				byte[] temp = headers.getBuffer();
				if (prevQ == q) {
					state = HttpRequestStates.headerSectionEnd;
					noRead = true;
					int pos = headers.getPosition();
					if (headers.get(pos - 2) == '\r')
						headerInsertionPoint = pos - 2;
					else
						headerInsertionPoint = pos - 1;
					break;
				}
				map.put(new String(temp, p, q - p).trim(), new String(temp,
						q + 1, headers.getPosition() - q - 1).trim());
				state = HttpRequestStates.headerLine;
				p = headers.getPosition();
				prevQ = q;
				break;
			case HttpRequestStates.headerSectionEnd:
				if (initialLineArray[0].equals("POST")) {
					int len = Integer.parseInt(map.get("Content-Length"));
					body.put(bufferedReader.read(len));
				}
				return;
			}
		}
	}

	@Override
	public String getValue(String headerKey) {
		return map.get(headerKey);
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
			serverAddress = map.get("Host").split(":");
		return serverAddress[0];
	}

	@Override
	public int getPort() {
		if (serverAddress == null)
			serverAddress = map.get("Host").split(":");
		if (serverAddress.length == 1)
			return 80;
		return Integer.parseInt(serverAddress[1]);
	}

	@Override
	public ApplicationLayerProtocolMessage getComplementaryProcessor(
			InputStream inputStream) throws IOException {
		return new HttpResponseProcessor(new ByteBuffer(
				Constants.HttpProtocol.responseHeaderBufferSize),
				new BufferedStreamReader(inputStream,
						Constants.HttpProtocol.streamBufferSize));
	}

	@Override
	public String getResource() {
		return initialLineArray[1];
	}

	public int getInsertionPoint() {
		return headerInsertionPoint;
	}

	@Override
	public void sendMessage(OutputStream outputStream) throws IOException {
		// TODO: Remove Proxy headers

		outputStream.write(headers.getBuffer(), 0, headers.getPosition());
		outputStream.write(body.getBuffer(), 0, body.getPosition());
	}

	@Override
	public boolean isAuthorized(OutputStream outputStream) throws IOException {
		String headerValue = map.get("Proxy-Authorization");
		// TODO: Check whether proper credentials or not.
		if (headerValue != null) {
			return true;
		}
		HttpHeader authorizationHeaders = new HttpHeader(
				Constants.HttpProtocol.defaultAuthenticationHeaders);
		outputStream.write(authorizationHeaders.getByteArray());
		return false;
	}
}