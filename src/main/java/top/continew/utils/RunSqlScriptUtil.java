package top.continew.utils;

import com.intellij.openapi.project.Project;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import javax.sql.DataSource;
import org.apache.ibatis.jdbc.ScriptRunner;

/**
 * @author lww
 * @date 2025-11-17 16:17
 */
public class RunSqlScriptUtil {

	public static void runSqlScript(File script, Project project) {
		try {
			DataSource dataSource = DataSourceUtils.getDataSource();
			Connection connection = dataSource.getConnection();
			ScriptRunner runner = new ScriptRunner(connection);
			Reader reader = new FileReader(script);
			runner.runScript(reader);
			connection.close();
			NotificationUtil.showInfoNotification(project, "执行SQL脚本", "执行SQL脚本成功");
		} catch (Exception e) {
			NotificationUtil.showErrorNotification(project, "执行SQL脚本失败", e.getMessage());
		}
	}
}
