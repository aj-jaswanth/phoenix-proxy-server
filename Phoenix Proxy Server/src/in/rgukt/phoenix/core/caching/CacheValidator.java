package in.rgukt.phoenix.core.caching;

import in.rgukt.phoenix.core.TimeStamp;
import in.rgukt.phoenix.core.protocols.http.HttpHeadersBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Validates cache items based on their criteria
 */
public final class CacheValidator {
	/**
	 * Reads the cache item and checks whether it is expired or not according to
	 * max-age value
	 * 
	 * @param cacheItem
	 * @return true if fresh else false
	 */
	public static boolean validateMaxAge(CacheItem cacheItem) {
		if (TimeStamp.getCurrentDifference(cacheItem.getTimeStamp()) < cacheItem
				.getMaxAge())
			return true;
		return false;
	}

	/**
	 * Reads the cache item and checks whether it is valid or not based on ETag
	 * value. It uses If-None-Match header to determine whether the resource is
	 * modified at the server or not.
	 * 
	 * @param resource
	 * @param etag
	 * @return true if valid else false
	 */
	public static boolean validateEtag(String resource, String etag) {
		try {
			String host = resource.substring(7);
			int sindex = host.indexOf("/");
			String[] array = host.substring(0, sindex).split(":");
			resource = host.substring(sindex);
			host = array[0];
			int port = 80;
			if (array.length == 2)
				port = Integer.parseInt(array[1]);

			HttpHeadersBuilder httpHeadersBuilder = new HttpHeadersBuilder(
					new String[] { "HEAD " + resource + " HTTP/1.1" });
			httpHeadersBuilder.addHeader("If-None-Match: " + etag);
			httpHeadersBuilder.addHeader("Host: " + host);

			Socket serverSocket = new Socket(host, port);
			serverSocket.getOutputStream().write(
					httpHeadersBuilder.getByteArray());

			InputStream inputStream = serverSocket.getInputStream();

			byte[] data = new byte[15];
			if (inputStream.read(data, 0, 15) > 0) {
				serverSocket.close();
				int index = 8;
				while (data[index] != ' ')
					index++;
				index++;
				if (data[index] == '3' && data[index + 1] == '0'
						&& data[index + 2] == '4') {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}