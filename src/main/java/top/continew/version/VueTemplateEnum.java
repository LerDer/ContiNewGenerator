package top.continew.version;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VueTemplateEnum implements TemplateEnum {

	api400("400/frontend/api.ftl", "src.api", "%s.ts", "4.0.0") {
		@Override
		public boolean firstToLowerCase() {
			return true;
		}
	},
	index400("400/frontend/index.ftl", "src.views", "index.vue", "4.0.0"),
	AddModal400("400/frontend/AddModal.ftl", "src.views", "%sAddModal.vue", "4.0.0"),
	DetailDrawer400("400/frontend/DetailDrawer.ftl", "src.views", "%sDetailDrawer.vue", "4.0.0")
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
