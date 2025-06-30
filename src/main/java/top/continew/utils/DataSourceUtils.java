package top.continew.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.zaxxer.hikari.HikariConfig;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.yaml.snakeyaml.Yaml;

/**
 * @author lww
 * @date 2025-06-30 15:37
 */
public class DataSourceUtils {

	public static HikariConfig getDataSourceConfig(Project project, VirtualFile vf) {
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

}
