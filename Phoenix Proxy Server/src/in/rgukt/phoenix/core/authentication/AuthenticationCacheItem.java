package in.rgukt.phoenix.core.authentication;

import in.rgukt.phoenix.core.TimeStamp;

public final class AuthenticationCacheItem {
	private String userName;
	private TimeStamp cachedTimeStamp;

	public AuthenticationCacheItem(String userName, TimeStamp timeStamp) {
		this.userName = userName;
		this.cachedTimeStamp = timeStamp;
	}

	public String getUserName() {
		return userName;
	}

	public TimeStamp getCachedTimeStamp() {
		return cachedTimeStamp;
	}
}