package in.rgukt.phoenix.core.access;

import java.util.HashMap;

/**
 * Node in the ACL List Trie. Note: This class is intentionally not well
 * encapsulated due to performance concerns.
 * 
 * @author Venkata Jaswanth
 */

public class AclNode {
	char data;
	AclNode child;
	boolean isJunction;
	HashMap<Character, AclNode> junction;

	/**
	 * Create new node containing the given character
	 * 
	 * @param data
	 *            character the node should hold
	 */
	AclNode(char data) {
		this.data = data;
	}

	/**
	 * Add a new node to the current junction.
	 * 
	 * @param node
	 */
	void addToJunction(AclNode node) {
		if (isJunction == false) {
			isJunction = true;
			junction = new HashMap<Character, AclNode>();
			AclNode t = new AclNode(this.data);
			t.child = this.child;
			junction.put(this.data, t);
			this.data = 'Z';
		}
		junction.put(node.data, node);
	}
}