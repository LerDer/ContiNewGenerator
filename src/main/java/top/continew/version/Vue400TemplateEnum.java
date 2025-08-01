package top.continew.version;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Vue400TemplateEnum implements TemplateEnum {

	api("frontend/api.ftl", "src.api", "api.ts"),
	index("frontend/index.ftl", "src.views", "index.vue"),
	AddModal("frontend/AddModal.ftl", "src.views", "%sAddModal.vue"),
	DetailDrawer("frontend/DetailDrawer.ftl", "src.views", "%sDetailDrawer.vue")
	//
	;

	private final String templatePath;
	private final String packageName;
	private final String fileName;
}
