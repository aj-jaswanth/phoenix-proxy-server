package in.rgukt.phoenix.core.access;

import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.TimeStamp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class HttpAccessController {

	private static TimeStamp prevUpdate = TimeStamp.getCurrentTimeStamp();
	private static ArrayList<String> aclList = new ArrayList<String>();

	static {
		try {
			updateAclList();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static boolean isAllowed(String clientAddress, String server,
			int port, String requestedResource) {
		if (port != 80)
			return false;
		if (TimeStamp.getCurrentDifference(prevUpdate) > Constants.Server.aclUpdateInterval) {
			prevUpdate = TimeStamp.getCurrentTimeStamp();
			try {
				updateAclList();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return isInAclList(server + requestedResource);
	}

	private synchronized static void updateAclList()
			throws FileNotFoundException {
		aclList.clear();
		File aclFile = new File(Constants.HttpProtocol.aclFile);
		Scanner scanner = new Scanner(aclFile);
		while (scanner.hasNext())
			addToAclList(scanner.next());
		scanner.close();
	}

	private static void addToAclList(String str) {
		aclList.add(str);
	}

	private static boolean isInAclList(String res) {
		for (String str : aclList)
			if (res.matches(str))
				return true;
		return false;
	}
}