package in.rgukt.phoenix.core.authentication;

import java.util.HashMap;
import java.util.Map.Entry;

public class Authenticator {

	protected static HashMap<String, String> authenticationCache = new HashMap<String, String>();
	private static char[] hexMap = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static void removeUser(String userName) {
		FileAuthenticator.removeUser(userName);
		for (Entry<String, String> entry : authenticationCache.entrySet()) {
			if (entry.getValue().equals(userName)) {
				authenticationCache.remove(entry.getKey());
				break;
			}
		}
	}

	public static void updateUser(String userName, String passwordHash) {
		removeUser(userName);
		addUser(userName, passwordHash);
	}

	public static void addUser(String userName, String passwordHash) {
		FileAuthenticator.addUser(userName, passwordHash);
	}

	public static String isValid(String str) {
		String userName = isInCache(str);
		if (userName != null)
			return userName;
		int state = 0;
		char c;
		for (int x = 0; x < str.length(); x++) {
			c = str.charAt(x);
			switch (state) {
			case 0:
				if (c == 'B')
					state = 1;
				else if (c == 'C')
					state = 2;
				break;
			case 1:
				if (c == 'a')
					return BasicAuthenticator.isValid(str);
				break;
			case 2:
				if (c == 'u')
					return CustomAuthenticator.isValid(str);
				break;
			}
		}
		return null;
	}

	protected synchronized static String isInCache(String str) {
		String user = authenticationCache.get(str);
		return user;
	}

	protected synchronized static void addToAuthenticationCache(String str,
			String user) {
		authenticationCache.put(str, user);
	}

	protected static String getHexRepresentation(byte[] array) {
		StringBuffer sb = new StringBuffer();
		char c;
		for (byte b : array) {
			c = (char) b;
			sb.append(hexMap[(c & 0xf0) >> 4]);
			sb.append(hexMap[c & 0x0f]);
		}
		return sb.toString();
	}
}