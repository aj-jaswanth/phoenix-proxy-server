package in.rgukt.phoenix.core.protocols;

import java.io.IOException;
import java.io.OutputStream;

public abstract class ErrorHandler {
	public abstract void sendInvalidProtocolError(OutputStream outputStream)
			throws IOException;

	public abstract void sendHomePage(OutputStream outputStream)
			throws IOException;

	public abstract void sendUnknownHostError(OutputStream outputStream,
			String host) throws IOException;
}