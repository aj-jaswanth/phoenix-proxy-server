package in.rgukt.phoenix.core.ipc;

import in.rgukt.phoenix.Configurator;
import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.access.HttpAccessController;
import in.rgukt.phoenix.core.authentication.Authenticator;
import in.rgukt.phoenix.core.quota.QuotaManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * This handles all Inter Process Communication between web server handling web
 * interface part of the proxy server and the core proxy server software
 * 
 * @author Venkata Jaswanth
 */
public class IpcServer implements Runnable {
	private static ServerSocket serverSocket;

	/**
	 * Starts the server and processes commands
	 */
	public static void listen() throws IOException {
		serverSocket = new ServerSocket(Constants.IPCServer.port);
		while (true) {
			Socket client = serverSocket.accept();
			Scanner scanner = new Scanner(client.getInputStream());
			String token = scanner.next();
			String cmd = scanner.next();
			if (IpcAuthenticator.isAuthenticated(token))
				processCommand(cmd);
			scanner.close();
			client.close();
		}
	}

	private static void processCommand(String cmd) {
		String[] a = cmd.split(":");
		switch (a[0]) {
		case "A":
			if (a[1].equals("CRD"))
				Authenticator.addUser(a[2], a[3]);
			else if (a[1].equals("ACL"))
				HttpAccessController.addToAclList(a[2]);
			else if (a[1].equals("QTA"))
				QuotaManager.addQuotaLimit(a[2],
						Configurator.parseDataSize((a[3])));
			break;
		case "R":
			if (a[1].equals("CRD"))
				Authenticator.removeUser(a[2]);
			else if (a[1].equals("ACL"))
				HttpAccessController.removeFromAclList(a[2]);
			else if (a[1].equals("QTA"))
				QuotaManager.removeQuotaLimit(a[2]);
			break;
		case "U":
			if (a[1].equals("CRD"))
				Authenticator.updateUser(a[2], a[3]);
			else if (a[1].equals("QTA"))
				QuotaManager.updateQuotaLimit(a[2],
						Configurator.parseDataSize(a[3]));
			break;
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				listen();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}