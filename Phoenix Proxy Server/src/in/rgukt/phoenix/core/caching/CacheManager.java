package in.rgukt.phoenix.core.caching;

import in.rgukt.phoenix.core.ByteBuffer;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {
	private static Map<String, CacheItem> mainCache = new HashMap<String, CacheItem>();

	public static synchronized CacheItem getFromCache(String resource) {
		CacheItem cacheItem = mainCache.get(resource);
		if (cacheItem != null) {
			Map<String, String> headersMap = cacheItem.getHeadersMap();
			CacheCriteria cacheCriteria = cacheItem.getCacheCriteria();
			if (cacheCriteria == CacheCriteria.ETAG
					&& CacheValidator.validateEtag(resource,
							headersMap.get("ETag")) == false) {
				cacheItem = null;
				mainCache.remove(resource);
			} else if (cacheCriteria == CacheCriteria.MAX_AGE
					&& CacheValidator.validateMaxAge(cacheItem) == false) {
				cacheItem = null;
				mainCache.remove(resource);
			}
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

	private static synchronized void addToCache(String key, CacheItem cacheItem) {
		mainCache.put(key, cacheItem);
	}

}