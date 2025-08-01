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

		@Override
		public boolean className2Folder() {
			return false;
		}
	},
	index400("400/frontend/index.ftl", "src.views", "index.vue", "4.0.0"),
	AddModal400("400/frontend/AddModal.ftl", "src.views", "%sAddModal.vue", "4.0.0"),
	DetailDrawer400("400/frontend/DetailDrawer.ftl", "src.views", "%sDetailDrawer.vue", "4.0.0"),

	api370("370/frontend/api.ftl", "src.api", "%s.ts", "3.7.0") {
		@Override
		public boolean firstToLowerCase() {
			return true;
		}

		@Override
		public boolean className2Folder() {
			return false;
		}
	},
	index370("370/frontend/index.ftl", "src.views", "index.vue", "3.7.0"),
	AddModal370("370/frontend/AddModal.ftl", "src.views", "%sAddModal.vue", "3.7.0"),
	DetailDrawer370("370/frontend/DetailDrawer.ftl", "src.views", "%sDetailDrawer.vue", "3.7.0"),

	api360("360/frontend/api.ftl", "src.api", "%s.ts", "3.6.0") {
		@Override
		public boolean firstToLowerCase() {
			return true;
		}

		@Override
		public boolean className2Folder() {
			return false;
		}
	},
	index360("360/frontend/index.ftl", "src.views", "index.vue", "3.6.0"),
	AddModal360("360/frontend/AddModal.ftl", "src.views", "%sAddModal.vue", "3.6.0"),
	DetailDrawer360("360/frontend/DetailDrawer.ftl", "src.views", "%sDetailDrawer.vue", "3.6.0"),

	api350("350/frontend/api.ftl", "src.api", "%s.ts", "3.5.0") {
		@Override
		public boolean firstToLowerCase() {
			return true;
		}

		@Override
		public boolean className2Folder() {
			return false;
		}
	},
	index350("350/frontend/index.ftl", "src.views", "index.vue", "3.5.0"),
	AddModal350("350/frontend/AddModal.ftl", "src.views", "%sAddModal.vue", "3.5.0"),
	DetailDrawer350("350/frontend/DetailDrawer.ftl", "src.views", "%sDetailDrawer.vue", "3.5.0"),
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

	@Override
	public boolean className2Folder() {
		return true;
	}
}
