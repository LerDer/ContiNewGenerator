package top.continew.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * @author Administrator
 * @date 2025-08-18 0:19
 */
@Data
@State(name = "ContiNewConfigPersistent", storages = {@Storage("ContiNewConfigPersistent.xml")})
public class ContiNewConfigPersistent implements PersistentStateComponent<ContiNewConfigPersistent> {

	private Boolean highLight;
	private String requestExcludeText;
	private String responseExcludeText;
	private String requiredExcludeText;
	private String queryExcludeText;
	private String stringType;
	private String numberType;
	private String dateType;
	private String booleanType;

	public static ContiNewConfigPersistent getInstance() {
		ContiNewConfigPersistent config = ApplicationManager.getApplication().getService(ContiNewConfigPersistent.class);
		if (config == null) {
			config = new ContiNewConfigPersistent();
		}
		if (config.getHighLight() == null) {
			config.setHighLight(true);
		}
		if (config.getRequestExcludeText() == null) {
			config.setRequestExcludeText("createUser,createTime,updateUser,updateTime,deleteUser,deleteTime,delFlag,isDeleted,deletedBy");
		}
		if (config.getResponseExcludeText() == null) {
			config.setResponseExcludeText("id,createUser,createTime,updateUser,updateTime,deleteUser,deleteTime,delFlag,isDeleted,deletedBy");
		}
		if (config.getRequiredExcludeText() == null) {
			config.setRequiredExcludeText("id,createUser,createTime,updateUser,updateTime,deleteUser,deleteTime,delFlag,isDeleted,deletedBy");
		}
		if (config.getQueryExcludeText() == null) {
			config.setQueryExcludeText("createUser,createTime,updateUser,updateTime,deleteUser,deleteTime,delFlag,isDeleted,deletedBy");
		}
		if (config.getStringType() == null) {
			config.setStringType("LIKE '%s%'");
		}
		if (config.getNumberType() == null) {
			config.setNumberType("=");
		}
		if (config.getDateType() == null) {
			config.setDateType("BETWEEN");
		}
		if (config.getBooleanType() == null) {
			config.setBooleanType("=");
		}
		return config;
	}

	@Override
	public ContiNewConfigPersistent getState() {
		return this;
	}

	@Override
	public void loadState(@NotNull ContiNewConfigPersistent state) {
		XmlSerializerUtil.copyBean(state, this);
	}
}
