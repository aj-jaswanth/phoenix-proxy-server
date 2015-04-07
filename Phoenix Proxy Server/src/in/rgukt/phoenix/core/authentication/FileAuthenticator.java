package in.rgukt.phoenix.core.authentication;

import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.TimeStamp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileAuthenticator {

	private static Map<String, String> credentialsMap = new HashMap<String, String>();
	private static TimeStamp prevUpdate = TimeStamp.getCurrentTimeStamp();
	static {
		try {
			updateCredentials();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String getPassword(String string) {
		if (TimeStamp.getCurrentDifference(prevUpdate) > Constants.Server.credentialsUpdateInterval)
			try {
				updateCredentials();
				prevUpdate = TimeStamp.getCurrentTimeStamp();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return credentialsMap.get(string);
	}

	private static void updateCredentials() throws FileNotFoundException {
		Scanner scanner = new Scanner(
				new File(Constants.Server.credentialsFile));
		while (scanner.hasNext()) {
			credentialsMap.put(scanner.next(), scanner.next());
		}
		scanner.close();
	}
}