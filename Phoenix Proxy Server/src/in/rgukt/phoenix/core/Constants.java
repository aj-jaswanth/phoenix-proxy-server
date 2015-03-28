package in.rgukt.phoenix.core;

public class Constants {
	public static class Server {
		public static String name = "Phoenix Proxy Server";
		public static int port = 3128;
	}

	public static class ErrorResponses {
		public static byte[] invalidProtocol;
		public static byte[] unknownHost;
		public static byte[] proxyServerHomePage;
	}

	public static class HttpProtocol {
		public static final String[] methods = { "GET", "HEAD", "POST",
				"OPTIONS", "PUT", "DELETE", "TRACE", "CONNECT", "PATCH" };
		public static final String name = "HTTP";
		public static final int requestHeaderBufferSize = 512;
		public static final int responseHeaderBufferSize = 1024;
		public static final int requestBodyBufferSize = 512;
		public static final int responseBodyBufferSize = 8192;
		public static final int streamBufferSize = 8192;
		public static final String[] defaultAcceptHeaders = new String[] {
				"HTTP/1.1 200 OK", "Server: " + Constants.Server.name,
				"Content-Type: text/html", "Proxy-Connection: keep-alive" };
		public static final String[] defaultAuthenticationHeaders = new String[] {
				"HTTP/1.1 401 Unauthorized", "Content-Type: text/html",
				"Proxy-Connection: keep-alive",
				"Proxy-Authenticate: Basic realm=\"Phoenix Proxy Server\"" };
	}
}