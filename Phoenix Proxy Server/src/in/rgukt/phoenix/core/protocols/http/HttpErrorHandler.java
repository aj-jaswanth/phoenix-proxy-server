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
	private static byte[] invalidProtocolHeadersArray;
	private static byte[] quotaExceededHeadersArray;
	private static byte[] authenticationHeadersArray;
	private static byte[] accessDeniedHeadersArray;

	/**
	 * Sends invalid protocol error
	 * 
	 * @param outputStream
	 */
	public static void sendInvalidProtocolError(OutputStream outputStream)
			throws IOException {
		if (invalidProtocolHeadersArray == null) {
			HttpHeadersBuilder invalidProtocolHeaders = new HttpHeadersBuilder(
					Constants.HttpProtocol.defaultAcceptHeaders);
			invalidProtocolHeaders
					.addHeader("Content-Length: "
							+ Constants.HttpProtocol.ErrorResponses.invalidProtocolHtml.length);
			invalidProtocolHeadersArray = invalidProtocolHeaders.getByteArray();
		}
		outputStream.write(invalidProtocolHeadersArray);
		outputStream
				.write(Constants.HttpProtocol.ErrorResponses.invalidProtocolHtml);
	}

	/**
	 * Sends unknown host error
	 * 
	 * @param outputStream
	 * @param host
	 */
	public static void sendUnknownHostError(OutputStream outputStream,
			String host) throws IOException {
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
	public static void sendQuotaExceeded(OutputStream outputStream)
			throws IOException {
		if (quotaExceededHeadersArray == null) {
			HttpHeadersBuilder quotaExceededHeaders = new HttpHeadersBuilder(
					Constants.HttpProtocol.defaultAcceptHeaders);
			quotaExceededHeaders
					.addHeader("Content-Length: "
							+ Constants.HttpProtocol.ErrorResponses.quotaExceededHtml.length);
			quotaExceededHeadersArray = quotaExceededHeaders.getByteArray();
		}
		outputStream.write(quotaExceededHeadersArray);
		outputStream
				.write(Constants.HttpProtocol.ErrorResponses.quotaExceededHtml);
	}

	/**
	 * Sends authentication error
	 * 
	 * @param outputStream
	 * @throws IOException
	 */
	public static void sendAuthenticationRequired(OutputStream outputStream)
			throws IOException {
		if (authenticationHeadersArray == null) {
			HttpHeadersBuilder authenticationHeaders = new HttpHeadersBuilder(
					Constants.HttpProtocol.defaultAuthenticationHeaders);
			authenticationHeaders
					.addHeader("Content-Length: "
							+ Constants.HttpProtocol.ErrorResponses.authenticationRequiredHtml.length);
			authenticationHeadersArray = authenticationHeaders.getByteArray();
		}
		outputStream.write(authenticationHeadersArray);
		outputStream
				.write(Constants.HttpProtocol.ErrorResponses.authenticationRequiredHtml);
	}

	/**
	 * Sends access denied error
	 * 
	 * @param outputStream
	 */
	public static void sendAccessDenied(OutputStream outputStream)
			throws IOException {
		if (accessDeniedHeadersArray == null) {
			HttpHeadersBuilder accessDeniedHeaders = new HttpHeadersBuilder(
					Constants.HttpProtocol.defaultAcceptHeaders);
			accessDeniedHeaders
					.addHeader("Content-Length: "
							+ Constants.HttpProtocol.ErrorResponses.accessDeniedHtml.length);
			accessDeniedHeadersArray = accessDeniedHeaders.getByteArray();
		}
		outputStream.write(accessDeniedHeadersArray);
		outputStream
				.write(Constants.HttpProtocol.ErrorResponses.accessDeniedHtml);
	}
}
