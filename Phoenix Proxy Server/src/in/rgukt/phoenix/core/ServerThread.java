package in.rgukt.phoenix.core;

import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolProcessor;
import in.rgukt.phoenix.core.protocols.ProtocolSensor;

import java.io.IOException;
import java.net.Socket;

/**
 * Handles a client request.
 * 
 * @author Venkata Jaswanth
 */
public final class ServerThread implements Runnable {
	private Socket clientSocket;
	private ApplicationLayerProtocolProcessor applicationLayerRequestProcessor;

	/**
	 * @param client
	 *            Client socket it needs to handle
	 */
	public ServerThread(Socket client) {
		this.clientSocket = client;
	}

	/**
	 * Starting point of client request handling.
	 */
	@Override
	public void run() {
		try {
			applicationLayerRequestProcessor = ProtocolSensor
					.sense(clientSocket);
			if (applicationLayerRequestProcessor == null) {
				clientSocket
						.getOutputStream()
						.write(Constants.HttpProtocol.ErrorResponses.invalidProtocolHtml);
				return;
			}
			applicationLayerRequestProcessor.processCompleteMessage();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null)
					clientSocket.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}
}