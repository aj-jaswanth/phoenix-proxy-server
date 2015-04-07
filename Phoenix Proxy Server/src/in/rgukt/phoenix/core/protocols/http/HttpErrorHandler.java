package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.Constants;

import java.io.IOException;
import java.io.OutputStream;

public final class HttpErrorHandler {

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

	public void sendHomePage(OutputStream outputStream) throws IOException {
		HttpHeadersBuilder homePageHeaders = new HttpHeadersBuilder(
				Constants.HttpProtocol.defaultAcceptHeaders);
		homePageHeaders.addHeader("Content-Length: "
				+ Constants.HttpProtocol.ErrorResponses.homePageHtml.length);
		outputStream.write(homePageHeaders.getByteArray());
		outputStream.write(Constants.HttpProtocol.ErrorResponses.homePageHtml);
	}

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

	public void sendQuotaExceeded(OutputStream outputStream) throws IOException {
		HttpHeadersBuilder quotaExceededHeaders = new HttpHeadersBuilder(
				Constants.HttpProtocol.defaultAcceptHeaders);
		quotaExceededHeaders
				.addHeader("Content-Length: "
						+ Constants.HttpProtocol.ErrorResponses.quotaExceededHtml.length);
		outputStream.write(quotaExceededHeaders.getByteArray());
		outputStream
				.write(Constants.HttpProtocol.ErrorResponses.quotaExceededHtml);
	}

	public void sendAuthenticationRequired(OutputStream outputStream)
			throws IOException {
		HttpHeadersBuilder authenticationHeaders = new HttpHeadersBuilder(
				Constants.HttpProtocol.defaultAuthenticationHeaders);
		authenticationHeaders
				.addHeader("Content-Length: "
						+ Constants.HttpProtocol.ErrorResponses.authenticationRequiredHtml.length);
		outputStream.write(authenticationHeaders.getByteArray());
		outputStream
				.write(Constants.HttpProtocol.ErrorResponses.authenticationRequiredHtml);
	}
}
