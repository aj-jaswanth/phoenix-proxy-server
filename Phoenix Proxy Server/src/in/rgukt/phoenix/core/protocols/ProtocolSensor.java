package in.rgukt.phoenix.core.protocols;

import in.rgukt.phoenix.core.ByteBuffer;
import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.protocols.http.HttpRequestProcessor;

import java.io.IOException;
import java.net.Socket;

/**
 * This class tries to find the application layer protocol the client is
 * speaking.
 * 
 * @author Venkata Jaswanth
 */
public final class ProtocolSensor {
	/**
	 * Finds the application layer protocol by reading few bytes
	 * 
	 * @param clientSocket
	 * @return application layer protocol
	 */
	public static ApplicationLayerProtocolProcessor sense(Socket clientSocket)
			throws IOException {
		ByteBuffer message = new ByteBuffer(
				Constants.HttpProtocol.requestHeadersBufferSize);
		BufferedStreamReader bufferedStreamReader = new BufferedStreamReader(
				clientSocket.getInputStream(),
				Constants.HttpProtocol.streamBufferSize);
		int state = 0, capacity = message.getCapacity();
		for (int x = 0; x < capacity; x++) {
			byte b = bufferedStreamReader.read();
			if (b == -1)
				break;
			message.put(b);
			switch (state) {
			case 0:
				if (b == 'G' || b == 'P' || b == 'H' || b == 'O' || b == 'D'
						|| b == 'T')
					state = 1;
				else
					// Almost confirm it is CONNECT. Tunneling proxy.
					return null;
				break;
			case 1:
				if (b == 'E' || b == 'O' || b == 'P' || b == 'E' || b == 'R')
					return new HttpRequestProcessor(clientSocket, message,
							bufferedStreamReader);
				break;
			}
		}
		return null;
	}
}