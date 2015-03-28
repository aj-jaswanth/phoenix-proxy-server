package in.rgukt.phoenix;

import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.FileHandler;
import in.rgukt.phoenix.core.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
	private static boolean stopServer = false;

	public static void main(String[] args) throws IOException,
			InterruptedException, ExecutionException {
		ServerSocket serverSocket = new ServerSocket(Constants.Server.port);
		initialize();
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6, 100,
				3, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		while (!stopServer) {
			Socket client = serverSocket.accept();
			threadPoolExecutor.execute(new ServerThread(client));
		}
		serverSocket.close();
	}

	private static void initialize() throws IOException {
		Constants.HttpProtocol.ErrorResponses.invalidProtocolHtml = FileHandler
				.readAsBytes("/home/aj/git_repos/ProxyServer/Phoenix Proxy Server/src/html/InvalidProtocol.html");
		Constants.HttpProtocol.ErrorResponses.homePageHtml = FileHandler
				.readAsBytes("/home/aj/git_repos/ProxyServer/Phoenix Proxy Server/src/html/HomePage.html");
	}
}