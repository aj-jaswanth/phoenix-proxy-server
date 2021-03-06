package in.rgukt.phoenix;

import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.FileHandler;
import in.rgukt.phoenix.core.ServerThread;
import in.rgukt.phoenix.core.ipc.IpcServer;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the proxy server.
 * 
 * @author Venkata Jaswanth
 */
public class Main {
	private static boolean stopServer = false;

	/**
	 * Starting point of the proxy server.
	 * 
	 * @param args
	 *            takes the path of the home directory of the proxy server
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException, ExecutionException {
		if (args.length == 1) {
			Constants.prefix = args[0] + File.separator;
			Configurator.configureServer();
		}
		if (Constants.IPCServer.enabled) {
			Thread IpcThread = new Thread(new IpcServer());
			IpcThread.start();
		}
		ServerSocket serverSocket = new ServerSocket(Constants.Server.port);
		initialize();
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 10,
				3, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		while (!stopServer) {
			Socket client = serverSocket.accept();
			threadPoolExecutor.execute(new ServerThread(client));
		}
		serverSocket.close();
	}

	private static void initialize() throws IOException {
		Constants.HttpProtocol.ErrorResponses.invalidProtocolHtml = FileHandler
				.readAsBytes("html/InvalidProtocol.html");
		Constants.HttpProtocol.ErrorResponses.quotaExceededHtml = FileHandler
				.readAsBytes("html/QuotaExceeded.html");
		Constants.HttpProtocol.ErrorResponses.authenticationRequiredHtml = FileHandler
				.readAsBytes("html/AuthenticationRequired.html");
		Constants.HttpProtocol.ErrorResponses.accessDeniedHtml = FileHandler
				.readAsBytes("html/AccessDenied.html");
	}
}