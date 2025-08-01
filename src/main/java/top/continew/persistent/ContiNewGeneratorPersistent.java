package top.continew.persistent;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * 缓存功能
 *
 * @author lww
 * @date 2020-05-15 14:24
 */
@Data
@State(name = "ContiNewGeneratorPersistent", storages = {@Storage("ContiNewGeneratorPersistent.xml")})
public class ContiNewGeneratorPersistent implements PersistentStateComponent<ContiNewGeneratorPersistent> {

	private String projectPath;
	private String vuePath;
	private String configPath;
	private String author;
	private String packageName;
	private String businessName;
	private String version;

	private boolean override;
	private boolean mysql;
	private boolean pg;

	private String tablePrefix;

	public static ContiNewGeneratorPersistent getInstance(Project project) {
		ContiNewGeneratorPersistent config = project.getService(ContiNewGeneratorPersistent.class);
		if (config == null) {
			config = new ContiNewGeneratorPersistent();
		}
		return config;
	}

	@Override
	public ContiNewGeneratorPersistent getState() {
		return this;
	}

	@Override
	public void loadState(@NotNull ContiNewGeneratorPersistent state) {
		XmlSerializerUtil.copyBean(state, this);
	}
}
