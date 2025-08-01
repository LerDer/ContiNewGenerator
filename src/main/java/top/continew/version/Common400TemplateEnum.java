package top.continew.version;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Administrator
 * @date 2025-07-29 0:18
 */
@Getter
@RequiredArgsConstructor
public enum Common400TemplateEnum implements TemplateEnum {

	MapperXml("backend/MapperXml.ftl", "mapper", "%sMapper.xml"),
	Menu("backend/Menu.ftl", "sql", "%sMenu.sql")
	//
	;

	private final String templatePath;
	private final String packageName;
	private final String fileName;

}
