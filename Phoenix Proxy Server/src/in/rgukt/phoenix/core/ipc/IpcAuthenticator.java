package in.rgukt.phoenix.core.ipc;

/**
 * Authenticator for IPC messages
 * 
 * @author Venkata Jaswanth
 */
public class IpcAuthenticator {

	public static boolean isAuthenticated(String token) {
		return true;
	}
}