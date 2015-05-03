package in.rgukt.phoenix.core;

/**
 * This class is like the database of the entire proxy server. It holds all
 * configuration details and internal parameters for the proxy server.
 * 
 * @author Venkata Jaswanth
 */
public final class Constants {
	/**
	 * Path for the proxy server's home folder.
	 */
	public static String prefix = System.getProperty("user.home")
			+ "/.phoenix/";

	/**
	 * This class holds IPCServer details.
	 * 
	 * @author Venkata Jaswanth
	 */
	public static final class IPCServer {
		public static boolean enabled = false;
		public static int port = 9090;
	}

	/**
	 * This class holds details about proxy server's file paths and other
	 * parameters
	 * 
	 * @author Venkata Jaswanth
	 */
	public static final class Server {
		public static final String name = "Phoenix Proxy Server";
		public static int port = 3128;
		public static int maxConcurrentThreads = 4;
		public static int numberOfRoles = 1;
		public static long quotaDumpInterval = 5 * 60 * 1000;
		public static String quotaResetPoint = "00:00";
		public static String quotasDir = prefix + "/quotas/";
		public static String quotaResetPointFile = prefix
				+ "/quotas/next_reset_point";
		public static String logFile = prefix + "/logs/phoenix.log";
		public static String credentialsFile = prefix + "credentials";
		public static String quotaFile = prefix + "/quotas/quota";
		public static String quotaLimitsFile = prefix + "/quotaLimits";
		public static long maxLogFileRotateSize = 1 << 20;
	}

	/**
	 * This class holds all configuration of HTTP Protocol.
	 */
	public static final class HttpProtocol {

		/**
		 * Holds all error responses HTML documents.
		 */
		public static final class ErrorResponses {
			public static byte[] invalidProtocolHtml;
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
		public static long maxCacheSize = 200 << 20;
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