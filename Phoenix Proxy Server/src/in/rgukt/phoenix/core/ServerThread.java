package in.rgukt.phoenix.core;

import in.rgukt.phoenix.core.protocols.ApplicationLayerProtocolMessage;
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
	private boolean stopServerThread;
	private ApplicationLayerProtocolMessage applicationLayerRequest;
	private ApplicationLayerProtocolMessage applicationLayerResponse;

	public ServerThread(Socket client) {
		this.client = client;
		stopServerThread = false;
	}

	@Override
	public void run() {
		// System.out.println(Thread.currentThread().getName());
		try {
			clientInputStream = client.getInputStream();
			clientOutputStream = client.getOutputStream();

			while (!stopServerThread) {
				applicationLayerRequest = ProtocolSensor
						.sense(clientInputStream);
				if (applicationLayerRequest == null) {
					if (!client.isClosed())
						ErrorHandler
								.sendInvalidProtocolError(clientOutputStream);
					return;
				}
				System.out.println(Thread.currentThread().getName() + " "
						+ applicationLayerRequest.getResource());

				// Defense against denial-of-service class attack.
				// Otherwise proxy server recursively connects to itself
				// draining resources.
				if (client.getInetAddress().isLoopbackAddress()
						&& (applicationLayerRequest.getPort() == Constants.Server.port)) {
					ErrorHandler.sendHomePage(clientOutputStream);
					return;
				}

				if (connectToServer(applicationLayerRequest.getServer(),
						applicationLayerRequest.getPort()) == false)
					return;
				// TODO: Remove Proxy headers
				// TODO: Header injection for authorization
				applicationLayerRequest.sendMessageToServer(serverOutputStream);
				// serverOutputStream.write(applicationLayerRequest.getMessage()
				// .getBuffer(), 0, applicationLayerRequest.getMessage()
				// .getPosition());

				applicationLayerResponse = applicationLayerRequest
						.getComplementaryObject(serverInputStream);

				// clientOutputStream.write(applicationLayerResponse.getMessage()
				// .getBuffer(), 0, applicationLayerResponse.getMessage()
				// .getPosition());

				if (applicationLayerResponse.getValue("Connection").equals(
						"close")
						|| applicationLayerRequest.getValue("Connection")
								.equals("close"))
					return;
				return; // TODO: Prevent CPU hog. Pipelining avoided.
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			stopServerThread = true;
			try {
				if (client != null && client.isConnected())
					client.close();
				if (server != null && server.isConnected())
					server.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

	private boolean connectToServer(String targetServer, int port)
			throws IOException {
		if (server == null || server.isClosed()) {
			try {
				server = new Socket(targetServer, port);
				serverOutputStream = server.getOutputStream();
				serverInputStream = server.getInputStream();
			} catch (UnknownHostException e) {
				ErrorHandler.sendUnknownHostError(clientOutputStream,
						applicationLayerRequest.getServer());
				return false;
			}
		}
		return true;
	}
}