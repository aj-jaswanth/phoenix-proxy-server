package in.rgukt.phoenix.core.protocols.http;

import in.rgukt.phoenix.core.Constants;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Contains methods to send error responses
 * 
 * @author Venkata Jaswanth
 */
public final class HttpErrorHandler {
	private byte[] invalidProtocolHeaders;
	private byte[] quotaExceededHeaders;
	private byte[] authenticationHeaders;
	private byte[] accessDeniedHeaders;

	/**
	 * Sends invalid protocol error
	 * 
	 * @param outputStream
	 */
	public void sendInvalidProtocolError(OutputStream outputStream)
			throws IOException {
		if (invalidProtocolHeaders == null) {
			HttpHeadersBuilder invalidProtocolHeaders = new HttpHeadersBuilder(
					Constants.HttpProtocol.defaultAcceptHeaders);
			invalidProtocolHeaders
					.addHeader("Content-Length: "
							+ Constants.HttpProtocol.ErrorResponses.invalidProtocolHtml.length);
			this.invalidProtocolHeaders = invalidProtocolHeaders.getByteArray();
		}
		outputStream.write(invalidProtocolHeaders);
		outputStream
				.write(Constants.HttpProtocol.ErrorResponses.invalidProtocolHtml);
	}

	/**
	 * Sends unknown host error
	 * 
	 * @param outputStream
	 * @param host
	 */
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

	/**
	 * Sends quota exceeded error
	 * 
	 * @param outputStream
	 */
	public void sendQuotaExceeded(OutputStream outputStream) throws IOException {
		if (quotaExceededHeaders == null) {
			HttpHeadersBuilder quotaExceededHeaders = new HttpHeadersBuilder(
					Constants.HttpProtocol.defaultAcceptHeaders);
			quotaExceededHeaders
					.addHeader("Content-Length: "
							+ Constants.HttpProtocol.ErrorResponses.quotaExceededHtml.length);
			this.quotaExceededHeaders = quotaExceededHeaders.getByteArray();
		}
		outputStream.write(quotaExceededHeaders);
		outputStream
				.write(Constants.HttpProtocol.ErrorResponses.quotaExceededHtml);
	}

	/**
	 * Sends authentication error
	 * 
	 * @param outputStream
	 * @throws IOException
	 */
	public void sendAuthenticationRequired(OutputStream outputStream)
			throws IOException {
		if (authenticationHeaders == null) {
			HttpHeadersBuilder authenticationHeaders = new HttpHeadersBuilder(
					Constants.HttpProtocol.defaultAuthenticationHeaders);
			authenticationHeaders
					.addHeader("Content-Length: "
							+ Constants.HttpProtocol.ErrorResponses.authenticationRequiredHtml.length);
			this.authenticationHeaders = authenticationHeaders.getByteArray();
		}
		outputStream.write(authenticationHeaders);
		outputStream
				.write(Constants.HttpProtocol.ErrorResponses.authenticationRequiredHtml);
	}

	/**
	 * Sends access denied error
	 * 
	 * @param outputStream
	 */
	public void sendAccessDenied(OutputStream outputStream) throws IOException {
		if (accessDeniedHeaders == null) {
			HttpHeadersBuilder accessDeniedHeaders = new HttpHeadersBuilder(
					Constants.HttpProtocol.defaultAcceptHeaders);
			accessDeniedHeaders
					.addHeader("Content-Length: "
							+ Constants.HttpProtocol.ErrorResponses.accessDeniedHtml.length);
			this.accessDeniedHeaders = accessDeniedHeaders.getByteArray();
		}
		outputStream.write(accessDeniedHeaders);
		outputStream
				.write(Constants.HttpProtocol.ErrorResponses.accessDeniedHtml);
	}
}
