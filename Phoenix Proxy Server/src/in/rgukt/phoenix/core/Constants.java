package in.rgukt.phoenix.core;

public final class Constants {
	public static final class Server {
		public static final String name = "Phoenix Proxy Server";
		public static final int port = 3128;
		public static final long credentialsttl = 2 * 60 * 60 * 1000;
	}

	public static final class Database {
		public static final String driver = "com.mysql.jdbc.Driver";
		public static final String url = "jdbc:mysql://localhost/proxy";
		public static final String userName = "root";
		public static final String password = "";

		public static final class Queries {
			public static final String authenticationQuery = "SELECT password FROM users WHERE uname=?";
			public static final String loggingQuery = "INSERT INTO log values(?,?,?,?)";
		}
	}

	public static final class HttpProtocol {
		public static final class ErrorResponses {
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
		public static final int inMemoryMaxResponseSaveSize = 5 << 20;
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