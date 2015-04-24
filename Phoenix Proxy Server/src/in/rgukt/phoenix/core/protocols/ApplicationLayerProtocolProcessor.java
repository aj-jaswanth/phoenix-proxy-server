package in.rgukt.phoenix.core.protocols;

import java.io.IOException;

/**
 * Base class for all application layer protocols
 * 
 * @author Venkata Jaswanth
 */
public abstract class ApplicationLayerProtocolProcessor {

	/**
	 * Return server hostname or ip
	 * 
	 * @return hostname or ip
	 */
	public abstract String getServer();

	/**
	 * Return server port
	 * 
	 * @return server port
	 */
	public abstract int getPort();

	/**
	 * Return value of the header
	 * 
	 * @param headerKey
	 * @return header value
	 */
	public abstract String getValue(String headerKey);

	/**
	 * Return version of the protocol
	 * 
	 * @return version
	 */
	public abstract float getVersion();

	/**
	 * Return name of the protocol
	 * 
	 * @return protocol name
	 */
	public abstract String getName();

	/**
	 * Reads and processes the complete message
	 * 
	 * @return data transferred
	 */
	public abstract long processCompleteMessage() throws IOException;

	/**
	 * Return resource requested
	 * 
	 * @return resource
	 */
	public abstract String getResource();

}