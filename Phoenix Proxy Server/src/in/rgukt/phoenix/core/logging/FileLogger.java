package in.rgukt.phoenix.core.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class FileLogger {

	private static BufferedWriter bufferedWriter;

	public static void logRequest(String userName, String url,
			boolean cacheHit, long dataUploaded, long dataDownloaded) {
		if (bufferedWriter == null) {
			try {
				bufferedWriter = new BufferedWriter(new FileWriter(
						System.getProperty("user.home")
								+ "/Desktop/phoenix.log"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(new Timestamp(System.currentTimeMillis()));
		buffer.append(',');
		buffer.append(userName);
		buffer.append(',');
		buffer.append(url);
		buffer.append(',');
		buffer.append(cacheHit == true ? 1 : 0);
		buffer.append(',');
		buffer.append(dataUploaded);
		buffer.append(',');
		buffer.append(dataDownloaded);
		buffer.append('\n');
		try {
			bufferedWriter.write(buffer.toString());
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}