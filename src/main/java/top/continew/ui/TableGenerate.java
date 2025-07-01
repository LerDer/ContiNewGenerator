package top.continew.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.commons.collections.CollectionUtils;
import top.continew.constant.GenerateConstant;
import top.continew.entity.SqlColumn;
import top.continew.entity.SysDict;
import top.continew.enums.FormTypeEnum;
import top.continew.enums.JavaTypeEnum;
import top.continew.enums.QueryTypeEnum;
import top.continew.utils.DataSourceUtils;

/**
 * @author lww
 * @date 2025-06-30 18:17
 */
public class TableGenerate extends DialogWrapper {

	private JPanel rootPanel;
	private JTable columnTable;
	private static final String[] columnList;
	//{"输入框", "数字输入框", "密码输入框", "文本域", "下拉框","单选框","开关","复选框","树选择","时间框","日期框","日期时间框","图片上传","文件上传","富文本框","地图选择"};
	private static final String[] FORM_TYPE_OPTIONS = Arrays.stream(FormTypeEnum.values()).map(FormTypeEnum::getDescription).toArray(String[]::new);
	// {"String", "Integer", "Long","Float","Double","Boolean","BigDecimal","LocalDate","LocalTime","LocalDateTime"};
	private static final String[] JAVA_TYPE_OPTIONS = Arrays.stream(JavaTypeEnum.values()).map(JavaTypeEnum::getDescription).toArray(String[]::new);
	// {"等于", "不等于", "大于", "大于等于", "小于", "小于等于", "模糊查询", "范围查询", "包含", "开始于", "结束于"};
	private static final String[] QUERY_TYPE_OPTIONS = Arrays.stream(QueryTypeEnum.values()).map(QueryTypeEnum::getDescription).toArray(String[]::new);

	static {
		columnList = new String[]{
				"序号", "列名称", "字段名称", "列类型", "Java类型", "描述", "列表", "表单", "必填", "查询", "表单类型", "查询方式", "关联字典"
		};
	}

	public TableGenerate(Project project, VirtualFile vf, Object selectedItem) {
		super(project);
		setTitle("ContiNew Generator");
		setModal(true);
		setResizable(false);
		this.setSize(1080, 720);
		this.init();
		showTable(project, vf, selectedItem);
	}

	private void showTable(Project project, VirtualFile vf, Object selectedItem) {
		columnTable.setDoubleBuffered(true);
		String tableName = "";
		Object[][] data = {};
		if (selectedItem != null) {
			if (selectedItem.toString().indexOf(" - ") > 0) {
				tableName = selectedItem.toString().split(" - ")[0];
			} else {
				tableName = selectedItem.toString();
			}
			List<SqlColumn> columns = DataSourceUtils.getSqlTablesColumns(project, vf, tableName);
			List<SysDict> dictNames = DataSourceUtils.getDictNames(project, vf);
			data = new Object[columns.size()][];
			for (SqlColumn sqlColumn : columns) {
				Object[] column = new Object[columnList.length];
				//序号
				column[0] = String.valueOf(sqlColumn.getOrdinalPosition());
				//名称
				column[1] = sqlColumn.getColumnName();
				//字段名称
				column[2] = sqlColumn.getJavaField();
				//类型
				column[3] = sqlColumn.getDataType();
				//Java类型
				column[4] = sqlColumn.getJavaType();
				//描述
				column[5] = sqlColumn.getColumnComment();
				//列表
				column[6] = Boolean.TRUE; // 设置为CheckBox
				//表单
				if (Arrays.asList(GenerateConstant.formExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
					column[7] = Boolean.FALSE;
					column[10] = "无需设置";
				} else {
					column[7] = Boolean.TRUE;
					if (sqlColumn.isNumber()) {
						column[10] = "数字输入框";
					} else if (sqlColumn.isDate()) {
						column[10] = "日期框";
					} else if (sqlColumn.isDatetime()) {
						column[10] = "日期时间框";
					} else if (sqlColumn.isBool()) {
						column[10] = "开关";
					} else if (sqlColumn.isString()) {
						column[10] = "输入框";
					} else {
						column[10] = "无需设置";
					}
				}
				//必填
				if (Arrays.asList(GenerateConstant.requiredExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
					column[8] = Boolean.FALSE;
				} else {
					column[8] = Boolean.TRUE;
				}
				//表单类型
				if (Arrays.asList(GenerateConstant.queryExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
					column[9] = Boolean.FALSE;
					column[11] = "无需设置";
				} else {
					column[9] = Boolean.TRUE;
					if (sqlColumn.isNumber()) {
						column[11] = "=";
					} else if (sqlColumn.isDate()) {
						column[11] = "BETWEEN";
					} else if (sqlColumn.isDatetime()) {
						column[11] = "BETWEEN";
					} else if (sqlColumn.isBool()) {
						column[11] = "=";
					} else if (sqlColumn.isString()) {
						column[11] = "LIKE '%s%'";
					} else {
						column[11] = "无需设置";
					}
				}

				//关联字典
				column[12] = "无需设置";
				data[columns.indexOf(sqlColumn)] = column;
			}

			// 创建表格模型
			DefaultTableModel model = new DefaultTableModel(data, columnList) {
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if (columnIndex == 6 || columnIndex == 7 || columnIndex == 8 || columnIndex == 9) {
						return Boolean.class;
					}
					return super.getColumnClass(columnIndex);
				}
			};
			MyHeaderRenderer headerRenderer = new MyHeaderRenderer();
			columnTable.getTableHeader().setDefaultRenderer(headerRenderer);
			MyCellRenderer renderer = new MyCellRenderer();
			for (int n = 0; n < columnTable.getColumnCount(); n++) {
				columnTable.getColumnModel().getColumn(n).setCellRenderer(renderer);
			}
			columnTable.getTableHeader().repaint();
			columnTable.setModel(model);
			columnTable.repaint();
			// 设置表单类型为下拉框
			TableColumn javaTypeColumn = columnTable.getColumnModel().getColumn(4);
			JComboBox<String> javaTypeComboBox = new ComboBox<>(JAVA_TYPE_OPTIONS);
			javaTypeColumn.setCellEditor(new DefaultCellEditor(javaTypeComboBox));

			TableColumn formTypeColumn = columnTable.getColumnModel().getColumn(10);
			JComboBox<String> formTypeComboBox = new ComboBox<>(FORM_TYPE_OPTIONS);
			formTypeColumn.setCellEditor(new DefaultCellEditor(formTypeComboBox));

			TableColumn queryTypeColumn = columnTable.getColumnModel().getColumn(11);
			JComboBox<String> queryTypeComboBox = new ComboBox<>(QUERY_TYPE_OPTIONS);
			queryTypeColumn.setCellEditor(new DefaultCellEditor(queryTypeComboBox));

			List<String> collect = dictNames.stream().map(SysDict::getName).collect(Collectors.toList());
			collect.add("无需设置");
			Collections.reverse(collect);
			TableColumn dictColumn = columnTable.getColumnModel().getColumn(12);
			JComboBox<String> dictComboBox = new ComboBox<>(collect.toArray(new String[0]));
			dictColumn.setCellEditor(new DefaultCellEditor(dictComboBox));
		}

	}

	@Override
	protected JComponent createCenterPanel() {
		return rootPanel;
	}

	@Override
	protected void dispose() {
		super.dispose();
	}

	@Override
	protected Action[] createActions() {
		return new Action[0];
	}
}
