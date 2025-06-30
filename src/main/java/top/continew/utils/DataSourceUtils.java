package top.continew.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import top.continew.entity.SqlColumn;
import top.continew.entity.SqlTable;

/**
 * @author lww
 * @date 2025-06-30 15:37
 */
@Slf4j
public class DataSourceUtils {

	private static volatile DataSource dataSource;

	@Getter
	private static volatile String dbName;

	private DataSourceUtils() {
	}

	public static List<SqlTable> getSqlTables(Project project, VirtualFile vf) {
		DataSourceUtils.initDataSource(project, vf);
		String sql = "SELECT TABLE_NAME,TABLE_COMMENT FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_SCHEMA = ?";
		ListHandler<SqlTable> handler = new ListHandler<>(SqlTable.class);
		return DataSourceUtils.executeQuery(sql, handler, DataSourceUtils.getDbName());
	}

	public static List<SqlColumn> getSqlTablesColumns(Project project, VirtualFile vf, String tableName) {
		String sql = "SELECT * FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = ? AND table_name = ? ORDER BY ORDINAL_POSITION";
		ListHandler<SqlColumn> handler = new ListHandler<>(SqlColumn.class);
		return DataSourceUtils.executeQuery(sql, handler, DataSourceUtils.getDbName(), tableName);
	}

	public static void initDataSource(Project project, VirtualFile vf) {
		if (dataSource != null && StringUtils.isNotEmpty(dbName)) {
			return;
		}
		HikariConfig config = getDataSourceConfig(project, vf);
		if (config != null) {
			try {
				dataSource = new HikariDataSource(config);
				Connection connection = dataSource.getConnection();
				dbName = connection.getCatalog();
				closeConnection(connection);
			} catch (SQLException e) {
				NotificationUtil.showErrorNotification(project, "连接数据库失败", e.getMessage());
			}
			NotificationUtil.showInfoNotification(project, "连接数据库成功", "连接数据库成功");
		} else {
			NotificationUtil.showErrorNotification(project, "数据库配置为空", "数据库配置为空");
		}
	}

	public static void closeConnection(Connection conn) {
		if (conn == null) {
			return;
		}
		try {
			conn.close();
			log.debug("关闭数据库连接");
		} catch (Exception e) {
			//log.error("关闭数据库连接失败", e);
		}
	}

	private static HikariConfig getDataSourceConfig(Project project, VirtualFile vf) {
		List<Map<String, Object>> docsList = new ArrayList<>();
		try (InputStream inputStream = vf.getInputStream()) {
			Yaml yaml = new Yaml();
			Iterable<Object> docs = yaml.loadAll(inputStream);
			for (Object doc : docs) {
				if (doc instanceof Map) {
					docsList.add((Map<String, Object>) doc);
				}
			}
			String url;
			String username;
			String password;
			Optional<Map<String, Object>> first = docsList.stream().filter(dl -> dl.get("spring") != null).findFirst();
			if (first.isPresent()) {
				Map<String, Object> spring = (Map<String, Object>) first.get().get("spring");
				Map<String, Object> datasource = (Map<String, Object>) spring.get("datasource");
				if (datasource != null) {
					url = (String) datasource.get("url");
					username = (String) datasource.get("username");
					password = (String) datasource.get("password");
				} else {
					NotificationUtil.showErrorNotification(project, "未找到数据源配置", "未找到数据源配置");
					return null;
				}
			} else {
				first = docsList.stream().filter(dl -> dl.get("spring.datasource") != null).findFirst();
				if (first.isPresent()) {
					Map<String, Object> datasource = (Map<String, Object>) first.get().get("spring.datasource");
					url = (String) datasource.get("url");
					username = (String) datasource.get("username");
					password = (String) datasource.get("password");
				} else {
					NotificationUtil.showErrorNotification(project, "未找到数据源配置", "未找到数据源配置");
					return null;
				}
			}

			String replaceUrl = url.replace("p6spy:", "")
					.replace("${DB_HOST:", "")
					.replace("${DB_PORT:", "")
					.replace("${DB_NAME:", "")
					.replace("}", "");
			String replaceUsername = username
					.replace("${DB_USER:", "")
					.replace("}", "");
			String replacePassword = password
					.replace("${DB_PWD:", "")
					.replace("}", "");
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl(replaceUrl);
			config.setUsername(replaceUsername);
			config.setPassword(replacePassword);
			config.setDriverClassName("com.mysql.cj.jdbc.Driver");
			config.setMaximumPoolSize(10);
			config.setMinimumIdle(5);
			config.setIdleTimeout(600000);
			config.setMaxLifetime(1800000);
			config.setConnectionTimeout(30000);
			return config;
		} catch (Exception e) {
			NotificationUtil.showErrorNotification(project, "未找到数据源配置", "未找到数据源配置");
		}
		return null;
	}

	/**
	 * 执行查询操作
	 *
	 * @param sql     SQL语句
	 * @param handler 结果集处理器
	 * @param params  参数数组
	 * @return 查询结果
	 */
	public static <T> T executeQuery(String sql, ResultSetHandler<T> handler, Object... params) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(sql);
			// 设置参数
			setParameters(pstmt, params);
			rs = pstmt.executeQuery();
			log.debug("执行查询操作, SQL: {}", sql);
			return handler.handle(rs);
		} catch (Exception e) {
			//log.error("执行查询操作失败, SQL: {}", sql, e);
			NotificationUtil.showWarningNotification("执行查询操作失败", e.getMessage());
		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
			closeConnection(conn);
		}
		return null;
	}

	/**
	 * 设置PreparedStatement参数
	 */
	private static void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}
		}
	}

	/**
	 * 关闭Statement
	 */
	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				//log.error("关闭Statement失败", e);
			}
		}
	}

	/**
	 * 关闭ResultSet
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				//log.error("关闭ResultSet失败", e);
			}
		}
	}

	/**
	 * 结果集处理器接口
	 */
	public interface ResultSetHandler<T> {

		T handle(ResultSet rs) throws SQLException;
	}

	/**
	 * Bean列表处理器(多行记录)
	 */
	public static class ListHandler<T> implements DataSourceUtils.ResultSetHandler<List<T>> {

		private final Class<T> clazz;

		public ListHandler(Class<T> clazz) {
			this.clazz = clazz;
		}

		@Override
		public List<T> handle(ResultSet rs) throws SQLException {
			List<T> list = new ArrayList<>();
			while (rs.next()) {
				T bean = getBean(rs, clazz);
				list.add(bean);
			}
			return list;
		}

		private static <T> T getBean(ResultSet rs, Class<T> clazz) {
			T bean = null;
			try {
				bean = clazz.getDeclaredConstructor().newInstance();
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnLabel(i);
					Object value = rs.getObject(i);
					try {
						Field field = clazz.getDeclaredField(columnName);
						field.setAccessible(true);
						field.set(bean, value);
					} catch (NoSuchFieldException e) {
						try {
							Field field = clazz.getDeclaredField(CommonUtil.underlineToHump(columnName));
							field.setAccessible(true);
							field.set(bean, value);
						} catch (NoSuchFieldException ex) {
						}
					}
				}
			} catch (Exception e) {
				//log.error("创建Bean实例失败", e);
				NotificationUtil.showWarningNotification("创建Bean实例失败", e.getMessage());
			}
			return bean;
		}
	}

}
