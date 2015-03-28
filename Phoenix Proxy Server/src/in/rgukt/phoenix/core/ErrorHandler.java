package in.rgukt.phoenix.core;

import in.rgukt.phoenix.core.protocols.http.HttpHeader;

import java.io.IOException;
import java.io.OutputStream;

public class ErrorHandler {

	public static void sendInvalidProtocolError(OutputStream outputStream)
			throws IOException {
		HttpHeader invalidProtocolHeader = new HttpHeader(
				Constants.HttpProtocol.defaultAcceptHeaders);
		invalidProtocolHeader.addHeader("Content-Length: "
				+ Constants.ErrorResponses.invalidProtocol.length);
		outputStream.write(invalidProtocolHeader.getByteArray());
		outputStream.write(Constants.ErrorResponses.invalidProtocol);
	}

	public static void sendHomePage(OutputStream outputStream)
			throws IOException {
		HttpHeader homePageHeader = new HttpHeader(
				Constants.HttpProtocol.defaultAcceptHeaders);
		homePageHeader.addHeader("Content-Length: "
				+ Constants.ErrorResponses.proxyServerHomePage.length);
		outputStream.write(homePageHeader.getByteArray());
		outputStream.write(Constants.ErrorResponses.proxyServerHomePage);
	}

	public static void sendUnknownHostError(OutputStream outputStream,
			String host) throws IOException {
		HttpHeader unknownHostHeader = new HttpHeader(
				Constants.HttpProtocol.defaultAcceptHeaders);
		String errorHTML = "<!DOCTYPE html>" + "<html>" + "<head>"
				+ "<title>Phoenix Proxy Server</title>" + "</head>" + "<body>"
				+ "<h1>Unknown Host</h1>" + "<hr>"
				+ "<h3><span style=\"color:blue;\">" + host
				+ "</span> is not resolvable.</h3>" + "</body>" + "</html>";
		byte[] errorHTMLArray = errorHTML.getBytes();
		unknownHostHeader.addHeader("Content-Length: " + errorHTMLArray.length);
		outputStream.write(unknownHostHeader.getByteArray());
		outputStream.write(errorHTMLArray);
	}
}
