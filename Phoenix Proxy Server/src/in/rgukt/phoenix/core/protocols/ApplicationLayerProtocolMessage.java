package in.rgukt.phoenix.core.protocols;

import in.rgukt.phoenix.core.ByteBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ApplicationLayerProtocolMessage {

	public abstract String getServer();

	public abstract int getPort();

	public abstract String getValue(String headerKey);

	public abstract float getVersion();

	public abstract String getName();

	public abstract ByteBuffer getMessage();

	public abstract ApplicationLayerProtocolMessage getComplementaryObject(
			InputStream inputStream) throws IOException;

	public abstract String getResource();

	public abstract void sendMessageToServer(OutputStream outputStream);

}