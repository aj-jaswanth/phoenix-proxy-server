package in.rgukt.phoenix.core;

import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolProcessor;
import in.rgukt.phoenix.core.protocols.ProtocolSensor;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

public class ServerThread implements Runnable {
	private Socket clientSocket;
	private ApplicationLayerProtocolProcessor applicationLayerRequestProcessor;
	private static Logger logger;
	static {
		logger = Logger.getLogger(ServerThread.class);
	}

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

			logger.debug(Thread.currentThread().getName() + " "
					+ applicationLayerRequestProcessor.getResource());

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