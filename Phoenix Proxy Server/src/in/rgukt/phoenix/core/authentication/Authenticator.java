package in.rgukt.phoenix.core.authentication;

import in.rgukt.phoenix.core.Constants;

import java.sql.Timestamp;
import java.util.HashMap;

public class Authenticator {

	protected static HashMap<String, Timestamp> cache = new HashMap<String, Timestamp>();

	public static boolean isValid(String str) {
		if (isInCache(str))
			return true;
		int state = 0;
		char c;
		for (int x = 0; x < str.length(); x++) {
			c = str.charAt(x);
			switch (state) {
			case 0:
				if (c == 'B')
					state = 1;
				break;
			case 1:
				if (c == 'a')
					return BasicAuthenticator.isValid(str);
				break;
			}
		}
		return true;
	}

	protected static boolean isInCache(String str) {
		Timestamp tp = cache.get(str), currentTimeStamp = new Timestamp(
				System.currentTimeMillis());
		if (tp != null
				&& (currentTimeStamp.getTime() - tp.getTime() < Constants.Server.credentialsttl)) {
			return true;
		}
		return false;
	}
}