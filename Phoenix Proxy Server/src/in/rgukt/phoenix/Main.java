package in.rgukt.phoenix;

import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.FileHandler;
import in.rgukt.phoenix.core.ServerThread;
import in.rgukt.phoenix.core.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class Main {
	private static boolean stopServer = false;

	public static void main(String[] args) throws IOException,
			InterruptedException, ExecutionException {
		ServerSocket serverSocket = new ServerSocket(Constants.Server.port);
		initialize();
		while (!stopServer) {
			Socket client = serverSocket.accept();
			ThreadPool.execute(new ServerThread(client));
		}
		serverSocket.close();
	}

	private static void initialize() throws IOException {
		Constants.HttpProtocol.ErrorResponses.invalidProtocolHtml = FileHandler
				.readAsBytes("html/InvalidProtocol.html");
		Constants.HttpProtocol.ErrorResponses.homePageHtml = FileHandler
				.readAsBytes("html/HomePage.html");
	}
}