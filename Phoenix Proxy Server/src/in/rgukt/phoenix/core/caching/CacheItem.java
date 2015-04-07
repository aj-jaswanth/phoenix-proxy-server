package in.rgukt.phoenix.core.caching;

import in.rgukt.phoenix.core.TimeStamp;
import in.rgukt.phoenix.core.protocols.http.HttpHeadersBuilder;

import java.util.Map;

public final class CacheItem {
	private CacheCriteria cacheCriteria;
	private Map<String, String> headersMap;
	private byte[] body;
	private long maxAge;
	private TimeStamp cachedTimeStamp;
	private int hits;
	private TimeStamp recentTimeStamp;
	private long size;

	public CacheItem(CacheCriteria cacheCriteria,
			Map<String, String> headersMap, byte[] body) {
		this.cacheCriteria = cacheCriteria;
		this.headersMap = headersMap;
		this.body = body;
		this.size = this.body.length;
		this.cachedTimeStamp = TimeStamp.getCurrentTimeStamp();
		this.recentTimeStamp = this.cachedTimeStamp;
	}

	public CacheItem(CacheCriteria cacheCriteria,
			Map<String, String> headersMap, byte[] body, long maxAge) {
		this(cacheCriteria, headersMap, body);
		this.maxAge = maxAge;
	}

	public CacheCriteria getCacheCriteria() {
		return cacheCriteria;
	}

	public TimeStamp getTimeStamp() {
		return cachedTimeStamp;
	}

	public long getMaxAge() {
		return maxAge;
	}

	public Map<String, String> getHeadersMap() {
		return headersMap;
	}

	public byte[] getHeaders() {
		HttpHeadersBuilder httpHeadersBuilder = new HttpHeadersBuilder(
				new String[] { "HTTP/1.1 200 OK" });
		httpHeadersBuilder.addAllHeaders(headersMap);
		return httpHeadersBuilder.getByteArray();
	}

	public byte[] getBody() {
		return body;
	}

	public long getSize() {
		return size;
	}

	public void updateItem() {
		recentTimeStamp = TimeStamp.getCurrentTimeStamp();
		hits++;
	}

	public int getHits() {
		return hits;
	}

	public TimeStamp getRecentTimeStamp() {
		return recentTimeStamp;
	}
}