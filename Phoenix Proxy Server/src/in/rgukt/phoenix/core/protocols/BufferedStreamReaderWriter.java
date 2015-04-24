package in.rgukt.phoenix.core.protocols;

import in.rgukt.phoenix.core.Constants;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Reads and writes data simultaneously in chunks.
 * 
 * @author Venkata Jaswanth
 */
public final class BufferedStreamReaderWriter {

	private OutputStream outputStream;
	private BufferedStreamReader bufferedStreamReader;

	public BufferedStreamReaderWriter(OutputStream outputStream,
			BufferedStreamReader bufferedStreamReader) {
		this.outputStream = outputStream;
		this.bufferedStreamReader = bufferedStreamReader;
	}

	/**
	 * Returns the data sent after completion
	 * 
	 * @param length
	 * @return data sent
	 */
	public byte[] readWrite(int length) throws IOException {
		byte[] array = new byte[length], temp;
		int bytesRead = 0;
		int bytesToRead = Constants.HttpProtocol.streamBufferSize;

		while (bytesToRead < length) {
			temp = bufferedStreamReader.read(bytesToRead);
			System.arraycopy(temp, 0, array, bytesRead, bytesToRead);
			bytesRead += bytesToRead;
			outputStream.write(temp, 0, bytesToRead);
			length -= bytesToRead;
		}
		if (length > 0) {
			temp = bufferedStreamReader.read(length);
			System.arraycopy(temp, 0, array, bytesRead, length);
			outputStream.write(temp, 0, length);
		}
		return array;
	}

	/**
	 * Doesn't return data after completion
	 * 
	 * @param length
	 */
	public void readWriteNoReturn(int length) throws IOException {
		int bytesToRead = Constants.HttpProtocol.streamBufferSize;
		byte[] temp;

		while (bytesToRead < length) {
			temp = bufferedStreamReader.read(bytesToRead);
			outputStream.write(temp, 0, bytesToRead);
			length -= bytesToRead;
		}
		if (length > 0) {
			temp = bufferedStreamReader.read(length);
			outputStream.write(temp, 0, length);
		}
	}
}