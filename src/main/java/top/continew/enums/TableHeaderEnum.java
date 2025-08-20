package top.continew.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.constant.GenerateConstant;

@Getter
@RequiredArgsConstructor
public enum TableHeaderEnum implements TableHeaderEnumMethod {

	INDEX(0, "序号") {
		@Override
		public boolean isEditable() {
			return false;
		}
	},
	COLUMN_NAME(1, "列名") {
		@Override
		public boolean isEditable() {
			return false;
		}
	},
	CODE_NAME(2, "字段名称"),
	COLUMN_TYPE(3, "类型") {
		@Override
		public boolean isEditable() {
			return false;
		}
	},
	CODE_TYPE(4, "Java类型") {
		@Override
		public boolean isComboBox() {
			return true;
		}

		@Override
		public String[] comboBoxOptions() {
			return GenerateConstant.JAVA_TYPE_OPTIONS;
		}
	},
	DESCRIPTION(5, "描述"),

	TABLE_LIST(6, "响应") {
		@Override
		public boolean isCheckbox() {
			return true;
		}

	},
	TABLE_FORM(7, "请求") {
		@Override
		public boolean isCheckbox() {
			return true;
		}

	},
	REQUIRED(8, "必填") {
		@Override
		public boolean isCheckbox() {
			return true;
		}

	},
	QUERY_FIELD(9, "查询") {
		@Override
		public boolean isCheckbox() {
			return true;
		}

	},
	QUERY_TYPE(10, "查询方式") {
		@Override
		public boolean isComboBox() {
			return true;
		}

		@Override
		public String[] comboBoxOptions() {
			return GenerateConstant.QUERY_TYPE_OPTIONS;
		}
	},
	FORM_SHOW_TYPE(11, "表单类型") {
		@Override
		public boolean isComboBox() {
			return true;
		}

		@Override
		public String[] comboBoxOptions() {
			return GenerateConstant.FORM_TYPE_OPTIONS;
		}
	},
	RELATION_DICT(12, "关联字典"),
	COLUMN_SIZE(13, "长度") {
		@Override
		public boolean isVisible() {
			return false;
		}
	},
	IS_PRIMARY(14, "是否主键") {
		@Override
		public boolean isVisible() {
			return false;
		}
	},
	IS_NULLABLE(15, "是否可为空") {
		@Override
		public boolean isVisible() {
			return false;
		}
	},
	UNIQUE(16, "唯一") {
		@Override
		public boolean isVisible() {
			return false;
		}
	},
	//
	;

	private final int index;
	private final String description;

	@Override
	public boolean isCheckbox() {
		return false;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public boolean isComboBox() {
		return false;
	}

	@Override
	public String[] comboBoxOptions() {
		return new String[0];
	}

}
