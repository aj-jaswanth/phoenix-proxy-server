package in.rgukt.phoenix.core.logging;

import in.rgukt.phoenix.core.Constants;
import in.rgukt.phoenix.core.database.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Queue;

class LogItem {
	String a, b;
	boolean c;
	Timestamp d;

	LogItem(String a, String b, boolean c, Timestamp t) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = t;
	}
}

public final class DatabaseLogger {

	private static PreparedStatement loggerStatement;

	// private static int count = 0;
	private static Queue<LogItem> queue = new LinkedList<LogItem>();

	public synchronized static void log(String userName, String url,
			boolean cacheHit) {
		if (userName == null)
			return;
		queue.offer(new LogItem(userName, url, cacheHit, new Timestamp(System
				.currentTimeMillis())));
		if (queue.size() < 50)
			return;
		try {
			if (loggerStatement == null || loggerStatement.isClosed())
				loggerStatement = DatabaseManager.getConnection()
						.prepareStatement(
								Constants.Database.Queries.loggingQuery);
			for (LogItem item : queue) {
				loggerStatement.setTimestamp(1, item.d);
				loggerStatement.setString(2, item.a);
				loggerStatement.setString(3, item.b);
				loggerStatement.setBoolean(4, item.c);
				loggerStatement.addBatch();
			}
			loggerStatement.executeBatch();
			queue.clear();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
