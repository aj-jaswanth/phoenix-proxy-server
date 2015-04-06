package in.rgukt.phoenix.core;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
			4, 10, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	public static void execute(Runnable runnable) {
		threadPoolExecutor.execute(runnable);
	}
}
