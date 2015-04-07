package in.rgukt.phoenix.core.authentication;

import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.TimeStamp;

import java.util.HashMap;

public class Authenticator {

	protected static HashMap<String, AuthenticationCacheItem> authenticationCache = new HashMap<String, AuthenticationCacheItem>();

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
				break;
			case 1:
				if (c == 'a')
					return BasicAuthenticator.isValid(str);
				break;
			}
		}
		return null;
	}

	protected synchronized static String isInCache(String str) {
		AuthenticationCacheItem cacheItem = authenticationCache.get(str);
		if (cacheItem != null) {
			if ((TimeStamp.getCurrentDifference(cacheItem.getCachedTimeStamp()) < Constants.Server.credentialsttl))
				return cacheItem.getUserName();
			else
				authenticationCache.remove(str);
		}
		return null;
	}

	protected synchronized static void addToAuthenticationCache(String str,
			AuthenticationCacheItem authenticationCacheItem) {
		authenticationCache.put(str, authenticationCacheItem);
	}
}