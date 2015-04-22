package in.rgukt.phoenix.core.authentication;

import in.rgukt.phoenix.core.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileAuthenticator {

	private static Map<String, String> credentialsMap = new HashMap<String, String>();

	static {
		try {
			updateCredentials();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	static void removeUser(String userName) {
		credentialsMap.remove(userName);
	}

	static void addUser(String userName, String passwordHash) {
		credentialsMap.put(userName, passwordHash);
	}

	public static String getPassword(String string) {
		return credentialsMap.get(string);
	}

	private static void updateCredentials() throws FileNotFoundException {
		credentialsMap.clear();
		Scanner scanner = new Scanner(
				new File(Constants.Server.credentialsFile));
		while (scanner.hasNext()) {
			credentialsMap.put(scanner.next(), scanner.next());
		}
		scanner.close();
	}
}