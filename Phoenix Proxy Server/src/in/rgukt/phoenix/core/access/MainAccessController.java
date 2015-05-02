package in.rgukt.phoenix.core.access;

import in.rgukt.phoenix.core.Constants;

/**
 * Manages HttpAccessControllers for serveral roles
 * 
 * @author Venkata Jaswanth
 */
public class MainAccessController {
	private static HttpAccessController[] accessControl = new HttpAccessController[Constants.Server.numberOfRoles];

	static {
		for (int x = 0; x < accessControl.length; x++)
			accessControl[x] = new HttpAccessController(x);
	}

	public static void addToAcl(int role, String domainName) {
		accessControl[role].addToAclList(domainName);
	}

	public static void removeFromAcl(int role, String domainName) {
		accessControl[role].removeFromAclList(domainName);
	}

	public static boolean isAllowed(int role, String clientAddress,
			String server, int port, String requestedResource) {
		return accessControl[role].isAllowed(clientAddress, server, port,
				requestedResource);
	}
}