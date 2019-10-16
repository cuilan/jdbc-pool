package cn.cuilan.orm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import cn.cuilan.utils.JdbcUtils;

/**
 * ORM模板
 *
 * @author 张岩
 */
public class ORM {

	/**
	 * 此方法为数据库读取方法，参数列表中传递的SQL语句必须为其每一个字段起一个别名，且首字母必须大写
	 * 
	 * @param sql
	 *            SQL语句，此SQL语句必须为其每一个字段起一个别名，且首字母必须大写
	 * @param clazz
	 *            与数据库中表对应的对象
	 * @return 将查询出的数据封装为一个与参数列表中传递过来的一致的对象
	 * @throws IllegalAccessException
	 *             private方法无法反射异常
	 * @throws IllegalArgumentException
	 *             反射参数异常
	 * @throws InvocationTargetException
	 *             调用执行异常
	 * @throws InstantiationException
	 *             创建实例异常
	 * @throws SQLException
	 *             SQL语句异常或执行SQL语句异常
	 */
	public static Object getObject(String sql, Class<?> clazz) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException, SQLException {
		// 创建连接对象
		Connection conn = null;
		// 创建预编译语句
		PreparedStatement ps = null;
		// 创建结果集
		ResultSet rs = null;
		try {
			// 建立连接
			conn = JdbcUtils.getConnection();
			// 执行预编译语句
			ps = conn.prepareStatement(sql);
			// 执行查询
			rs = ps.executeQuery();
			// 获取结果集中的数据信息
			ResultSetMetaData rsmd = rs.getMetaData();
			// 获取结果集的列数
			int count = rsmd.getColumnCount();
			// 创建String数组，用户保存每列的名称，大小为count
			String[] colNames = new String[count];
			// 循环遍历，从1开始
			for (int i = 1; i <= count; i++) {
				// 获取每列的别名（名称），赋值
				colNames[i - 1] = rsmd.getColumnLabel(i);
			}
			// 创建Object对象
			Object object = null;
			// 获取clazz对象中的方法
			Method[] ms = clazz.getMethods();
			if (rs.next()) {
				// 创建实例
				object = clazz.newInstance();
				for (int i = 0; i < colNames.length; i++) {
					// 拼接字符串为setXxxx
					String colName = colNames[i];
					String methodName = "set" + colName;
					for (Method m : ms) {
						// 如果方法中的名称等于刚刚拼接的方法名称
						if (methodName.equals(m.getName())) {
							// 则执行调用
							m.invoke(object, rs.getObject(colName));
						}
					}
				}
			}
			System.out.println("SQL: " + sql);
			// 返回此对象
			return object;
		} finally {
			JdbcUtils.free(rs, ps, conn);
		}
	}

	// TODO
	public static int update(String sql, Class<?> clazz) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException, SQLException {
		// 创建连接对象
		Connection conn = null;
		// 创建预编译语句
		PreparedStatement ps = null;
		// 创建结果集
		ResultSet rs = null;
		try {
			// 建立连接
			conn = JdbcUtils.getConnection();
			// 执行预编译语句
			ps = conn.prepareStatement(sql);
			// 执行查询
			int count = ps.executeUpdate();
			System.out.println("SQL: " + sql);
			// 返回此对象
			return count;
		} finally {
			JdbcUtils.free(rs, ps, conn);
		}
	}

}
