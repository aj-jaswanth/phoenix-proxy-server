package in.rgukt.phoenix.core.caching;

import in.rgukt.phoenix.core.TimeStamp;
import in.rgukt.phoenix.core.protocols.http.HttpHeadersBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class CacheValidator {

	public static boolean validateMaxAge(CacheItem cacheItem) {
		if (TimeStamp.getCurrentDifference(cacheItem.getTimeStamp()) < cacheItem
				.getMaxAge())
			return true;
		return false;
	}

	public static boolean validateEtag(String resource, String etag) {
		try {
			HttpHeadersBuilder httpHeadersBuilder = new HttpHeadersBuilder(
					new String[] { "HEAD " + resource + " HTTP/1.1" });
			String host = parseHost(resource);
			httpHeadersBuilder.addHeader("Host: " + host);
			httpHeadersBuilder.addHeader("If-None-Match: " + etag);

			Socket serverSocket = connectToServer(host);
			serverSocket.getOutputStream().write(
					httpHeadersBuilder.getByteArray());

			InputStream inputStream = serverSocket.getInputStream();

			byte[] data = new byte[30];
			if (inputStream.read(data, 0, 30) > 0) {
				int index = 0;
				while (index < 30 && data[index] != ' ')
					index++;
				index++;
				if (30 - index >= 3) {
					if (data[index] == '3' && data[index + 1] == '0'
							&& data[index + 2] == '4') {
						return true;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static Socket connectToServer(String host) throws IOException {
		String[] array = host.split(":");
		String address = array[0];
		int port = array.length == 2 ? Integer.parseInt(array[1]) : 80;
		Socket serverSocket = new Socket(address, port);
		return serverSocket;
	}

	private static String parseHost(String host) {
		host = host.substring(7);
		host = host.substring(0, host.indexOf("/"));
		return host;
	}
}