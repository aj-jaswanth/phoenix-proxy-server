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
		public static final int requesetBufferSize = 512;
		public static final int responseBufferSize = 8192;
	}
}