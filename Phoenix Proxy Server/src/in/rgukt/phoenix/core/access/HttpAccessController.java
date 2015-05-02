package in.rgukt.phoenix.core.access;

import in.rgukt.phoenix.core.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class handles all access control mechanisms. It currently supports
 * domain based access control only. It supports regular expression * in domain
 * names to indicate anything. eg., www.google.com, *.google.com. This uses a
 * Trie based algorithm to support decision making and allowing regular
 * expressions. This is faster than a HashTable.
 * 
 * @author Venkata Jaswanth
 */

public class HttpAccessController {

	private AclNode root;
	private int role;

	public HttpAccessController(int role) {
		this.role = role;
		try {
			updateAclList();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Determines whether the current request is allowed or not.
	 * 
	 * @param clientAddress
	 * @param server
	 * @param port
	 * @param requestedResource
	 * @return true if allowed or false if denied
	 */
	public boolean isAllowed(String clientAddress, String server, int port,
			String requestedResource) {
		if (port != 80)
			return false;
		return isInAclList(server);
	}

	private synchronized void updateAclList() throws FileNotFoundException {
		root = null;
		File aclFile = new File(Constants.HttpProtocol.aclFile + "_" + role);
		Scanner scanner = new Scanner(aclFile);
		while (scanner.hasNext())
			addToAclList(scanner.next());
		scanner.close();
	}

	/**
	 * Adds the given domain to the list of allowed sites.
	 * 
	 * @param domainName
	 *            domain name
	 */
	public void addToAclList(String domainName) {
		int x = domainName.length() - 1;
		AclNode temp;
		if (root == null) {
			root = new AclNode(domainName.charAt(x--));
			temp = root;
		} else if (root.data == domainName.charAt(x)) {
			x--;
			temp = root;
		} else if (root.data == '*')
			return;
		else {
			AclNode node = null;
			char c = domainName.charAt(x--);
			if (root.isJunction)
				node = root.junction.get(c);
			if (node == null) {
				node = new AclNode(c);
				root.addToJunction(node);
			}
			temp = node;
		}

		for (; x >= 0; x--) {
			char c = domainName.charAt(x);
			if (temp.child == null) {
				temp.child = new AclNode(c);
				temp = temp.child;
			} else if (temp.child.data == c) {
				temp = temp.child;
				continue;
			} else if (temp.child.data == '*')
				return;
			else {
				AclNode n = new AclNode(c);
				temp.child.addToJunction(n);
				temp = n;
			}
		}
	}

	/**
	 * Removes the given domain from the allowed domains list
	 * 
	 * @param domainName
	 *            Domain to be removed
	 */
	public void removeFromAclList(String domainName) {
		if (root == null)
			return;
		AclNode temp = root;
		int x = domainName.length() - 1;
		AclNode lastJunction = null;
		char lastChar = 0;
		for (; x >= 0; x--) {
			char c = domainName.charAt(x);
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

	private boolean isInAclList(String domainName) {
		if (root == null)
			return false;
		AclNode temp = root;
		int x = domainName.length() - 1;
		for (; x >= 0; x--) {
			char c = domainName.charAt(x);
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