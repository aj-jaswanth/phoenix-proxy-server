package in.rgukt.phoenix.core.quota;

import in.rgukt.phoenix.core.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class QuotaManager {

	private static Map<String, Long> quotaMap = new HashMap<String, Long>();

	static {
		try {
			updateQuotaFromFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static long getUsedData(String userName) {
		return quotaMap.get(userName);
	}

	public static boolean isQuotaExceeded(String userName) {
		Long quota = quotaMap.get(userName);
		if (quota != null)
			return quota > Constants.Server.maxUserQuota;
		return true;
	}

	private static void updateQuotaFromFile() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(Constants.Server.quotaFile));
		while (scanner.hasNext()) {
			quotaMap.put(scanner.next(), scanner.nextLong());
		}
		scanner.close();
	}

	public static void addQuota(String userName, long dataUsedNow) {
		Long dataAlreadyUsed = quotaMap.get(userName);
		if (dataAlreadyUsed == null)
			dataAlreadyUsed = new Long(0);
		quotaMap.put(userName, dataAlreadyUsed + dataUsedNow);
	}
}