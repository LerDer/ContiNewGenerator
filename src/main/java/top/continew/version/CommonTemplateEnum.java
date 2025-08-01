package top.continew.version;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Administrator
 * @date 2025-07-29 0:18
 */
@Getter
@RequiredArgsConstructor
public enum CommonTemplateEnum implements TemplateEnum {

	MapperXml400("400/backend/MapperXml.ftl", "mapper", "%sMapper.xml", "4.0.0"),
	Menu400("400/backend/Menu.ftl", "sql", "%sMenu.sql", "4.0.0")
	//
	;

	private final String templatePath;
	private final String packageName;
	private final String fileName;
	private final String version;

	@Override
	public boolean firstToLowerCase() {
		return false;
	}
}
