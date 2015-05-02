package in.rgukt.phoenix.core.authentication;

import in.rgukt.phoenix.core.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Maintains list of users and their passwords
 * 
 * @author Venkata Jaswanth
 */
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

	/**
	 * Get the password of the given user
	 * 
	 * @param userName
	 *            userName
	 * @return password
	 */
	public static String getPassword(String userName) {
		return credentialsMap.get(userName);
	}

	private static void updateCredentials() throws FileNotFoundException {
		credentialsMap.clear();
		Scanner scanner = new Scanner(
				new File(Constants.Server.credentialsFile));
		while (scanner.hasNext()) {
			String userName = scanner.next();
			int role = scanner.nextInt();
			String password = scanner.next();
			credentialsMap.put(userName, password);
			RoleManager.addRole(userName, role);
		}
		scanner.close();
	}
}