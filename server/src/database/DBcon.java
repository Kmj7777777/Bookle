package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBcon {

	private DBcon() {

	}

	public static Connection getConnection() {
		Connection con = null;

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String loginId = "java01";
			String loginPw = "java01";
			con = DriverManager.getConnection(url, loginId, loginPw);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return con;
	}

}
