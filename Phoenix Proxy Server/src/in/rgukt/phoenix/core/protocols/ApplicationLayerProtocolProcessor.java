package in.rgukt.phoenix.core.protocols;

import java.io.IOException;

public abstract class ApplicationLayerProtocolProcessor {

	public abstract String getServer();

	public abstract int getPort();

	public abstract String getValue(String headerKey);

	public abstract float getVersion();

	public abstract String getName();

	public abstract long processCompleteMessage() throws IOException;

	public abstract String getResource();

}