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
		ServerSocket proxyServerSocket = new ServerSocket(Constants.Server.port);
		initializeProxyServer();
		ThreadPoolExecutor tp = new ThreadPoolExecutor(6, 100, 3,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		while (!stopServer) {
			Socket client = proxyServerSocket.accept();
			tp.execute(new ServerThread(client));
		}
		proxyServerSocket.close();
	}

	private static void initializeProxyServer() throws IOException {
		Constants.ErrorResponses.invalidProtocol = FileHandler
				.readBytes("/home/aj/git_repos/ProxyServer/Phoenix Proxy Server/src/html/InvalidProtocol.html");
		Constants.ErrorResponses.proxyServerHomePage = FileHandler
				.readBytes("/home/aj/git_repos/ProxyServer/Phoenix Proxy Server/src/html/HomePage.html");
	}
}