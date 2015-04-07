package in.rgukt.phoenix.core.authentication;

import in.rgukt.phoenix.core.TimeStamp;
import in.rgukt.phoenix.core.authentication.decoders.Base64;

public final class BasicAuthenticator extends Authenticator {

	public static String isValid(String str) {
		String str2 = str.split(" ")[1];
		String[] a = Base64.decode(str2).split(":");
		if (a.length == 2) {
			String s = FileAuthenticator.getPassword(a[0]);
			if (s != null && s.equals(a[1])) {
				addToAuthenticationCache(str, new AuthenticationCacheItem(a[0],
						TimeStamp.getCurrentTimeStamp()));
				return a[0];
			}
		}
		return null;
	}
}