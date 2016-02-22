package cn.cuilan.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.cuilan.jdbcpool.JdbcPool;

/**
 * JDBC 工具类
 * 
 * @author 张岩
 *
 */
public final class JdbcUtils {

	private static JdbcPool jdbcPool = new JdbcPool();

	/**
	 * 私有化构造器
	 */
	private JdbcUtils() {
	}

	/**
	 * 建立连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		return jdbcPool.getConnection();
	}

	/**
	 * 释放资源
	 * 
	 * @param resultSet
	 * @param statement
	 * @param connection
	 */
	public static void free(ResultSet resultSet, Statement statement, Connection connection) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					jdbcPool.free(connection);
					// try {
					// connection.close();
					// } catch (SQLException e) {
					// e.printStackTrace();
					// }
				}
			}
		}
	}

}
