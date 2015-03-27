package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolMessage;
import in.rgukt.phoenix.core.protocols.BufferedStreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class HttpProtocolResponse extends ApplicationLayerProtocolMessage {
	private ByteBuffer message;
	private BufferedStreamReader bufferedReader;
	private HashMap<String, String> map = new HashMap<String, String>();
	private String[] initialLineArray;
	private int headerInsertionPoint;

	public HttpProtocolResponse(ByteBuffer message,
			BufferedStreamReader bufferedReader) throws IOException {
		this.message = message;
		this.bufferedReader = bufferedReader;
		processCompleteStream();
	}

	private void processCompleteStream() throws IOException {
		byte b = 0;
		int state = HttpResponseStates.initialLine;
		int p = 0, q = 0, prevQ = 0;
		boolean noRead = false;
		while (true) {
			if (noRead == false) {
				b = bufferedReader.read();
				if (b == -1)
					return;
				message.put(b);
			} else
				noRead = false;
			switch (state) {
			case HttpResponseStates.initialLine:
				if (b == '\n') {
					initialLineArray = new String(message.getBuffer(), 0,
							message.getPosition()).trim().split(" ");
					p = message.getPosition();
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
					q = message.getPosition() - 1;
				break;
			case HttpResponseStates.headerLineEnd:
				byte[] temp = message.getBuffer();
				if (prevQ == q) {
					state = HttpResponseStates.headerSectionEnd;
					noRead = true;
					int pos = message.getPosition();
					if (message.get(pos - 2) == '\r')
						headerInsertionPoint = pos - 2;
					else
						headerInsertionPoint = pos - 1;
					break;
				}
				map.put(new String(temp, p, q - p).trim(), new String(temp,
						q + 1, message.getPosition() - q - 1).trim());
				state = HttpResponseStates.headerLine;
				p = message.getPosition();
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
					byte[] data = bufferedReader.read(l);
					message.put(data);
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
				message.put(b);
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
				for (int x = 0; x < len; x++) {
					b = bufferedReader.read();
					if (b == -1)
						return;
					message.put(b);
				}
				while (true) {
					b = bufferedReader.read();
					if (b == -1)
						return;
					message.put(b);
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
	public ByteBuffer getMessage() {
		return message;
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
	public ApplicationLayerProtocolMessage getComplementaryObject(
			InputStream inputStream) throws IOException {
		return new HttpProtocolRequest(new ByteBuffer(
				Constants.HttpProtocol.requesetBufferSize),
				new BufferedStreamReader(inputStream,
						Constants.HttpProtocol.requesetBufferSize));
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
	public void sendMessageToServer(OutputStream outputStream) {
		// TODO Auto-generated method stub

	}
}
