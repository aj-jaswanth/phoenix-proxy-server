package in.rgukt.phoenix.core.database;

import in.rgukt.phoenix.core.Constants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DatabaseAuthenticator {

	private static PreparedStatement authenticationStatement;

	public static String getPassword(String userName) {
		String password = null;
		try {
			if (authenticationStatement == null
					|| authenticationStatement.isClosed())
				authenticationStatement = DatabaseManager.getConnection()
						.prepareStatement(
								Constants.Database.Queries.authenticationQuery);
			authenticationStatement.setString(1, userName);
			ResultSet result = authenticationStatement.executeQuery();

			if (result.next())
				password = result.getString(1);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return password;
	}
}