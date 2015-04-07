package in.rgukt.phoenix;

import in.rgukt.phoenix.core.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Configurator {

	public static void configureServer(String string) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(string));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		String token = null;
		while (scanner.hasNext()) {
			token = scanner.next();
			switch (token) {
			case "port":
				Constants.Server.port = scanner.nextInt();
				break;
			case "credentials_ttl":
				Constants.Server.credentialsttl = parseTime(scanner.next());
				break;
			case "credentials_update_interval":
				Constants.Server.credentialsUpdateInterval = parseTime(scanner
						.next());
				break;
			case "max_user_quota":
				Constants.Server.maxUserQuota = parseDataSize(scanner.next());
				break;
			case "quota_update_interval":
				Constants.Server.quotaUpdateInterval = parseTime(scanner.next());
				break;
			case "in_memory_max_response_save_size":
				Constants.HttpProtocol.inMemoryMaxResponseSaveSize = parseDataSize(scanner
						.next());
				break;
			case "stream_buffer_size":
				Constants.HttpProtocol.streamBufferSize = (int) parseDataSize(scanner
						.next());
				break;
			case "max_cache_size":
				Constants.HttpProtocol.maxCacheSize = (int) parseDataSize(scanner
						.next());
				break;
			case "max_log_file_rotate_size":
				Constants.Server.maxLogFileRotateSize = parseDataSize(scanner
						.next());
				break;
			case "log_file":
				Constants.Server.logFile = Constants.prefix + scanner.next();
				break;
			case "credentials_file":
				Constants.Server.credentialsFile = Constants.prefix
						+ scanner.next();
				break;
			case "quota_file":
				Constants.Server.quotaFile = Constants.prefix + scanner.next();
				break;
			default:
				if (token.charAt(0) != '#') {
					System.err.println("Invalid configuration line : " + token);
					return;
				}
				scanner.nextLine();
				break;
			}
		}
		scanner.close();
	}

	private static long parseTime(String time) {
		int fistColon = time.indexOf(':');
		int secondColon = time.indexOf(':', fistColon + 1);
		int hours = Integer.parseInt(time.substring(0, fistColon));
		int minutes = Integer.parseInt(time.substring(fistColon + 1,
				secondColon));
		int seconds = Integer.parseInt(time.substring(secondColon + 1));
		int milliseconds = ((hours * 60 * 60) + (minutes * 60) + seconds) * 1000;
		return milliseconds;
	}

	private static long parseDataSize(String dataSize) {
		int len = dataSize.length();
		long size = Integer.parseInt(dataSize.substring(0, len - 2));
		switch (dataSize.substring(len - 2, len)) {
		case "KB":
			size <<= 10;
			break;
		case "MB":
			size <<= 20;
			break;
		case "GB":
			size <<= 30;
			break;
		case "TB":
			size <<= 40;
			break;
		}
		return size;
	}
}