package in.rgukt.phoenix.core.authentication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

/**
 * Handles Base64 scheme of authentication
 * 
 * @author Venkata Jaswanth
 */

public final class BasicAuthenticator extends Authenticator {
	/**
	 * Check whether the authentication identifier is valid or not
	 * 
	 * @param str
	 * @return true if valid else false
	 */
	public static String isValid(String str) {
		String str2 = str.split(" ")[1];
		String[] a = new String(DatatypeConverter.parseBase64Binary(str2))
				.split(":");
		if (a.length == 2) {
			String s = FileAuthenticator.getPassword(a[0]);
			String hashedPassword = null;
			try {
				MessageDigest md = MessageDigest.getInstance("SHA1");
				md.update(a[1].getBytes());
				byte[] digest = md.digest();
				hashedPassword = getHexRepresentation(digest);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			if (s != null && s.equals(hashedPassword)) {
				addToAuthenticationCache(str, a[0]);
				return a[0];
			}
		}
		return null;
	}
}