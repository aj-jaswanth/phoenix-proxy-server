package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolMessage;
import in.rgukt.phoenix.core.protocols.BufferedStreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class HttpResponseProcessor extends ApplicationLayerProtocolMessage {
	private ByteBuffer headers;
	private ByteBuffer body;
	private BufferedStreamReader bufferedReader;
	private HashMap<String, String> map = new HashMap<String, String>();
	private String[] initialLineArray;
	private int headerInsertionPoint;

	public HttpResponseProcessor(ByteBuffer message,
			BufferedStreamReader bufferedReader) throws IOException {
		this.headers = message;
		this.body = new ByteBuffer(
				Constants.HttpProtocol.responseBodyBufferSize);
		this.bufferedReader = bufferedReader;
		readCompleteStream();
	}

	private void readCompleteStream() throws IOException {
		byte b = 0;
		int state = HttpResponseStates.initialLine;
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
			case HttpResponseStates.initialLine:
				if (b == '\n') {
					initialLineArray = new String(headers.getBuffer(), 0,
							headers.getPosition()).trim().split(" ");
					p = headers.getPosition();
					state = HttpResponseStates.headerLine;
					break;
				}
				break;
			case HttpResponseStates.headerLine:
				if (b == '\n') {
					state = HttpResponseStates.headerLineEnd;
					noRead = true;
					break;
				}
				if (b == ':' && q == prevQ)
					q = headers.getPosition() - 1;
				break;
			case HttpResponseStates.headerLineEnd:
				byte[] temp = headers.getBuffer();
				if (prevQ == q) {
					state = HttpResponseStates.headerSectionEnd;
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
				state = HttpResponseStates.headerLine;
				p = headers.getPosition();
				prevQ = q;
				break;
			case HttpResponseStates.headerSectionEnd:
				String len = map.get("Content-Length");
				if (len == null) {
					String encoding = map.get("Transfer-Encoding");
					if (encoding != null && encoding.equals("chunked")) {
						readChunkedData(bufferedReader);
					}
				} else {
					int l = Integer.parseInt(len);
					body.put(bufferedReader.read(l));
				}
				return;
			}
		}
	}

	private void readChunkedData(BufferedStreamReader bufferedReader)
			throws IOException {
		int state = HttpResponseStates.lengthLine, counter = 0;
		StringBuilder length = new StringBuilder();
		boolean noRead = false, semicolonFound = false;
		byte b = 0;
		while (true) {
			if (noRead == false) {
				b = bufferedReader.read();
				if (b == -1)
					return;
				body.put(b);
			} else
				noRead = true;
			switch (state) {
			case HttpResponseStates.lengthLine:
				if (b == '\n') {
					state = HttpResponseStates.lengthLineEnd;
					noRead = true;
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
				noRead = false;
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
		return map.get(headerKey);
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
	public ApplicationLayerProtocolMessage getComplementaryProcessor(
			InputStream inputStream) throws IOException {
		return new HttpRequestProcessor(new ByteBuffer(
				Constants.HttpProtocol.requestHeaderBufferSize),
				new BufferedStreamReader(inputStream,
						Constants.HttpProtocol.streamBufferSize));
	}

	@Override
	public String getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getInsertionPoint() {
		return headerInsertionPoint;
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
