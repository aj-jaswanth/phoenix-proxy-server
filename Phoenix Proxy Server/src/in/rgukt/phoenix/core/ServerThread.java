package in.rgukt.phoenix.core;

import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolProcessor;
import in.rgukt.phoenix.core.protocols.ProtocolSensor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerThread implements Runnable {
	private Socket client;
	private Socket server;
	private InputStream clientInputStream;
	private OutputStream clientOutputStream;
	private InputStream serverInputStream;
	private OutputStream serverOutputStream;
	private boolean stopServer;
	private ApplicationLayerProtocolProcessor applicationLayerRequestProcessor;
	private ApplicationLayerProtocolProcessor applicationLayerResponseProcessor;

	public ServerThread(Socket client) {
		this.client = client;
		stopServer = false;
	}

	@Override
	public void run() {
		try {
			clientInputStream = client.getInputStream();
			clientOutputStream = client.getOutputStream();

			while (!stopServer) {
				applicationLayerRequestProcessor = ProtocolSensor
						.sense(clientInputStream);
				if (applicationLayerRequestProcessor == null) // TODO: nothing
					return;
				applicationLayerRequestProcessor.processMessage();

				// Defense against denial-of-service class attack.
				// Otherwise proxy server recursively connects to itself
				// draining resources.
				if (client.getInetAddress().isLoopbackAddress()
						&& (applicationLayerRequestProcessor.getPort() == Constants.Server.port)) {
					applicationLayerRequestProcessor.errorHandler
							.sendHomePage(clientOutputStream);
					return;
				}

				System.out.println(Thread.currentThread().getName() + " "
						+ applicationLayerRequestProcessor.getResource());

				if (applicationLayerRequestProcessor
						.isAuthorized(clientOutputStream) == false)
					return;

				if (connectToServer(
						applicationLayerRequestProcessor.getServer(),
						applicationLayerRequestProcessor.getPort()) == false)
					return;

				applicationLayerRequestProcessor
						.sendMessage(serverOutputStream);

				applicationLayerResponseProcessor = applicationLayerRequestProcessor
						.getComplementaryProcessor(serverInputStream);
				applicationLayerResponseProcessor.processMessage();

				applicationLayerResponseProcessor
						.sendMessage(clientOutputStream);

				// if (applicationLayerResponse.getValue("Connection").equals(
				// "close")
				// || applicationLayerRequest.getValue("Connection")
				// .equals("close"))
				// return;
				return; // TODO: Prevent CPU hog. Pipelining avoided.
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			stopServer = true;
			try {
				if (client != null && !client.isClosed())
					client.close();
				if (server != null && !server.isClosed())
					server.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

	private boolean connectToServer(String serverAddress, int port)
			throws IOException {
		if (server == null || server.isClosed()) {
			try {
				server = new Socket(serverAddress, port);
				serverOutputStream = server.getOutputStream();
				serverInputStream = server.getInputStream();
			} catch (UnknownHostException e) {
				applicationLayerRequestProcessor.errorHandler
						.sendUnknownHostError(clientOutputStream,
								applicationLayerRequestProcessor.getServer());
				return false;
			}
		}
		return true;
	}
}