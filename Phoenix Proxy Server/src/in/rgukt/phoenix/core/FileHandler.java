package in.rgukt.phoenix.core;

import java.io.FileReader;
import java.io.IOException;

public class FileHandler {
	public static byte[] readBytes(String location) throws IOException {
		FileReader fileReader = new FileReader(location);
		ByteBuffer buffer = new ByteBuffer(1024);
		int b = 0;
		while ((b = fileReader.read()) != -1)
			buffer.put((byte) b);
		fileReader.close();
		buffer.trim();
		return buffer.getBuffer();
	}
}