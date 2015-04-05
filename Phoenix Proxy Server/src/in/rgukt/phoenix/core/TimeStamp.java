package in.rgukt.phoenix.core;

public final class TimeStamp {
	private long time;

	public static TimeStamp getCurrentTimeStamp() {
		return new TimeStamp(System.currentTimeMillis());
	}

	public static long getDifference(TimeStamp firstTimeStamp,
			TimeStamp secondTimeStamp) {
		return firstTimeStamp.getTime() - secondTimeStamp.getTime();
	}

	public static long getCurrentDifference(TimeStamp secondTimeStamp) {
		return getCurrentTime() - secondTimeStamp.getTime();
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	private TimeStamp(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}
}