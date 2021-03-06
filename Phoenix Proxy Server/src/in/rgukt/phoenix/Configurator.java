package in.rgukt.phoenix;

import in.rgukt.phoenix.core.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Reads the configuration file and updates the relevant details
 * 
 * @author Venkata Jaswanth
 */
public class Configurator {

	public static void configureServer() {
		File cfile = new File(Constants.prefix + "configuration");
		if (cfile.exists() == false)
			return;
		Scanner scanner = null;
		try {
			scanner = new Scanner(cfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		String token = null;
		while (scanner.hasNext()) {
			token = scanner.next();
			switch (token) {
			case "proxy_server_port":
				Constants.Server.port = scanner.nextInt();
				break;
			case "quota_dump_interval":
				Constants.Server.quotaDumpInterval = parseTime(scanner.next());
				break;
			case "max_cache_item_size":
				Constants.HttpProtocol.maxCacheItemSize = parseDataSize(scanner
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
				Constants.Server.logFile = scanner.next();
				break;
			case "credentials_file":
				Constants.Server.credentialsFile = scanner.next();
				break;
			case "quota_file":
				Constants.Server.quotaFile = scanner.next();
				break;
			case "quota_reset_point":
				Constants.Server.quotaResetPoint = scanner.next();
				break;
			case "ipc_server_port":
				Constants.IPCServer.port = scanner.nextInt();
				break;
			case "max_concurrent_threads":
				Constants.Server.maxConcurrentThreads = scanner.nextInt();
				break;
			case "number_of_roles":
				Constants.Server.numberOfRoles = scanner.nextInt();
				break;
			case "enable_ipc_server":
				Constants.IPCServer.enabled = scanner.nextBoolean();
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

	/**
	 * Parses time in hh:mm:ss format to milliseconds
	 * 
	 * @param time
	 *            Time as hh:mm:ss string
	 * @return number of milliseconds
	 */
	public static long parseTime(String time) {
		int fistColon = time.indexOf(':');
		int secondColon = time.indexOf(':', fistColon + 1);
		int hours = Integer.parseInt(time.substring(0, fistColon));
		int minutes = Integer.parseInt(time.substring(fistColon + 1,
				secondColon));
		int seconds = Integer.parseInt(time.substring(secondColon + 1));
		int milliseconds = ((hours * 60 * 60) + (minutes * 60) + seconds) * 1000;
		return milliseconds;
	}

	/**
	 * Parses data in general format like 10MB etc., to number of bytes
	 * 
	 * @param dataSize
	 *            data as 10MB etc., in string format
	 * @return number of bytes
	 */
	public static long parseDataSize(String dataSize) {
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