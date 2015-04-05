package in.rgukt.phoenix.core;

import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolProcessor;
import in.rgukt.phoenix.core.protocols.ProtocolSensor;

import java.io.IOException;
import java.net.Socket;

public final class ServerThread implements Runnable {
	private Socket clientSocket;
	private ApplicationLayerProtocolProcessor applicationLayerRequestProcessor;

	public ServerThread(Socket client) {
		this.clientSocket = client;
	}

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

			// System.out.println(Thread.currentThread().getName() + " "
			// + applicationLayerRequestProcessor.getResource());

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