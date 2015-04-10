package in.rgukt.phoenix.core.authentication;

import in.rgukt.phoenix.core.TimeStamp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class CustomAuthenticator extends Authenticator {

	public static String isValid(String str) {
		String payload = str.split(" ")[1];
		payload = new String(DatatypeConverter.parseBase64Binary(payload));
		String[] array = payload.split(":");
		String userName = array[0];
		String receivedHash = array[1];
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(FileAuthenticator.getPassword(userName).getBytes());
			String passwordHash = DatatypeConverter.printBase64Binary(md
					.digest());
			if (passwordHash.equals(receivedHash)) {
				addToAuthenticationCache(str, new AuthenticationCacheItem(
						userName, TimeStamp.getCurrentTimeStamp()));
				return userName;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}