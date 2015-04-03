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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Main {
	private static boolean stopServer = false;
	private static Logger logger;
	static {
		logger = Logger.getLogger(Main.class);
		PropertyConfigurator.configure(ClassLoader.getSystemClassLoader()
				.getResource("log4j.properties"));
	}

	public static void main(String[] args) throws IOException,
			InterruptedException, ExecutionException {
		ServerSocket serverSocket = new ServerSocket(Constants.Server.port);
		initialize();
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6, 100,
				3, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		int count = 0;
		while (!stopServer) {
			if (count > Constants.Server.gcCount) {
				count = 0;
				long prevMemoryFree = Runtime.getRuntime().freeMemory();
				System.gc();
				long currentMemoryFree = Runtime.getRuntime().totalMemory();
				logger.debug("GC freed "
						+ ((currentMemoryFree - prevMemoryFree) / (1 << 20))
						+ " MB");
			}
			Socket client = serverSocket.accept();
			threadPoolExecutor.execute(new ServerThread(client));
			count++;
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