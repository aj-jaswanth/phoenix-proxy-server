package in.rgukt.phoenix.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * This is used to load files relative to the runtime environment. This
 * eliminates the need for absolute file paths.
 * 
 * @author Venkata Jaswanth
 */
public final class FileHandler {
	/**
	 * Reads the given file and returns its contents as byte array.
	 * 
	 * @param fileLocation
	 *            file path relative to the runtime
	 * @return file's contents as byte array
	 */
	public static byte[] readAsBytes(String fileLocation) throws IOException {
		URL fileUrl = ClassLoader.getSystemClassLoader().getResource(
				fileLocation);
		InputStream inputStream = fileUrl.openStream();
		ByteBuffer buffer = new ByteBuffer(1024);
		int b = 0;
		while ((b = inputStream.read()) != -1)
			buffer.put((byte) b);
		inputStream.close();
		buffer.trim();
		return buffer.getBuffer();
	}
}