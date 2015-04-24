package in.rgukt.phoenix.core;

/**
 * This handles all timestamp related stuff. This holds only milliseconds for
 * identifying timestamps.
 * 
 * @author Venkata Jaswanth
 */
public final class TimeStamp {
	private long time;

	/**
	 * Current TimeStamp factory
	 * 
	 * @return current timestamp
	 */
	public static TimeStamp getCurrentTimeStamp() {
		return new TimeStamp(System.currentTimeMillis());
	}

	/**
	 * Returns difference between two timestamps
	 * 
	 * @param firstTimeStamp
	 * @param secondTimeStamp
	 * @return Difference between two timestamps in milliseconds.
	 */
	public static long getDifference(TimeStamp firstTimeStamp,
			TimeStamp secondTimeStamp) {
		return firstTimeStamp.getTime() - secondTimeStamp.getTime();
	}

	/**
	 * Returns the difference between the given timestamp and the current
	 * timestamp.
	 * 
	 * @param secondTimeStamp
	 * @return number of milliseconds since the given timestamp was created.
	 */
	public static long getCurrentDifference(TimeStamp secondTimeStamp) {
		return getCurrentTime() - secondTimeStamp.getTime();
	}

	/**
	 * Returns the current time in milliseconds.
	 * 
	 * @return time in milliseconds since Jan 1 1970
	 */
	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	private TimeStamp(long time) {
		this.time = time;
	}

	/**
	 * Returns the time when the timestamp was created.
	 * 
	 * @return time in milliseconds
	 */
	public long getTime() {
		return time;
	}
}