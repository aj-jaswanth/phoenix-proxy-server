package in.rgukt.phoenix.core.protocols.http;

public class InfoItem {

	String requestedResource;
	String userName;

	public InfoItem(String requestedResource, String userName) {
		this.requestedResource = requestedResource;
		this.userName = userName;
	}

	public String getRequestedResource() {
		return requestedResource;
	}

	public String getUserName() {
		return userName;
	}
}