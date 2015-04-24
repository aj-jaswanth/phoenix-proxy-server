package in.rgukt.phoenix.core.quota;

import in.rgukt.phoenix.Configurator;
import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.TimeStamp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Handles all quota management of users
 * 
 * @author Venkata Jaswanth
 */
public class QuotaManager {

	private static Map<String, Long> quotaMap = new HashMap<String, Long>();
	private static Map<String, Long> quotaLimits = new HashMap<String, Long>();
	private static TimeStamp prevUpdate = TimeStamp.getCurrentTimeStamp();
	private static Calendar resetPoint;

	static {
		try {
			updateQuotaFromFile();
			updateQuotaLimitsFromFile();
			File r = new File(Constants.Server.quotaResetPointFile);
			Scanner s = new Scanner(r);
			resetPoint = Calendar.getInstance();
			if (s.hasNextLong()) {
				long rp = s.nextLong();
				s.close();
				resetPoint.setTime(new Date(rp));
			} else {
				String[] a = Constants.Server.quotaResetPoint.split(":");
				resetPoint.add(Calendar.HOUR_OF_DAY, Integer.parseInt(a[0])
						- resetPoint.get(Calendar.HOUR_OF_DAY));
				resetPoint.add(Calendar.MINUTE, Integer.parseInt(a[1])
						- resetPoint.get(Calendar.MINUTE));
				resetPoint.add(Calendar.SECOND, Integer.parseInt(a[2])
						- resetPoint.get(Calendar.SECOND));
				resetPoint.add(Calendar.DAY_OF_MONTH, 1);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns whether the current length data if downloaded exceeds quota or
	 * not
	 * 
	 * @param userName
	 * @param len
	 * @return true if quota exceeds else false
	 */
	public synchronized static boolean exceedsQuotaLimit(String userName,
			long len) {
		return quotaMap.get(userName) > quotaLimits.get(userName);
	}

	/**
	 * Returns whether the user has exceeded his quota or not
	 * 
	 * @param userName
	 * @return true if quota exceeded else false
	 */
	public synchronized static boolean isQuotaExceeded(String userName) {
		Long quota = quotaMap.get(userName);
		if (quota != null) {
			Long limit = quotaLimits.get(userName);
			if (limit == null)
				return true;
			return quota > limit;
		}
		quotaMap.put(userName, 0L);
		return false;
	}

	/**
	 * Adds quota limit to user
	 * 
	 * @param userName
	 * @param limit
	 */
	public static void addQuotaLimit(String userName, long limit) {
		quotaLimits.put(userName, limit);
	}

	/**
	 * Removes quota limit to user
	 * 
	 * @param userName
	 */
	public static void removeQuotaLimit(String userName) {
		quotaLimits.remove(userName);
	}

	/**
	 * Updates quota limit of user
	 * 
	 * @param userName
	 * @param limit
	 */
	public static void updateQuotaLimit(String userName, long limit) {
		quotaLimits.put(userName, limit);
	}

	private static void dumpQuotaToFile() throws IOException {
		FileWriter writer = new FileWriter(new File(Constants.Server.quotaFile));
		for (String user : quotaMap.keySet())
			writer.write(user + " " + quotaMap.get(user) + "\n");
		writer.close();
	}

	private static void updateQuotaFromFile() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(Constants.Server.quotaFile));
		while (scanner.hasNext())
			quotaMap.put(scanner.next(), scanner.nextLong());
		scanner.close();
	}

	private static void updateQuotaLimitsFromFile()
			throws FileNotFoundException {
		Scanner scanner = new Scanner(
				new File(Constants.Server.quotaLimitsFile));
		while (scanner.hasNext())
			quotaLimits.put(scanner.next(),
					Configurator.parseDataSize(scanner.next()));
		scanner.close();
	}

	/**
	 * Adds number of bytes to the user's quota
	 * 
	 * @param userName
	 * @param dataUsedNow
	 */
	public synchronized static void addQuota(String userName, long dataUsedNow) {
		Long dataAlreadyUsed = quotaMap.get(userName);
		if (dataAlreadyUsed == null)
			dataAlreadyUsed = new Long(0);
		quotaMap.put(userName, dataAlreadyUsed + dataUsedNow);
		if (TimeStamp.getCurrentDifference(prevUpdate) > Constants.Server.quotaDumpInterval) {
			try {
				dumpQuotaToFile();
				prevUpdate = TimeStamp.getCurrentTimeStamp();
				Calendar current = Calendar.getInstance();
				if (current.after(resetPoint)) {
					resetPoint.add(Calendar.DAY_OF_MONTH, 1);
					quotaMap.clear();
					File rFile = new File(Constants.Server.quotasDir
							+ new Date().toString() + "_quota");
					new File(Constants.Server.quotaFile).renameTo(rFile);
					new File(Constants.Server.quotaFile).createNewFile();
					FileWriter writer = new FileWriter(new File(
							Constants.Server.quotaResetPointFile));
					writer.write(resetPoint.getTime().getTime() + "");
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}