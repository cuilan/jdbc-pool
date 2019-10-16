package cn.cuilan.test;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import cn.cuilan.orm.ORM;
import cn.cuilan.utils.JdbcUtils;

/**
 * 数据库连接池测试
 * 
 * @author 张岩
 */
public class Test {

	public static void main(String[] args) throws SQLException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException {
		User user = (User) ORM.getObject(
				"SELECT id as Id, name as Name, birthday as Birthday, money as Money FROM `user`", User.class);
		System.out.println(user.toString());

		for (int i = 0; i < 10; i++) {
			Connection c = JdbcUtils.getConnection();
			System.out.println(c);
		}

	}

}
