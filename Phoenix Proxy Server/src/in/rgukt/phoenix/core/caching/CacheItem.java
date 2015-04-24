package in.rgukt.phoenix.core.caching;

import in.rgukt.phoenix.core.TimeStamp;
import in.rgukt.phoenix.core.protocols.http.HttpHeadersBuilder;

import java.util.Map;

/**
 * Used to hold an item that can be cached. It also keeps details like Criteria,
 * CachedTimeStamp and RecentlyUsedTimeStamp
 */
public final class CacheItem {
	private CacheCriteria cacheCriteria;
	private Map<String, String> headersMap;
	private byte[] body;
	private long maxAge;
	private TimeStamp cachedTimeStamp;
	private int hits;
	private TimeStamp recentTimeStamp;
	private long size;
	private byte[] headersArray;

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

	/**
	 * Return the cache criteria
	 * 
	 * @return cache criteria
	 */
	public CacheCriteria getCacheCriteria() {
		return cacheCriteria;
	}

	/**
	 * Return cached TimeStamp
	 * 
	 * @return time stamp
	 */
	public TimeStamp getTimeStamp() {
		return cachedTimeStamp;
	}

	/**
	 * Return max age
	 * 
	 * @return max age
	 */

	public long getMaxAge() {
		return maxAge;
	}

	/**
	 * Return headers map
	 * 
	 * @return headers map
	 */
	public Map<String, String> getHeadersMap() {
		return headersMap;
	}

	/**
	 * Create a new header having 200 OK response and all other header values.
	 * 
	 * @return byte array containing headers in HTTP format
	 */
	public byte[] getHeaders() {
		if (headersArray == null) {
			HttpHeadersBuilder httpHeadersBuilder = new HttpHeadersBuilder(
					new String[] { "HTTP/1.1 200 OK" });
			httpHeadersBuilder.addAllHeaders(headersMap);
			headersArray = httpHeadersBuilder.getByteArray();
		}
		return headersArray;
	}

	/**
	 * Return body of the cached HTTP response
	 * 
	 * @return byte array
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * Return size of the cached item. Currently only the size of the body is
	 * saved.
	 * 
	 * @return size of the cached item
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Update cached item for every access
	 */
	public void updateItem() {
		recentTimeStamp = TimeStamp.getCurrentTimeStamp();
		hits++;
	}

	/**
	 * Return the number of times the cached item has been served
	 * 
	 * @return number of hits
	 */
	public int getHits() {
		return hits;
	}

	/**
	 * Return the recently accessed time of cached item
	 * 
	 * @return recently accessed time
	 */
	public TimeStamp getRecentTimeStamp() {
		return recentTimeStamp;
	}
}