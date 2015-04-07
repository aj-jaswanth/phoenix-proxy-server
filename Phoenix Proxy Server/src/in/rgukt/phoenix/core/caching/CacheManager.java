package in.rgukt.phoenix.core.caching;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class CacheManager {
	private static Map<String, CacheItem> mainCache = new HashMap<String, CacheItem>();
	private static long cacheSize;
	private static long startTime = System.currentTimeMillis();

	public synchronized static CacheItem getFromCache(String resource) {
		CacheItem cacheItem = mainCache.get(resource);
		if (cacheItem != null) {
			Map<String, String> headersMap = cacheItem.getHeadersMap();
			CacheCriteria cacheCriteria = cacheItem.getCacheCriteria();
			if (cacheCriteria == CacheCriteria.ETAG
					&& CacheValidator.validateEtag(resource,
							headersMap.get("ETag")) == false) {
				cacheItem = null;
				removeFromCache(resource, cacheItem);
			} else if (cacheCriteria == CacheCriteria.MAX_AGE
					&& CacheValidator.validateMaxAge(cacheItem) == false) {
				cacheItem = null;
				removeFromCache(resource, cacheItem);
			}
			cacheItem.updateItem();
		}
		return cacheItem;
	}

	public static void inspect(String resource, Map<String, String> headersMap,
			ByteBuffer body) {
		if (headersMap.containsKey("ETag"))
			addToCache(resource, new CacheItem(CacheCriteria.ETAG, headersMap,
					body.getTrimmedBuffer()));
		else {
			String cacheControl = headersMap.get("Cache-Control");
			if (cacheControl != null) {
				Map<String, String> cacheControlMap = parseCacheControl(cacheControl);
				if (cacheControlMap.containsKey("no-store")
						|| cacheControlMap.containsKey("private"))
					return;
				else if (cacheControlMap.containsKey("max-age"))
					addToCache(
							resource,
							new CacheItem(CacheCriteria.MAX_AGE, headersMap,
									body.getTrimmedBuffer(), Integer
											.parseInt(cacheControlMap
													.get("max-age")) * 1000));
			}
		}
	}

	private static Map<String, String> parseCacheControl(String cacheControl) {
		Map<String, String> map = new HashMap<String, String>();
		String[] array = cacheControl.split(","), temp;
		for (String str : array) {
			temp = str.trim().split("=");
			if (temp.length == 2)
				map.put(temp[0], temp[1]);
			else
				map.put(temp[0], null);
		}
		return map;
	}

	private synchronized static void addToCache(String key, CacheItem cacheItem) {
		cacheSize += cacheItem.getSize();
		mainCache.put(key, cacheItem);
		if (cacheSize > Constants.HttpProtocol.maxCacheSize)
			cleanUpCache();
	}

	private static void cleanUpCache() {
		System.out.println("Clean up...");
		ArrayList<Entry<String, CacheItem>> keys = new ArrayList<Entry<String, CacheItem>>();
		long prevSize = cacheSize, cacheLimit = (long) (Constants.HttpProtocol.maxCacheSize * 0.8);
		long currentTime = System.currentTimeMillis();
		long timeElapsed = currentTime - startTime, minAge;
		double timeFraction = 0.2;
		while (cacheSize > cacheLimit) {
			minAge = (long) (timeElapsed * timeFraction);
			for (Entry<String, CacheItem> resource : mainCache.entrySet()) {
				if (resource.getValue().getRecentTimeStamp().getTime()
						- startTime < minAge) {
					keys.add(resource);
					prevSize -= resource.getValue().getSize();
					if (prevSize < cacheLimit)
						break;
				}
			}
			for (Entry<String, CacheItem> resource : keys)
				removeFromCache(resource.getKey(), resource.getValue());
			timeFraction += 0.1;
			keys.clear();
		}
	}

	private static void removeFromCache(String resource, CacheItem cacheItem) {
		cacheSize -= cacheItem.getSize();
		mainCache.remove(resource);
	}
}