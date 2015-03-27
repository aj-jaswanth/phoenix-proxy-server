package in.rgukt.phoenix.core;

import java.io.IOException;
import java.io.OutputStream;

public class ErrorHandler {

	private static String httpHeader = "HTTP/1.1 200 OK\r\nServer: "
			+ Constants.Server.name
			+ "\r\nContent-Type: text/html\r\nConnection: Keep-Alive\r\nContent-Length: ";
	private static byte[] array = httpHeader.getBytes();

	public static void sendInvalidProtocolError(OutputStream outputStream)
			throws IOException {
		outputStream.write(array);
		outputStream
				.write((Constants.ErrorResponses.invalidProtocol.length + "\r\n\r\n")
						.getBytes());
		outputStream.write(Constants.ErrorResponses.invalidProtocol);
	}

	public static void sendHomePage(OutputStream outputStream)
			throws IOException {
		outputStream.write(array);
		outputStream
				.write((Constants.ErrorResponses.proxyServerHomePage.length + "\r\n\r\n")
						.getBytes());
		outputStream.write(Constants.ErrorResponses.proxyServerHomePage);
	}

	public static void sendUnknownHostError(OutputStream outputStream,
			String host) throws IOException {
		String errorHTML = "<!DOCTYPE html>" + "<html>" + "<head>"
				+ "<title>Phoenix Proxy Server</title>" + "</head>" + "<body>"
				+ "<h1>Unknown Host</h1>" + "<hr>"
				+ "<h3><span style=\"color:blue;\">" + host
				+ "</span> is not resolvable.</h3>" + "</body>" + "</html>";
		byte[] errorHTMLArray = errorHTML.getBytes();
		outputStream.write(array);
		outputStream.write((errorHTMLArray.length + "\r\n\r\n").getBytes());
		outputStream.write(errorHTMLArray);
	}
}
