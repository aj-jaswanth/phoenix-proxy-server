package in.rgukt.phoenix.core.protocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ApplicationLayerProtocolMessage {

	public abstract String getServer();

	public abstract int getPort();

	public abstract String getValue(String headerKey);

	public abstract float getVersion();

	public abstract String getName();

	public abstract ApplicationLayerProtocolMessage getComplementaryProcessor(
			InputStream inputStream) throws IOException;

	public abstract String getResource();

	public abstract void sendMessage(OutputStream outputStream)
			throws IOException;

	public abstract boolean isAuthorized(OutputStream outputStream)
			throws IOException;
}