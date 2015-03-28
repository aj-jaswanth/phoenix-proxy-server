package in.rgukt.phoenix.core.protocols;

import java.io.IOException;
import java.io.InputStream;

public class BufferedStreamReader {

	private InputStream inputStream;
	private byte[] buffer;
	private int position = 0;
	private int limit = 0;
	private int capacity;

	public BufferedStreamReader(InputStream inputStream, int bufferLength) {
		this.inputStream = inputStream;
		this.buffer = new byte[bufferLength];
		this.capacity = bufferLength;
	}

	public byte read() throws IOException {
		if (position == limit) {
			limit = inputStream.read(buffer, 0, capacity);
			if (limit == -1)
				return -1;
			position = 0;
		}
		return buffer[position++];
	}

	public byte[] read(int length) throws IOException {
		byte[] array = new byte[length];
		if (position < limit) {
			int rem = limit - position;
			if (length >= rem) {
				System.arraycopy(buffer, position, array, 0, rem);
				length -= rem;
				position = limit;
				readIterative(array, rem, length);
			} else {
				System.arraycopy(buffer, position, array, 0, length);
				position += length;
			}
		} else
			readIterative(array, 0, length);
		return array;
	}

	public void readIterative(byte[] array, int offset, int length)
			throws IOException {
		int readData = 0;
		while (readData < length)
			readData += inputStream.read(array, offset + readData, length
					- readData);
	}
}