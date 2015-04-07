package in.rgukt.phoenix.core;

public final class Constants {
	public static final String prefix = System.getProperty("user.home");

	public static final class Server {
		public static final String name = "Phoenix Proxy Server";
		public static int port = 3128;
		public static long credentialsttl = 2 * 60 * 1000;
		public static long credentialsUpdateInterval = 2 * 60 * 1000;
		public static long quotaUpdateInterval = 5 * 60 * 1000;
		public static long maxUserQuota = 200 << 20;
		public static String logFile = prefix + "/.phoenix/phoenix.log";
		public static String credentialsFile = prefix + "/.phoenix/credentials";
		public static String quotaFile = prefix + "/.phoenix/quota";
		public static long maxLogFileRotateSize = 40 << 20;
	}

	public static final class HttpProtocol {
		public static final class ErrorResponses {
			public static byte[] invalidProtocolHtml;
			public static byte[] homePageHtml;
			public static byte[] quotaExceededHtml;
			public static byte[] authenticationRequiredHtml;
		}

		public static final String[] methods = { "GET", "HEAD", "POST",
				"OPTIONS", "PUT", "DELETE", "TRACE", "CONNECT", "PATCH" };
		public static final String name = "HTTP";
		public static final int requestHeadersBufferSize = 1 << 9;
		public static final int responseHeadersBufferSize = 1 << 10;
		public static final int requestBodyBufferSize = 1 << 9;
		public static final int responseBodyBufferSize = 64 << 10;
		public static int streamBufferSize = 64 << 10;
		public static long inMemoryMaxResponseSaveSize = 5 << 20;
		public static long maxCacheSize = 1 << 15;
		public static final String[] defaultAcceptHeaders = new String[] {
				"HTTP/1.1 200 OK", "Server: " + Constants.Server.name,
				"Content-Type: text/html", "Proxy-Connection: keep-alive" };
		public static final String[] defaultAuthenticationHeaders = new String[] {
				"HTTP/1.1 407 Proxy Authentication Required",
				"Server: " + Constants.Server.name, "Content-Type: text/html",
				"Proxy-Connection: keep-alive",
				"Proxy-Authenticate: Basic realm=\"Phoenix Proxy Server\"" };
	}
}