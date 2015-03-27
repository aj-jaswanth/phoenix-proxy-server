package in.rgukt.phoenix.core.protocols.http;

public final class HttpRequestStates {
	public static final int initialLine = 1;
	public static final int headerLine = 2;
	public static final int headerLineEnd = 3;
	public static final int headerSectionEnd = 4;
}