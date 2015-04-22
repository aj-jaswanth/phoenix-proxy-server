package in.rgukt.phoenix.core.access;

import in.rgukt.phoenix.core.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HttpAccessController {

	private static AclNode root;

	static {
		try {
			updateAclList();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static boolean isAllowed(String clientAddress, String server,
			int port, String requestedResource) {
		if (port != 80)
			return false;
		return isInAclList(server);
	}

	private synchronized static void updateAclList()
			throws FileNotFoundException {
		root = null;
		File aclFile = new File(Constants.HttpProtocol.aclFile);
		Scanner scanner = new Scanner(aclFile);
		while (scanner.hasNext())
			addToAclList(scanner.next());
		scanner.close();
	}

	public static void addToAclList(String str) {
		int x = str.length() - 1;
		AclNode temp;
		if (root == null) {
			root = new AclNode(str.charAt(x--));
			temp = root;
		} else if (root.data == str.charAt(x)) {
			x--;
			temp = root;
		} else {
			AclNode n = null;
			char c = str.charAt(x--);
			if (root.isJunction)
				n = root.junction.get(c);
			if (n == null) {
				n = new AclNode(c);
				root.addToJunction(n);
			}
			temp = n;
		}

		for (; x >= 0; x--) {
			char c = str.charAt(x);
			if (temp.child == null) {
				temp.child = new AclNode(c);
				temp = temp.child;
			} else if (temp.child.data == c) {
				temp = temp.child;
				continue;
			} else {
				AclNode n = new AclNode(c);
				temp.child.addToJunction(n);
				temp = n;
			}
		}
	}

	public static void removeFromAclList(String res) {
		if (root == null)
			return;
		AclNode temp = root;
		int x = res.length() - 1;
		AclNode lastJunction = null;
		char lastChar = 0;
		for (; x >= 0; x--) {
			char c = res.charAt(x);
			if (temp.isJunction) {
				lastJunction = temp;
				temp = temp.junction.get(c);
				lastChar = c;
			}
			if (temp == null)
				break;
			else if (temp.data == c) {
				temp = temp.child;
				continue;
			} else
				break;
		}
		if (x == -1 && temp == null) {
			if (lastJunction != null)
				lastJunction.junction.remove(lastChar);
			else
				root = null;
		}
	}

	private static boolean isInAclList(String res) {
		if (root == null)
			return false;
		AclNode temp = root;
		int x = res.length() - 1;
		for (; x >= 0; x--) {
			char c = res.charAt(x);
			if (temp.isJunction)
				temp = temp.junction.get(c);
			if (temp == null)
				break;
			else if (temp.data == c) {
				temp = temp.child;
				continue;
			} else if (temp.data == '*')
				return true;
			else
				break;
		}
		return x == -1 && temp == null;
	}
}