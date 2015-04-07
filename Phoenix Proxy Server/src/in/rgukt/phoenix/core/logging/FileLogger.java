package in.rgukt.phoenix.core.logging;

import in.rgukt.phoenix.core.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class FileLogger {

	private static BufferedWriter bufferedWriter;
	private static File file;
	private static int count = 1;
	static {
		createNewFile(Constants.Server.logFile);
	}

	public static void logRequest(String userName, String url,
			boolean cacheHit, long dataUploaded, long dataDownloaded) {
		if (file.length() > Constants.Server.maxLogFileRotateSize) {
			File rFile = file;
			while (rFile.exists())
				rFile = new File(Constants.Server.logFile + "_" + count++);
			file.renameTo(rFile);
			createNewFile(Constants.Server.logFile);
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

	private static void createNewFile(String fileName) {
		file = new File(fileName);
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}