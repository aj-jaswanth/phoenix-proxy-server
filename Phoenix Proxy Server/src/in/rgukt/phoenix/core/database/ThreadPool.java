package in.rgukt.phoenix.core.database;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
			1, 3, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	public static void execute(Runnable runnable) {
		threadPoolExecutor.execute(runnable);
	}
}
