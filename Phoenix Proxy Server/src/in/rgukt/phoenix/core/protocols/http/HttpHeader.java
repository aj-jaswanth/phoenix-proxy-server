package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;

public class HttpHeader {
	private ByteBuffer headerBuffer;
	private byte[] delimiter = "\r\n".getBytes();

	public HttpHeader(String[] headers) {
		headerBuffer = new ByteBuffer(
				Constants.HttpProtocol.requestHeaderBufferSize);
		for (String header : headers) {
			headerBuffer.put(header.getBytes());
			headerBuffer.put(delimiter);
		}
	}

	public void addHeader(String header) {
		headerBuffer.put(header.getBytes());
		headerBuffer.put(delimiter);
	}

	public byte[] getByteArray() {
		headerBuffer.put(delimiter);
		headerBuffer.trim();
		return headerBuffer.getBuffer();
	}
}