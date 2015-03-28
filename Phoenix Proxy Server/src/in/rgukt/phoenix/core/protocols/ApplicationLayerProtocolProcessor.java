package in.rgukt.phoenix.core.protocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ApplicationLayerProtocolProcessor {

	public ErrorHandler errorHandler;

	public abstract String getServer();

	public abstract int getPort();

	public abstract String getValue(String headerKey);

	public abstract float getVersion();

	public abstract String getName();

	public abstract void processMessage() throws IOException;

	public abstract ApplicationLayerProtocolProcessor getComplementaryProcessor(
			InputStream inputStream) throws IOException;

	public abstract String getResource();

	public abstract void sendMessage(OutputStream outputStream)
			throws IOException;

	public abstract boolean isAuthorized(OutputStream outputStream)
			throws IOException;
}