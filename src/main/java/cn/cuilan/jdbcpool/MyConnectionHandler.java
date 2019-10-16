package cn.cuilan.jdbcpool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * 调用处理器
 * 
 * @author 张岩
 *
 */
class MyConnectionHandler implements InvocationHandler {

	/** 真正的连接 */
	private Connection realConnection;

	/** 包装后的连接 */
	private Connection warpedConnection;

	/** 应用数据源 */
	private JdbcPool jdbcPool;

	/** 每个连接的最大使用次数 */
	private int maxUseCount = JdbcPool.maxUseCountToInt;

	/** 当前使用次数 */
	private int currentUseCount = 0;

	/**
	 * 构造方法
	 * 
	 * @param jdbcPool
	 */
	public MyConnectionHandler(JdbcPool jdbcPool) {
		this.jdbcPool = jdbcPool;
	}

	/**
	 * 创建连接
	 * 
	 * @param realConn
	 * @return
	 */
	public Connection bind(Connection realConn) {
		this.realConnection = realConn;
		// 代理来创建一个类，这个类实现java.sql.Connection接口，传递给this这个类
		this.warpedConnection = (Connection) Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class[] { Connection.class }, this);
		return warpedConnection;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if ("close".equals(method.getName())) {
			this.currentUseCount++;
			if (this.currentUseCount < this.maxUseCount) {
				this.jdbcPool.connectionPool.addLast(this.warpedConnection);
			} else {
				this.realConnection.close();
				this.jdbcPool.currentCount--;
			}
		}
		return method.invoke(this.realConnection, args);
	}

}
