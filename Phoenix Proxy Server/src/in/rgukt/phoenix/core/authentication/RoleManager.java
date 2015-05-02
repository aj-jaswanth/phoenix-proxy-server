package in.rgukt.phoenix.core.authentication;

import java.util.HashMap;

/**
 * Manages uses and their roles
 * 
 * @author Venkata Jaswanth
 */
public class RoleManager {
	private static HashMap<String, Integer> roleMap = new HashMap<String, Integer>();

	public static int getRole(String userName) {
		return roleMap.get(userName);
	}

	public static void addRole(String userName, int role) {
		roleMap.put(userName, role);
	}

	public static void removeRole(String userName) {
		roleMap.remove(userName);
	}
}