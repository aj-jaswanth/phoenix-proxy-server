package in.rgukt.phoenix.core.authentication;

import javax.xml.bind.DatatypeConverter;

/**
 * Handles custom authentication scheme used by a client software.
 * 
 * @author Venkata Jaswanth
 *
 */
public class CustomAuthenticator extends Authenticator {
	/**
	 * Check whether the authentication identifer is valid or not
	 * 
	 * @param str
	 * @return true if valid else false
	 */
	public static String isValid(String str) {
		String payload = str.split(" ")[1];
		payload = new String(DatatypeConverter.parseBase64Binary(payload));
		String[] array = payload.split(":");
		String userName = array[0];
		String receivedHash = array[1];
		String passwordHash = FileAuthenticator.getPassword(userName);
		if (passwordHash.equals(receivedHash)) {
			addToAuthenticationCache(str, userName);
			return userName;
		}
		return null;
	}
}