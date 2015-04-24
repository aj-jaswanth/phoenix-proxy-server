package in.rgukt.phoenix.core.protocols;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads data from a stream. Uses buffering to increase performance.
 * 
 * @author Venkata Jaswanth
 */
public final class BufferedStreamReader {

	private InputStream inputStream;
	private byte[] buffer;
	private int position = 0;
	private int limit = 0;
	private int capacity;

	public BufferedStreamReader(InputStream inputStream, int bufferSize) {
		this.inputStream = inputStream;
		this.buffer = new byte[bufferSize];
		this.capacity = bufferSize;
	}

	/**
	 * Reads a single byte
	 * 
	 * @return byte read
	 */
	public byte read() throws IOException {
		if (position == limit) {
			limit = inputStream.read(buffer, 0, capacity);
			if (limit == -1)
				return -1;
			position = 0;
		}
		return buffer[position++];
	}

	/**
	 * Reads length bytes from the stream
	 * 
	 * @param length
	 * @return data read
	 */
	public byte[] read(int length) throws IOException {
		byte[] array = new byte[length];
		if (position < limit) {
			int remainder = limit - position;
			if (length >= remainder) {
				System.arraycopy(buffer, position, array, 0, remainder);
				length -= remainder;
				position = limit;
				readIteratively(array, remainder, length);
			} else {
				System.arraycopy(buffer, position, array, 0, length);
				position += length;
			}
		} else
			readIteratively(array, 0, length);
		return array;
	}

	/**
	 * Reads data from socket until length bytes are read.
	 * 
	 * @param array
	 * @param offset
	 * @param length
	 */
	public void readIteratively(byte[] array, int offset, int length)
			throws IOException {
		int bytesRead = 0;
		while (bytesRead < length)
			bytesRead += inputStream.read(array, offset + bytesRead, length
					- bytesRead);
	}
}