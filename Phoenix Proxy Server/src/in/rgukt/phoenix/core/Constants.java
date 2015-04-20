package in.rgukt.phoenix.core;

public final class Constants {
	public static String prefix = System.getProperty("user.home")
			+ "/.phoenix/";

	public static final class Server {
		public static final String name = "Phoenix Proxy Server";
		public static int port = 3128;
		public static long credentialsttl = 2 * 60 * 1000;
		public static long credentialsUpdateInterval = 2 * 60 * 1000;
		public static long quotaDumpInterval = 5 * 60 * 1000;
		public static String quotaResetPoint = "00:00";
		public static String quotasDir = prefix + "/quotas/";
		public static String quotaResetPointFile = prefix
				+ "/quotas/next_reset_point";
		public static long maxUserQuota = 200 << 20;
		public static String logFile = prefix + "/logs/phoenix.log";
		public static String credentialsFile = prefix + "credentials";
		public static String quotaFile = prefix + "/quotas/quota";
		public static long maxLogFileRotateSize = 1 << 20;
		public static long aclUpdateInterval = 5 * 60 * 1000;
	}

	public static final class HttpProtocol {
		public static final class ErrorResponses {
			public static byte[] invalidProtocolHtml;
			public static byte[] homePageHtml;
			public static byte[] quotaExceededHtml;
			public static byte[] authenticationRequiredHtml;
			public static byte[] accessDeniedHtml;
		}

		public static final String[] methods = { "GET", "HEAD", "POST",
				"OPTIONS", "PUT", "DELETE", "TRACE", "CONNECT", "PATCH" };
		public static final String name = "HTTP";
		public static final int requestHeadersBufferSize = 1 << 9;
		public static final int responseHeadersBufferSize = 1 << 10;
		public static final int requestBodyBufferSize = 1 << 9;
		public static final int responseBodyBufferSize = 64 << 10;
		public static int streamBufferSize = 64 << 10;
		public static long maxCacheItemSize = 5 << 20;
		public static long maxCacheSize = 1 << 15;
		public static final String[] defaultAcceptHeaders = new String[] {
				"HTTP/1.1 200 OK", "Server: " + Constants.Server.name,
				"Content-Type: text/html", "Proxy-Connection: keep-alive" };
		public static final String[] defaultAuthenticationHeaders = new String[] {
				"HTTP/1.1 407 Proxy Authentication Required",
				"Server: " + Constants.Server.name, "Content-Type: text/html",
				"Proxy-Connection: keep-alive",
				"Proxy-Authenticate: Basic realm=\"Phoenix Proxy Server\"" };
		public static String aclFile = prefix + "acl";
	}
}