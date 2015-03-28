package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.protocols.ErrorHandler;

import java.io.IOException;
import java.io.OutputStream;

public class HttpErrorHandler extends ErrorHandler {

	@Override
	public void sendInvalidProtocolError(OutputStream outputStream)
			throws IOException {
		HttpHeadersBuilder invalidProtocolHeaders = new HttpHeadersBuilder(
				Constants.HttpProtocol.defaultAcceptHeaders);
		invalidProtocolHeaders
				.addHeader("Content-Length: "
						+ Constants.HttpProtocol.ErrorResponses.invalidProtocolHtml.length);
		outputStream.write(invalidProtocolHeaders.getByteArray());
		outputStream
				.write(Constants.HttpProtocol.ErrorResponses.invalidProtocolHtml);
	}

	@Override
	public void sendHomePage(OutputStream outputStream) throws IOException {
		HttpHeadersBuilder homePageHeaders = new HttpHeadersBuilder(
				Constants.HttpProtocol.defaultAcceptHeaders);
		homePageHeaders.addHeader("Content-Length: "
				+ Constants.HttpProtocol.ErrorResponses.homePageHtml.length);
		outputStream.write(homePageHeaders.getByteArray());
		outputStream.write(Constants.HttpProtocol.ErrorResponses.homePageHtml);
	}

	@Override
	public void sendUnknownHostError(OutputStream outputStream, String host)
			throws IOException {
		HttpHeadersBuilder unknownHostHeaders = new HttpHeadersBuilder(
				Constants.HttpProtocol.defaultAcceptHeaders);
		String unknownHostHtmlString = "<!DOCTYPE html>" + "<html>" + "<head>"
				+ "<title>Phoenix Proxy Server</title>" + "</head>" + "<body>"
				+ "<h1>Unknown Host</h1>" + "<hr>"
				+ "<h3><span style=\"color:blue;\">" + host
				+ "</span> is not resolvable.</h3>" + "</body>" + "</html>";
		byte[] unknownHostHtml = unknownHostHtmlString.getBytes();
		unknownHostHeaders.addHeader("Content-Length: "
				+ unknownHostHtml.length);
		outputStream.write(unknownHostHeaders.getByteArray());
		outputStream.write(unknownHostHtml);
	}
}
