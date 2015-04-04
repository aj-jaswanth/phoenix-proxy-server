package in.rgukt.phoenix.core.authentication;

import in.rgukt.phoenix.core.authentication.decoders.Base64;

import java.util.HashMap;

public class BasicAuthenticator extends Authenticator {

	private static HashMap<String, String> map = new HashMap<String, String>();
	static { // TODO: Use a Database
		map.put("abcd", "abcd");
	}

	public static boolean isValid(String str) {
		String str2 = str.split(" ")[1];
		String[] a = Base64.decode(str2).split(":");
		if (a.length == 2) {
			String s = map.get(a[0]);
			if (s != null && s.equals(a[1])) {
				addToAuthenticationCache(str);
				return true;
			}
		}
		return false;
	}
}