package in.rgukt.phoenix.core.authentication;

import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.TimeStamp;

import java.util.HashMap;

public class Authenticator {

	protected static HashMap<String, TimeStamp> cache = new HashMap<String, TimeStamp>();

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

	protected synchronized static boolean isInCache(String str) {
		TimeStamp tp = cache.get(str);
		if (tp != null
				&& (TimeStamp.getCurrentDifference(tp) < Constants.Server.credentialsttl)) {
			return true;
		}
		return false;
	}

	protected synchronized static void addToAuthenticationCache(String str) {
		cache.put(str, TimeStamp.getCurrentTimeStamp());
	}
}