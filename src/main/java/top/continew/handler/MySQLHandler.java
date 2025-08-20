package top.continew.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import top.continew.constant.GenerateConstant;
import top.continew.entity.SqlColumn;
import top.continew.entity.SqlTable;
import top.continew.entity.SysDict;
import top.continew.entity.TableIndex;
import top.continew.utils.DataSourceUtils;
import top.continew.utils.DataSourceUtils.ListHandler;
import top.continew.utils.NotificationUtil;

/**
 * @author lww
 * @date 2025-08-03 14:13
 */
public class MySQLHandler implements QueryHandler {

	private static MySQLHandler instance = new MySQLHandler();

	private MySQLHandler() {
	}

	public static MySQLHandler getInstance() {
		if (instance == null) {
			instance = new MySQLHandler();
		}
		return instance;
	}

	@Override
	public List<SqlTable> getSqlTables(Project project, VirtualFile vf) {
		initDataSource(project, vf);
		String sql = "SELECT TABLE_NAME,TABLE_COMMENT FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_SCHEMA = ? and TABLE_NAME not in (" + GenerateConstant.excludeTables + ")";
		ListHandler<SqlTable> handler = new ListHandler<>(SqlTable.class);
		return DataSourceUtils.executeQuery(sql, handler, DataSourceUtils.getDbName());
	}

	@Override
	public List<SqlColumn> getSqlTablesColumns(Project project, VirtualFile vf, String tableName) {
		initDataSource(project, vf);
		String sql = "SELECT * FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = ? AND table_name = ? ORDER BY ORDINAL_POSITION";
		ListHandler<SqlColumn> handler = new ListHandler<>(SqlColumn.class);
		return DataSourceUtils.executeQuery(sql, handler, DataSourceUtils.getDbName(), tableName);
	}

	@Override
	public List<SysDict> getDictNames(Project project, VirtualFile vf) {
		initDataSource(project, vf);
		String sql = "select `name`,`code` from sys_dict";
		ListHandler<SysDict> handler = new ListHandler<>(SysDict.class);
		return DataSourceUtils.executeQuery(sql, handler);
	}

	@Override
	public List<TableIndex> getSqlTablesIndex(Project project, VirtualFile vf, String tableName) {
		initDataSource(project, vf);
		String sql = "SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = ? AND table_name = ?";
		ListHandler<TableIndex> handler = new ListHandler<>(TableIndex.class);
		return DataSourceUtils.executeQuery(sql, handler, DataSourceUtils.getDbName(), tableName);
	}

	public static void initDataSource(Project project, VirtualFile vf) {
		if (DataSourceUtils.getDataSource() != null && StringUtils.isNotEmpty(DataSourceUtils.getDbName())) {
			return;
		}
		HikariConfig config = DataSourceUtils.getDataSourceConfig(project, vf, DBTypeEnum.MySQL);
		if (config != null) {
			try {
				DataSourceUtils.setDataSource(new HikariDataSource(config));
				Connection connection = DataSourceUtils.getDataSource().getConnection();
				DataSourceUtils.setDbName(connection.getCatalog());
				DataSourceUtils.closeConnection(connection);
			} catch (SQLException e) {
				NotificationUtil.showErrorNotification(project, "连接数据库失败", e.getMessage());
			}
			NotificationUtil.showInfoNotification(project, "连接数据库成功", "连接数据库成功");
		} else {
			NotificationUtil.showErrorNotification(project, "数据库配置为空", "数据库配置为空");
		}
	}

}
