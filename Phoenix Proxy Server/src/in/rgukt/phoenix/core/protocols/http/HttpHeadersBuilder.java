package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;

import java.util.Map;

public class HttpHeadersBuilder {
	private ByteBuffer headerBuffer;
	private byte[] delimiter = "\r\n".getBytes();
	private byte[] byteArray;

	public HttpHeadersBuilder(String[] headers) {
		headerBuffer = new ByteBuffer(
				Constants.HttpProtocol.requestHeadersBufferSize);
		for (String header : headers) {
			headerBuffer.put(header.getBytes());
			headerBuffer.put(delimiter);
		}
	}

	public void addHeader(String header) {
		headerBuffer.put(header.getBytes());
		headerBuffer.put(delimiter);
	}

	public void addAllHeaders(Map<String, String> headersMap) {
		for (String header : headersMap.keySet())
			addHeader(header + ": " + headersMap.get(header));
	}

	public byte[] getByteArray() {
		if (byteArray == null) {
			headerBuffer.put(delimiter);
			headerBuffer.trim();
			byteArray = headerBuffer.getBuffer();
		}
		return byteArray;
	}
}