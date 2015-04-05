package in.rgukt.phoenix.core.database;

import in.rgukt.phoenix.core.Constants;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public final class DatabaseLogger {

	private static PreparedStatement loggerStatement;
	private static int count = 0;

	public static void log(String userName, String url) {
		try {
			if (loggerStatement == null || loggerStatement.isClosed())
				loggerStatement = DatabaseManager.getConnection()
						.prepareStatement(
								Constants.Database.Queries.loggingQuery);
			synchronized (DatabaseLogger.class) {
				if (count < 50) {
					loggerStatement.setTimestamp(1,
							new Timestamp(System.currentTimeMillis()));
					loggerStatement.setString(2, userName);
					loggerStatement.setString(3, url);
					loggerStatement.addBatch();
				} else {
					ThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							try {
								loggerStatement.executeBatch();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					});
					count = 0;
				}
			}
			count++;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
