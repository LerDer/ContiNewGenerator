package top.continew.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.util.DateUtils;
import org.jetbrains.annotations.NotNull;
import top.continew.constant.GenerateConstant;
import top.continew.entity.SqlColumn;
import top.continew.entity.SysDict;
import top.continew.enums.FormTypeEnum;
import top.continew.enums.QueryTypeEnum;
import top.continew.enums.TableHeaderEnum;
import top.continew.icon.PluginIcons;
import top.continew.persistent.ContiNewGeneratorPersistent;
import top.continew.utils.CommonUtil;
import top.continew.utils.DataSourceUtils;

/**
 * @author lww
 * @date 2025-06-30 18:17
 */
public class TableGenerate extends DialogWrapper {

	private JPanel rootPanel;
	private JTable columnTable;
	private JButton returnButton;
	private JButton generateButton;
	private Map<String, Object> dictMap = new HashMap<>();

	public TableGenerate(Project project, VirtualFile vf, Object selectedItem, Object moduleSelectItem) {
		super(project);
		setTitle("ContiNew Generator");
		setModal(true);
		setResizable(false);
		this.setSize(1080, 720);
		this.init();
		showTable(project, vf, selectedItem);
		generateButton.setIcon(PluginIcons.success);
		returnButton.setIcon(PluginIcons.sendToTheLeft);
		returnButton.addActionListener(e -> dispose());
		generateButton.addActionListener(e -> generateCode(project, selectedItem, moduleSelectItem));
	}

	private void generateCode(Project project, Object selectedItem, Object moduleSelectItem) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		String projectPath = instance.getProjectPath();
		String vuePath = instance.getVuePath();
		String configPath = instance.getConfigPath();
		String author = instance.getAuthor();
		String packageName = instance.getPackageName();
		String businessName = instance.getBusinessName();
		boolean isOverride = instance.isOverride();
		boolean isMysql = instance.isMysql();
		boolean isPg = instance.isPg();
		String tablePrefix = instance.getTablePrefix();

		String tableName;
		String tableComment = "";
		String className = "";
		String firstSmallClassName = "";
		if (selectedItem.toString().indexOf(" - ") > 0) {
			tableName = selectedItem.toString().split(" - ")[0];
			tableComment = selectedItem.toString().split(" - ")[1];
		} else {
			tableName = selectedItem.toString();
		}

		if (StringUtils.isNotBlank(tablePrefix)) {
			className = CommonUtil.underlineToHump1(tableName.replace(tablePrefix, ""));
			firstSmallClassName = CommonUtil.underlineToHump(tableName.replace(tablePrefix, ""));
		} else {
			className = CommonUtil.underlineToHump1(tableName);
			firstSmallClassName = CommonUtil.underlineToHump(tableName);
		}

		Map<String, Object> dataModel = new HashMap<>();

