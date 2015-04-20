package in.rgukt.phoenix.core.access;

import java.util.HashMap;

public class AclNode {
	char data;
	AclNode child;
	boolean isJunction;
	HashMap<Character, AclNode> junction;

	public AclNode(char data) {
		this.data = data;
	}

	public void addToJunction(AclNode node) {
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