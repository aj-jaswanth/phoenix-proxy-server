package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolProcessor;
import in.rgukt.phoenix.core.protocols.BufferedStreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class HttpResponseProcessor extends ApplicationLayerProtocolProcessor {
	private ByteBuffer headers;
	private ByteBuffer body;
	private BufferedStreamReader bufferedStreamReader;
	private HashMap<String, String> headersMap = new HashMap<String, String>();
	private String[] initialLineArray;
	private int headersEndingIndex;

	public HttpResponseProcessor(ByteBuffer message,
			BufferedStreamReader bufferedStreamReader) throws IOException {
		this.headers = message;
		this.body = new ByteBuffer(
				Constants.HttpProtocol.responseBodyBufferSize);
		this.bufferedStreamReader = bufferedStreamReader;
	}

	@Override
	public void processMessage() throws IOException {
		byte b = 0;
		int state = HttpResponseStates.initialLine;
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
			case HttpResponseStates.initialLine:
				if (b == '\n') {
					initialLineArray = new String(headers.getBuffer(), 0,
							headers.getPosition()).trim().split(" ");
					headerStart = headers.getPosition();
					state = HttpResponseStates.headerLine;
					break;
				}
				break;
			case HttpResponseStates.headerLine:
				if (b == '\n') {
					state = HttpResponseStates.headerLineEnd;
					skipRead = true;
					break;
				}
				if (b == ':' && headerSemiColon == previousHeaderSemiColon)
					headerSemiColon = headers.getPosition() - 1;
				break;
			case HttpResponseStates.headerLineEnd:
				byte[] temp = headers.getBuffer();
				if (previousHeaderSemiColon == headerSemiColon) {
					state = HttpResponseStates.headersSectionEnd;
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
				state = HttpResponseStates.headerLine;
				headerStart = headers.getPosition();
				previousHeaderSemiColon = headerSemiColon;
				break;
			case HttpResponseStates.headersSectionEnd:
				String lengthHeaderValue = headersMap.get("Content-Length");
				if (lengthHeaderValue == null) {
					String encodingHeaderValue = headersMap
							.get("Transfer-Encoding");
					if (encodingHeaderValue != null
							&& encodingHeaderValue.equals("chunked")) {
						readChunkedData(bufferedStreamReader);
					}
				} else {
					int len = Integer.parseInt(lengthHeaderValue);
					body.put(bufferedStreamReader.read(len));
				}
				return;
			}
		}
	}

	private void readChunkedData(BufferedStreamReader bufferedReader)
			throws IOException {
		int state = HttpResponseStates.lengthLine, counter = 0;
		StringBuilder length = new StringBuilder();
		boolean skipRead = false, semicolonFound = false;
		byte b = 0;
		while (true) {
			if (skipRead == false) {
				b = bufferedReader.read();
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
				int len = Integer.parseInt(length.toString().trim(), 16);
				length = new StringBuilder();
				if (len == 0) {
					state = HttpResponseStates.readRemainingData;
					counter = 0;
					break;
				}
				body.put(bufferedReader.read(len));
				while (true) {
					b = bufferedReader.read();
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
		return Float.parseFloat(initialLineArray[0].split("/")[1]);
	}

	@Override
	public String getName() {
		return Constants.HttpProtocol.name;
	}

	@Override
	public String getServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ApplicationLayerProtocolProcessor getComplementaryProcessor(
			InputStream inputStream) throws IOException {
		return new HttpRequestProcessor(new ByteBuffer(
				Constants.HttpProtocol.requestHeadersBufferSize),
				new BufferedStreamReader(inputStream,
						Constants.HttpProtocol.streamBufferSize));
	}

	@Override
	public String getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getHeadersEndingIndex() {
		return headersEndingIndex;
	}

	@Override
	public void sendMessage(OutputStream outputStream) throws IOException {
		// TODO: Header injection for authorization and Proxy-Connection

		outputStream.write(headers.getBuffer(), 0, headers.getPosition());
		outputStream.write(body.getBuffer(), 0, body.getPosition());
	}

	@Override
	public boolean isAuthorized(OutputStream outputStream) throws IOException {
		return true;
	}
}
