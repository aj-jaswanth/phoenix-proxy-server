package in.rgukt.phoenix.core.database;

import in.rgukt.phoenix.core.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {
	private static Connection databaseConnection;

	public static Connection getConnection() throws ClassNotFoundException,
			SQLException {
		if (databaseConnection == null || databaseConnection.isClosed()) {
			Class.forName(Constants.Database.driver);
			databaseConnection = DriverManager.getConnection(
					Constants.Database.url, Constants.Database.userName,
					Constants.Database.password);
		}
		return databaseConnection;
	}
}