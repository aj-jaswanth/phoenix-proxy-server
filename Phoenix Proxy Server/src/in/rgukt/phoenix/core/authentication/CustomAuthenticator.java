package in.rgukt.phoenix.core.authentication;

import in.rgukt.phoenix.core.TimeStamp;

import javax.xml.bind.DatatypeConverter;

public class CustomAuthenticator extends Authenticator {

	public static String isValid(String str) {
		String payload = str.split(" ")[1];
		payload = new String(DatatypeConverter.parseBase64Binary(payload));
		String[] array = payload.split(":");
		String userName = array[0];
		String receivedHash = array[1];
		String passwordHash = FileAuthenticator.getPassword(userName);
		if (passwordHash.equals(receivedHash)) {
			addToAuthenticationCache(str, new AuthenticationCacheItem(userName,
					TimeStamp.getCurrentTimeStamp()));
			return userName;
		}
		return null;
	}
}