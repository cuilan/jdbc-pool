package cn.cuilan.jdbcpool;

import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * 自己动手做一个数据库连接池
 * 
 * @author 张岩
 */
public class JdbcPool implements DataSource {

	/** URL连接 */
	private static String url;

	/** 用户名 */
	private static String user;

	/** 密码 */
	private static String password;

	/** 字符串初始化连接数 */
	private static String initConnections;

	/** 整形初始化连接数 */
	private static int initCount;

	/** 字符串最大连接数 */
	private static String maxConnections;

	/** 整形最大连接数 */
	private static int maxCount;

	/** 当前连接数 */
	int currentCount = 0;

	/** 每个连接的最大使用次数 */
	static String maxUseCount;

	/** 每个连接的最大使用次数 */
	static int maxUseCountToInt;

	/** 连接池 */
	LinkedList<Connection> connectionPool = new LinkedList<>();

	/**
	 * 注册驱动
	 */
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Properties jdbcProp = new Properties();
			InputStream is = JdbcPool.class.getClassLoader().getResourceAsStream("jdbc.properties");
			jdbcProp.load(is);
			url = jdbcProp.getProperty("url");
			user = jdbcProp.getProperty("user");
			password = jdbcProp.getProperty("password");
			initConnections = jdbcProp.getProperty("initConnections");
			initCount = Integer.parseInt(initConnections);
			maxConnections = jdbcProp.getProperty("maxConnections");
			maxCount = Integer.parseInt(maxConnections);
			maxUseCount = jdbcProp.getProperty("maxUseCount");
			maxUseCountToInt = Integer.parseInt(maxUseCount);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 空参构造器，执行创建数据库连接池
	 */
	public JdbcPool() {
		try {
			for (int i = 0; i < initCount; i++) {
				this.connectionPool.addLast(this.createConnection());
				this.currentCount++;
			}
		} catch (SQLException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * 创建连接
	 * 
	 * @return 返回Connection对象
	 * @throws SQLException
	 */
	private Connection createConnection() throws SQLException {
		Connection realConn = DriverManager.getConnection(url, user, password);
		MyConnectionHandler proxy = new MyConnectionHandler(this);
		return proxy.bind(realConn);
	}

	/**
	 * 获取一个连接，支持并发多线程
	 * 
	 * @return 返回一个Connection对象
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		synchronized (connectionPool) {
			if (this.connectionPool.size() > 0) {
				return this.connectionPool.removeFirst();
			}
			if (this.currentCount < maxCount) {
				this.currentCount++;
				return this.createConnection();
			}
			throw new SQLException("数据库连接池中没有可用的连接");
		}
	}

	/**
	 * 释放资源
	 * 
	 * @param conn
	 */
	public void free(Connection conn) {
		this.connectionPool.addLast(conn);
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return null;
	}

}
