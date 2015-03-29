package in.rgukt.phoenix.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileHandler {
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