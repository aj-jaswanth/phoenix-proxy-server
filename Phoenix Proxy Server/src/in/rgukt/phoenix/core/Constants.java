package in.rgukt.phoenix.core;

public class Constants {
	public static class Server {
		public static String name = "Phoenix Proxy Server";
		public static int port = 3128;
		public static long credentialsttl = 2 * 60 * 60 * 1000;
		public static int gcCount = 20;
	}

	public static class HttpProtocol {
		public static class ErrorResponses {
			public static byte[] invalidProtocolHtml;
			public static byte[] homePageHtml;
		}

		public static final String[] methods = { "GET", "HEAD", "POST",
				"OPTIONS", "PUT", "DELETE", "TRACE", "CONNECT", "PATCH" };
		public static final String name = "HTTP";
		public static final int requestHeadersBufferSize = 1 << 9;
		public static final int responseHeadersBufferSize = 1 << 10;
		public static final int requestBodyBufferSize = 1 << 9;
		public static final int responseBodyBufferSize = 64 << 10;
		public static final int streamBufferSize = 64 << 10;
		public static final int inMemoryMaxResponseSaveSize = 120;// 5 << 20;
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