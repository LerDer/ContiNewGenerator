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
 * @date 2025-08-03 14:29
 */
public class PostgreSQLHandler implements QueryHandler {

	private static PostgreSQLHandler instance = new PostgreSQLHandler();

	private PostgreSQLHandler() {
	}

	public static PostgreSQLHandler getInstance() {
		if (instance == null) {
			instance = new PostgreSQLHandler();
		}
		return instance;
	}

	@Override
	public List<SqlTable> getSqlTables(Project project, VirtualFile vf) {
		initDataSource(project, vf);
		String sql = "SELECT tablename AS TABLE_NAME, obj_description(relfilenode, 'pg_class') AS TABLE_COMMENT FROM pg_tables t JOIN pg_class c ON t.tablename = c.relname "
				+ "WHERE schemaname = 'public' and t.tablename not in (" + GenerateConstant.excludeTables + ")";
		ListHandler<SqlTable> handler = new ListHandler<>(SqlTable.class);
		return DataSourceUtils.executeQuery(sql, handler);
	}

	@Override
	public List<SqlColumn> getSqlTablesColumns(Project project, VirtualFile vf, String tableName) {
		initDataSource(project, vf);
		String sql = "SELECT a.attname AS COLUMN_NAME, a.attnum AS ORDINAL_POSITION, pg_get_expr(ad.adbin, ad.adrelid) AS COLUMN_DEFAULT, CASE WHEN a.attnotnull THEN 'NO' ELSE 'YES' END AS IS_NULLABLE, t.typname AS DATA_TYPE, '' AS COLUMN_TYPE, a.attlen AS CHARACTER_MAXIMUM_LENGTH, CASE WHEN ct.contype = 'p' THEN 'PRI' ELSE '' END AS COLUMN_KEY, col_description(a.attrelid, a.attnum) AS COLUMN_COMMENT FROM pg_attribute a LEFT JOIN pg_type t ON a.atttypid = t.oid LEFT JOIN pg_attrdef ad ON a.attrelid = ad.adrelid AND a.attnum = ad.adnum LEFT JOIN pg_constraint ct ON ct.conrelid = a.attrelid AND a.attnum = ANY(ct.conkey) AND ct.contype = 'p' WHERE a.attrelid = (SELECT oid FROM pg_class WHERE relname = ?) AND a.attnum > 0 AND NOT a.attisdropped ORDER BY a.attnum";
		ListHandler<SqlColumn> handler = new ListHandler<>(SqlColumn.class);
		return DataSourceUtils.executeQuery(sql, handler, tableName);
	}

	@Override
	public List<SysDict> getDictNames(Project project, VirtualFile vf) {
		initDataSource(project, vf);
		String sql = "select name,code from sys_dict";
		ListHandler<SysDict> handler = new ListHandler<>(SysDict.class);
		return DataSourceUtils.executeQuery(sql, handler);
	}

	@Override
	public List<TableIndex> getSqlTablesIndex(Project project, VirtualFile vf, String tableName) {
		String sql = """
				SELECT
				    idxs.tablename as tableName,
				    i.relname AS indexName,
				    a.attname AS columnName,
				    am.amname AS indexType,
				    case idx.indisunique when  'true' then 0 else 1 end AS nonUnique,
				    case idx.indisprimary when  'true' then 1 else 0 end AS isPrimary
				FROM
				    pg_class t,
				    pg_class i,
				    pg_index idx,
				    pg_attribute a,
				    pg_am am,
				    pg_indexes idxs
				WHERE
				    t.oid = idx.indrelid
				  AND i.oid = idx.indexrelid
				  AND a.attrelid = t.oid
				  AND a.attnum = ANY(idx.indkey)
				  AND t.relkind = 'r'
				  AND i.relam = am.oid
				  AND i.relname = idxs.indexname
				  AND t.relname = ?
				ORDER BY
				    i.relname,
				    a.attnum;
				""";
		ListHandler<TableIndex> handler = new ListHandler<>(TableIndex.class);
		return DataSourceUtils.executeQuery(sql, handler, tableName);
	}

	public static void initDataSource(Project project, VirtualFile vf) {
		if (DataSourceUtils.getDataSource() != null && StringUtils.isNotEmpty(DataSourceUtils.getDbName())) {
			return;
		}
		HikariConfig config = DataSourceUtils.getDataSourceConfig(project, vf, DBTypeEnum.PostgreSQL);
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
