package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolMessage;
import in.rgukt.phoenix.core.protocols.BufferedStreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class HttpProtocolRequest extends ApplicationLayerProtocolMessage {

	private ByteBuffer message;
	private BufferedStreamReader bufferedReader;
	private HashMap<String, String> map = new HashMap<String, String>();
	private String[] initialLineArray;
	private String[] serverAddress;
	private int headerInsertionPoint;

	public HttpProtocolRequest(ByteBuffer message,
			BufferedStreamReader bufferedReader) throws IOException {
		this.message = message;
		this.bufferedReader = bufferedReader;
		processCompleteStream();
	}

	private void processCompleteStream() throws IOException {
		byte b = 0;
		int state = HttpRequestStates.initialLine;
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
			case HttpRequestStates.initialLine:
				if (b == '\n') {
					initialLineArray = new String(message.getBuffer(), 0,
							message.getPosition()).trim().split(" ");
					p = message.getPosition();
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
					q = message.getPosition() - 1;
				break;
			case HttpRequestStates.headerLineEnd:
				byte[] temp = message.getBuffer();
				if (prevQ == q) {
					state = HttpRequestStates.headerSectionEnd;
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
				state = HttpRequestStates.headerLine;
				p = message.getPosition();
				prevQ = q;
				break;
			case HttpRequestStates.headerSectionEnd:
				if (initialLineArray[0].equals("POST")) {
					int len = Integer.parseInt(map.get("Content-Length"));
					for (int x = 0; x < len; x++)
						message.put(bufferedReader.read());
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
	public ByteBuffer getMessage() {
		return message;
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
	public ApplicationLayerProtocolMessage getComplementaryObject(
			InputStream inputStream) throws IOException {
		return new HttpProtocolResponse(new ByteBuffer(
				Constants.HttpProtocol.responseBufferSize),
				new BufferedStreamReader(inputStream,
						Constants.HttpProtocol.responseBufferSize));
	}

	@Override
	public String getResource() {
		return initialLineArray[1];
	}

	public int getInsertionPoint() {
		return headerInsertionPoint;
	}

	@Override
	public void sendMessageToServer(OutputStream outputStream) {
		// TODO Auto-generated method stub

	}
}