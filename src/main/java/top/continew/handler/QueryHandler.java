package top.continew.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.List;
import top.continew.entity.SqlColumn;
import top.continew.entity.SqlTable;
import top.continew.entity.SysDict;
import top.continew.entity.TableIndex;

/**
 * @author lww
 * @date 2025-08-03 14:10
 */
public interface QueryHandler {

	List<SqlTable> getSqlTables(Project project, VirtualFile vf);

	List<SqlColumn> getSqlTablesColumns(Project project, VirtualFile vf, String tableName);

	List<SysDict> getDictNames(Project project, VirtualFile vf);

	List<TableIndex> getSqlTablesIndex(Project project, VirtualFile vf, String tableName);

}
