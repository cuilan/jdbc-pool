# Java ConnectionPool

### Java 数据库连接池

* 依赖于 `mysql-connetor-java-{version}-bin.jar`
* 必须在 src 目录下创建一个 `jdbc.properties` 的配置文件

### 配置文件包含：

* `url='jdbc:mysql://localhost:3306/jdbc'` // 连接
* `user=root` // 用户名
* `password=123456` // 密码
* `initConnections=2` // 初始化连接数
* `maxConnections=10` //最大连接数
* `maxUseCount=5` // 每个连接的使用次数

### 使用

* 获取连接
```java
    package xxx;
    
    import cn.cuilan.utils.JdbcUtils;
    
    public class Test {
        public static void main(String[] args) {
        	Connection conn = JdbcUtils.getConnection();
        	......
        }
    }
```

* 使用 ORM 对象映射查询
```java
	package xxx;
	import java.lang.reflect.InvocationTargetException;
	import java.sql.Connection;
	import java.sql.SQLException;
	import cn.cuilan.orm.ORM;
	import cn.cuilan.utils.JdbcUtils;
	public class Test {
		public static void main(String[] args) throws SQLException, IllegalAccessException, 
					IllegalArgumentException, InvocationTargetException, InstantiationException {
			User user = (User) ORM.getObject(
				"SELECT id as Id, name as Name, birthday as Birthday, money as Money FROM `user`", User.class);
			System.out.println(user.toString());
		}
	}
```

	
	
	
