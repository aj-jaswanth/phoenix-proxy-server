package in.rgukt.phoenix.core.quota;

import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.TimeStamp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class QuotaManager {

	private static Map<String, Long> quotaMap = new HashMap<String, Long>();
	private static TimeStamp prevUpdate = TimeStamp.getCurrentTimeStamp();
	static {
		try {
			updateQuota();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static boolean isQuotaExceeded(String userName) {
		if (TimeStamp.getCurrentDifference(prevUpdate) > Constants.Server.quotaUpdateInterval)
			try {
				updateQuota();
				prevUpdate = TimeStamp.getCurrentTimeStamp();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		Long quota = quotaMap.get(userName);
		if (quota != null)
			return quota > Constants.Server.maxUserQuota;
		return true;
	}

	private static void updateQuota() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(Constants.Server.quotaFile));
		while (scanner.hasNext()) {
			quotaMap.put(scanner.next(), scanner.nextLong());
		}
		scanner.close();
	}
}