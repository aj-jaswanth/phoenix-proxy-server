package in.rgukt.phoenix.core.protocols.http;

/**
 * Class holds constants for HTTP request processing automata
 * 
 * @author Venkata Jaswanth
 */
public final class HttpRequestStates {
	public static final int initialRequestLine = 1;
	public static final int headerLine = 2;
	public static final int headerLineEnd = 3;
	public static final int headersSectionEnd = 4;
}