		List<Object> dictCodes = new ArrayList<>();
		dataModel.put("dictCodes", dictCodes);
		//表名称
		dataModel.put("tableName", tableName);
		//表注释
		dataModel.put("tableComment", tableComment);
		//模块名称
		if (moduleSelectItem != null) {
			dataModel.put("apiModuleName", moduleSelectItem);
		} else {
			dataModel.put("apiModuleName", "");
		}
		//包名
		dataModel.put("packageName", packageName);
		//业务名
		dataModel.put("businessName", businessName);
		//作者
		dataModel.put("author", author);
		//表前缀
		dataModel.put("tablePrefix", tablePrefix);
		//是否覆盖
		dataModel.put("isOverride", isOverride);
		//创建时间
		dataModel.put("createTime", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		//修改时间
		dataModel.put("updateTime", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		//生成时间
		dataModel.put("datetime", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

		//API 名称
		dataModel.put("apiName", firstSmallClassName);
		//类名
		dataModel.put("className", className);
		//类名前缀
		dataModel.put("classNamePrefix", className);
		//子包名称
		dataModel.put("subPackageName", GenerateConstant.doPackageName);

		dataModel.put("hasBigDecimalField", false);
		dataModel.put("hasTimeField", false);
		dataModel.put("hasDictField", false);
		dataModel.put("hasRequiredField", false);

		dataModel.put("doExcludeFields", GenerateConstant.doExcludeFields.split(","));
		dataModel.put("respExcludeFields", GenerateConstant.respExcludeFields.split(","));
		dataModel.put("detailRespExcludeFields", GenerateConstant.detailRespExcludeFields.split(","));

		List<Map<String, Object>> fieldConfigs = new ArrayList<>();
		dataModel.put("fieldConfigs", fieldConfigs);
		Map<String, Object> fieldConfig;
		TableColumnModel columnModel = columnTable.getColumnModel();
		int rowCount = columnTable.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			fieldConfig = new HashMap<>();
			fieldConfigs.add(fieldConfig);
			fieldConfig.put("tableName", tableName);
			// 遍历列
			for (int j = 0; j < columnModel.getColumnCount(); j++) {
				TableColumn column = columnModel.getColumn(j);
				String columnName = column.getHeaderValue().toString();
				if (columnName.equals(TableHeaderEnum.INDEX.getDescription())) {
					fieldConfig.put("id", columnTable.getValueAt(i, j).toString());
					fieldConfig.put("fieldSort", columnTable.getValueAt(i, j).toString());
					continue;
				}
				// 列名称
				if (columnName.equals(TableHeaderEnum.COLUMN_NAME.getDescription())) {
					fieldConfig.put("columnName", columnTable.getValueAt(i, j).toString());
					continue;
				}
				// 字段名称
				if (columnName.equals(TableHeaderEnum.CODE_NAME.getDescription())) {
					fieldConfig.put("fieldName", columnTable.getValueAt(i, j).toString());
					continue;
				}
				// 列类型
				if (columnName.equals(TableHeaderEnum.COLUMN_TYPE.getDescription())) {
					fieldConfig.put("columnType", columnTable.getValueAt(i, j).toString());
					if (CommonUtil.isDateTimeType(columnTable.getValueAt(i, j).toString()) || CommonUtil.isDateType(columnTable.getValueAt(i, j).toString())) {
						dataModel.put("hasTimeField", true);
						continue;
					}
				}

				// Java类型
				if (columnName.equals(TableHeaderEnum.CODE_TYPE.getDescription())) {
					fieldConfig.put("fieldType", columnTable.getValueAt(i, j).toString());
					if (columnTable.getValueAt(i, j).toString().equalsIgnoreCase("BigDecimal")) {
						dataModel.put("hasBigDecimalField", true);
					}
					continue;
				}
				// 描述
				if (columnName.equals(TableHeaderEnum.DESCRIPTION.getDescription())) {
					fieldConfig.put("comment", columnTable.getValueAt(i, j).toString());
					continue;
				}
				// 列表
				if (columnName.equals(TableHeaderEnum.TABLE_LIST.getDescription())) {
					fieldConfig.put("showInList", columnTable.getValueAt(i, j));
					continue;
				}
				// 表单
				if (columnName.equals(TableHeaderEnum.TABLE_FORM.getDescription())) {
					fieldConfig.put("showInForm", columnTable.getValueAt(i, j));
					continue;
				}
				// 必填
				if (columnName.equals(TableHeaderEnum.REQUIRED.getDescription())) {
					fieldConfig.put("isRequired", columnTable.getValueAt(i, j));
					if (columnTable.getValueAt(i, j).equals(Boolean.TRUE)) {
						dataModel.put("hasRequiredField", true);
					}
					continue;
				}
				// 查询
				if (columnName.equals(TableHeaderEnum.QUERY_FIELD.getDescription())) {
					fieldConfig.put("showInQuery", columnTable.getValueAt(i, j));
					continue;
				}
				// 查询方式
				if (columnName.equals(TableHeaderEnum.QUERY_TYPE.getDescription())) {
					String queryType = columnTable.getValueAt(i, j).toString();
					if (StringUtils.isNotBlank(queryType)) {
						QueryTypeEnum typeEnum = QueryTypeEnum.getByDes(queryType);
						if (typeEnum != null) {
							fieldConfig.put("queryType", typeEnum.name());
						} else {
							fieldConfig.put("queryType", "");
						}
					} else {
						fieldConfig.put("queryType", "");
					}
					continue;
				}
				// 表单类型
				if (columnName.equals(TableHeaderEnum.FORM_SHOW_TYPE.getDescription())) {
					String formType = columnTable.getValueAt(i, j).toString();
					if (StringUtils.isNotBlank(formType)) {
						FormTypeEnum typeEnum = FormTypeEnum.getByDes(formType);
						if (typeEnum != null) {
							fieldConfig.put("formType", typeEnum.name());
						} else {
							fieldConfig.put("formType", "");
						}
					} else {
						fieldConfig.put("formType", "");
					}
					continue;
				}

				// 关联字典
				if (columnName.equals(TableHeaderEnum.RELATION_DICT.getDescription())) {
					String dictName = columnTable.getValueAt(i, j).toString();
					if (StringUtils.isNotBlank(dictName)) {
						Object o = dictMap.get(dictName);
						if (o != null) {
							dataModel.put("hasDictField", true);
							fieldConfig.put("dictCode", o);
							dictCodes.add(o);
						} else {
							fieldConfig.put("dictCode", "");
						}
					} else {
						fieldConfig.put("dictCode", "");
					}
					continue;
				}
				// 长度
				if (columnName.equals(TableHeaderEnum.COLUMN_SIZE.getDescription())) {
					Object valueAt = columnTable.getValueAt(i, j);
					if (valueAt != null) {
						try {
							Integer value = Integer.valueOf(valueAt.toString());
							fieldConfig.put("columnSize", value);
						} catch (NumberFormatException ignored) {
						}
					}
					continue;
				}
				if (columnName.equals(TableHeaderEnum.IS_PRIMARY.getDescription())) {
					Object valueAt = columnTable.getValueAt(i, j);
					if (Boolean.TRUE.equals(valueAt)) {
						fieldConfig.put("isPrimary", Boolean.TRUE);
					} else {
						fieldConfig.put("isPrimary", Boolean.FALSE);
					}
					continue;
				}
				if (columnName.equals(TableHeaderEnum.IS_NULLABLE.getDescription())) {
					Object valueAt = columnTable.getValueAt(i, j);
					if (Boolean.TRUE.equals(valueAt)) {
						fieldConfig.put("isNotNull", Boolean.TRUE);
					} else {
						fieldConfig.put("isNotNull", Boolean.TRUE);
					}
				}
			}
		}

		// 使用与依赖相同的版本
		Configuration cfg = new Configuration(new Version("2.3.28"));
		cfg.setSharedVariable("statics", BeansWrapper.getDefaultInstance().getStaticModels());
		// 设置模板所在目录
		cfg.setClassForTemplateLoading(TableGenerate.class, "/templates");
		String javaPath = projectPath + File.separator + "src" + File.separator + "main" + File.separator + "java";
		String resourcesPath = projectPath + File.separator + "src" + File.separator + "main" + File.separator + "resources";

		//String jsonString = JSONObject.toJSONString(dataModel);
		//System.out.println("jsonString = " + jsonString);
		if (StringUtils.isNotBlank(projectPath)) {
			//生成DO
			generateFile(cfg, GenerateConstant.doTemplatePath,
					dataModel,
					javaPath,
					packageName + "." + GenerateConstant.doPackageName,
					className + "DO.java");
			//生成Query
			generateFile(cfg, GenerateConstant.queryTemplatePath,
					dataModel,
					javaPath,
					packageName + "." + GenerateConstant.queryPackageName,
					className + "Query.java");
			//生成Req
			generateFile(cfg, GenerateConstant.reqTemplatePath,
					dataModel,
					javaPath,
					packageName + "." + GenerateConstant.reqPackageName,
					className + "Req.java");
			//生成Resp
			generateFile(cfg, GenerateConstant.respTemplatePath,
					dataModel,
					javaPath,
					packageName + "." + GenerateConstant.respPackageName,
					className + "Resp.java");
			//生成DetailResp
			generateFile(cfg, GenerateConstant.detailRespTemplatePath,
					dataModel,
					javaPath,
					packageName + "." + GenerateConstant.detailRespPackageName,
					className + "DetailResp.java");
			//生成Mapper
			generateFile(cfg, GenerateConstant.mapperTemplatePath,
					dataModel,
					javaPath,
					packageName + "." + GenerateConstant.mapperPackageName,
					className + "Mapper.java");

			//生成MapperXml
			generateFile(cfg, GenerateConstant.mapperXmlTemplatePath,
					dataModel,
					resourcesPath,
					GenerateConstant.mapperXmlPackageName,
					className + GenerateConstant.mapperXmlSuffex + GenerateConstant.mapperXmlExtenstion);
			//生成Service
			generateFile(cfg, GenerateConstant.serviceTemplatePath,
					dataModel,
					javaPath,
					packageName + "." + GenerateConstant.servicePackageName,
					className + "Service.java");
			//生成ServiceImpl
			generateFile(cfg, GenerateConstant.serviceImplTemplatePath,
					dataModel,
					javaPath,
					packageName + "." + GenerateConstant.serviceImplPackageName,
					className + "ServiceImpl.java");
			//生成Controller
			generateFile(cfg, GenerateConstant.controllerTemplatePath,
					dataModel,
					javaPath,
					packageName + "." + GenerateConstant.controllerPackageName,
					className + "Controller.java");
		}
		if (StringUtils.isNotBlank(vuePath)) {
			//前端代码
			//生成Api
			generateFile(cfg, GenerateConstant.apiTemplatePath,
					dataModel,
					vuePath,
					GenerateConstant.apiPackageName + (moduleSelectItem == null ? "" : File.separator + moduleSelectItem),
					firstSmallClassName + GenerateConstant.apiExtenstion);
			//生成Index
			generateFile(cfg, GenerateConstant.indexTemplatePath,
					dataModel,
					vuePath,
					GenerateConstant.indexPackageName + (moduleSelectItem == null ? "" : File.separator + moduleSelectItem),
					firstSmallClassName + File.separator + "index" + GenerateConstant.indexExtenstion);
			//生成Modal
			generateFile(cfg, GenerateConstant.addModelTemplatePath,
					dataModel,
					vuePath,
					GenerateConstant.addModelPackageName + (moduleSelectItem == null ? "" : File.separator + moduleSelectItem),
					firstSmallClassName + File.separator + className + "AddModal" + GenerateConstant.addModelExtenstion);
			//生成DetailDrawer
			generateFile(cfg, GenerateConstant.detailDrawerTemplatePath,
					dataModel,
					vuePath,
					GenerateConstant.detailDrawerPackageName + (moduleSelectItem == null ? "" : File.separator + moduleSelectItem),
					firstSmallClassName + File.separator + className + "DetailDrawer" + GenerateConstant.detailDrawerExtenstion);
			//生成Menu
			generateFile(cfg, GenerateConstant.menuTemplatePath,
					dataModel,
					resourcesPath,
					GenerateConstant.menuPackageName,
					className + "Menu" + GenerateConstant.menuExtenstion);
			//NotificationUtil.showInfoNotification(project, "生成成功", "生成成功");
			JOptionPane.showMessageDialog(rootPanel, "success", "生成成功!", JOptionPane.INFORMATION_MESSAGE);
		}
		this.dispose();
	}

	private void generateFile(Configuration cfg, String templatePath, Map<String, Object> dataModel, String filePath, String packageName, String fileName) {
		File file = new File(filePath + File.separator + packageName.replace(".", File.separator) + File.separator + fileName);
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		try (Writer out = new OutputStreamWriter(new FileOutputStream(file),
				StandardCharsets.UTF_8)) {
			Template template = cfg.getTemplate(templatePath);
			template.process(dataModel, out);
			// 输出结果
			String result = out.toString();
			System.out.println(result);
		} catch (IOException | TemplateException e) {
			throw new RuntimeException(e);
		}

	}

	private void showTable(Project project, VirtualFile vf, Object selectedItem) {
		columnTable.setDoubleBuffered(true);
		String tableName;
		Object[][] data;
		if (selectedItem != null) {
			if (selectedItem.toString().indexOf(" - ") > 0) {
				tableName = selectedItem.toString().split(" - ")[0];
			} else {
				tableName = selectedItem.toString();
			}
			List<SqlColumn> columns = DataSourceUtils.getSqlTablesColumns(project, vf, tableName);
			List<SysDict> dictNames = DataSourceUtils.getDictNames(project, vf);
			dictMap = dictNames.stream().collect(Collectors.toMap(SysDict::getName, SysDict::getCode));
			data = new Object[columns.size()][];
			for (int i = 0; i < columns.size(); i++) {
				SqlColumn sqlColumn = columns.get(i);
				Object[] column = new Object[GenerateConstant.COLUMN_LIST.length];
				//序号
				column[0] = i + 1;
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
				} else {
					column[7] = Boolean.TRUE;
				}
				//必填
				if (Arrays.asList(GenerateConstant.requiredExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
					column[8] = Boolean.FALSE;
				} else {
					column[8] = Boolean.TRUE;
				}
				//查询
				if (Arrays.asList(GenerateConstant.queryExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
					column[9] = Boolean.FALSE;
				} else {
					// 数字类型默认支持查询 减少选择项
					if (sqlColumn.isNumber()) {
						column[9] = Boolean.TRUE;
					} else {
						column[9] = Boolean.FALSE;
					}
				}
				//查询方式
				if (Arrays.asList(GenerateConstant.queryExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
					column[10] = GenerateConstant.DEFAULT_TEXT;
				} else {
					if (sqlColumn.isNumber()) {
						column[10] = "=";
						//} else if (sqlColumn.isDate()) {
						//	column[10] = "BETWEEN";
						//} else if (sqlColumn.isDatetime()) {
						//	column[10] = "BETWEEN";
						//} else if (sqlColumn.isBool()) {
						//	column[10] = "=";
						//} else if (sqlColumn.isString()) {
						//	column[10] = "LIKE '%s%'";
					} else {
						column[10] = GenerateConstant.DEFAULT_TEXT;
					}
				}
				//表单类型
				if (Arrays.asList(GenerateConstant.formExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
					column[11] = GenerateConstant.DEFAULT_TEXT;
				} else {
					if (sqlColumn.isString()) {
						column[11] = "输入框";
					} else if (sqlColumn.isDate()) {
						column[11] = FormTypeEnum.DATE.getDescription();
					} else if (sqlColumn.isDatetime()) {
						column[11] = FormTypeEnum.DATE_TIME.getDescription();
					} else if (sqlColumn.isBool()) {
						column[11] = FormTypeEnum.SWITCH.getDescription();
					} else if (sqlColumn.isNumber()) {
						column[11] = FormTypeEnum.INPUT_NUMBER.getDescription();
					} else {
						column[11] = GenerateConstant.DEFAULT_TEXT;
					}
				}
				//关联字典
				column[12] = GenerateConstant.DEFAULT_TEXT;
				data[columns.indexOf(sqlColumn)] = column;
				column[13] = sqlColumn.getCharacterMaximumLength();
				column[14] = sqlColumn.isPrimaryKey();
				column[15] = sqlColumn.isNULLAble();
			}

			// 创建表格模型
			DefaultTableModel model = new DefaultTableModel(data, GenerateConstant.COLUMN_LIST) {
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					List<Integer> collect = Arrays.stream(TableHeaderEnum.values())
							.filter(TableHeaderEnum::isCheckbox)
							.map(TableHeaderEnum::getIndex)
							.toList();
					if (collect.contains(columnIndex)) {
						columnTable.getColumnModel().getColumn(columnIndex).setPreferredWidth(12);
						return Boolean.class;
					}
					return super.getColumnClass(columnIndex);
				}

				@Override
				public boolean isCellEditable(int row, int column) {
					List<Integer> collect = Arrays.stream(TableHeaderEnum.values())
							.filter(TableHeaderEnum::isEditable)
							.map(TableHeaderEnum::getIndex)
							.toList();
					if (collect.contains(column)) {
						return true;
					}
					return false;
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
			columnTable.setRowHeight(25);
			columnTable.getColumnModel().getColumn(0).setPreferredWidth(12);
			columnTable.getColumnModel().getColumn(5).setPreferredWidth(100);

			TableHeaderEnum[] values = TableHeaderEnum.values();
			for (TableHeaderEnum value : values) {
				if (!value.isVisible()) {
					TableColumn column = columnTable.getColumnModel().getColumn(value.getIndex());
					//隐藏列
					column.setMinWidth(0);
					column.setMaxWidth(0);
					column.setWidth(0);
				}
				// 设置表单类型为下拉框
				if (value.isComboBox()) {
					TableColumn column = columnTable.getColumnModel().getColumn(value.getIndex());
					JComboBox<String> comboBoxColumn = new ComboBox<>(value.comboBoxOptions());
					column.setCellEditor(new DefaultCellEditor(comboBoxColumn));
				}
			}

			//字典
			List<String> collect = dictNames.stream().map(SysDict::getName).collect(Collectors.toList());
			collect.add(GenerateConstant.DEFAULT_TEXT);
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
	protected Action @NotNull [] createActions() {
		return new Action[0];
	}
}
