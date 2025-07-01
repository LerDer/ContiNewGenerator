package top.continew.ui;

import com.alibaba.fastjson.JSONObject;
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
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.util.DateUtils;
import top.continew.constant.GenerateConstant;
import top.continew.entity.SqlColumn;
import top.continew.entity.SysDict;
import top.continew.enums.FormTypeEnum;
import top.continew.enums.JavaTypeEnum;
import top.continew.enums.QueryTypeEnum;
import top.continew.persistent.ContiNewGeneratorPersistent;
import top.continew.utils.CommonUtil;
import top.continew.utils.DataSourceUtils;
import top.continew.utils.NotificationUtil;
import top.continew.utils.PluginIconsUtils;

/**
 * @author lww
 * @date 2025-06-30 18:17
 */
public class TableGenerate extends DialogWrapper {

	private JPanel rootPanel;
	private JTable columnTable;
	private JButton returnButton;
	private JButton generateButton;
	private static final String[] columnList;
	//{"输入框", "数字输入框", "密码输入框", "文本域", "下拉框","单选框","开关","复选框","树选择","时间框","日期框","日期时间框","图片上传","文件上传","富文本框","地图选择"};
	private static final String[] FORM_TYPE_OPTIONS = Arrays.stream(FormTypeEnum.values()).map(FormTypeEnum::getDescription).toArray(String[]::new);
	// {"String", "Integer", "Long","Float","Double","Boolean","BigDecimal","LocalDate","LocalTime","LocalDateTime"};
	private static final String[] JAVA_TYPE_OPTIONS = Arrays.stream(JavaTypeEnum.values()).map(JavaTypeEnum::getDescription).toArray(String[]::new);
	// {"等于", "不等于", "大于", "大于等于", "小于", "小于等于", "模糊查询", "范围查询", "包含", "开始于", "结束于"};
	private static final String[] QUERY_TYPE_OPTIONS = Arrays.stream(QueryTypeEnum.values()).map(QueryTypeEnum::getDescription).toArray(String[]::new);

	private Map<String, Object> dictMap = new HashMap<>();

	private static final String defaultText = "请选择";


	static {
		columnList = new String[]{
				"序号", "列名称", "字段名称", "列类型", "Java类型", "描述", "列表", "表单", "必填", "表单类型", "查询", "查询方式", "关联字典", "长度"
		};
	}

	public TableGenerate(Project project, VirtualFile vf, Object selectedItem, Object moduleSelectItem) {
		super(project);
		setTitle("ContiNew Generator");
		setModal(true);
		setResizable(false);
		this.setSize(1080, 720);
		this.init();
		showTable(project, vf, selectedItem);
		generateButton.setIcon(PluginIconsUtils.success);
		returnButton.setIcon(PluginIconsUtils.sendToTheLeft);
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
		}else {
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
				if (columnName.equals("序号")) {
					fieldConfig.put("id", columnTable.getValueAt(i, j).toString());
					fieldConfig.put("fieldSort", columnTable.getValueAt(i, j).toString());
				}
				// 列名称
				if (columnName.equals("列名称")) {
					fieldConfig.put("columnName", columnTable.getValueAt(i, j).toString());
				}
				// 列类型
				if (columnName.equals("列类型")) {
					fieldConfig.put("columnType", columnTable.getValueAt(i, j).toString());
					if (CommonUtil.isDateTimeType(columnTable.getValueAt(i, j).toString()) || CommonUtil.isDateType(columnTable.getValueAt(i, j).toString())) {
						dataModel.put("hasTimeField", true);
					}
				}
				// 字段名称
				if (columnName.equals("字段名称")) {
					fieldConfig.put("fieldName", columnTable.getValueAt(i, j).toString());
				}
				// Java类型
				if (columnName.equals("Java类型")) {
					fieldConfig.put("fieldType", columnTable.getValueAt(i, j).toString());
					if (columnTable.getValueAt(i, j).toString().equalsIgnoreCase("BigDecimal")) {
						dataModel.put("hasBigDecimalField", true);
					}
				}
				// 描述
				if (columnName.equals("描述")) {
					fieldConfig.put("comment", columnTable.getValueAt(i, j).toString());
				}
				// 必填
				if (columnName.equals("必填")) {
					fieldConfig.put("isRequired", columnTable.getValueAt(i, j));
					if (columnTable.getValueAt(i, j).equals(Boolean.TRUE)) {
						dataModel.put("hasRequiredField", true);
					}
				}
				// 列表
				if (columnName.equals("列表")) {
					fieldConfig.put("showInList", columnTable.getValueAt(i, j));
				}
				// 表单
				if (columnName.equals("表单")) {
					fieldConfig.put("showInForm", columnTable.getValueAt(i, j));
				}
				// 查询
				if (columnName.equals("查询")) {
					fieldConfig.put("showInQuery", columnTable.getValueAt(i, j));
				}
				// 表单类型
				if (columnName.equals("表单类型")) {
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
				}
				// 查询方式
				if (columnName.equals("查询方式")) {
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
				}
				// 关联字典
				if (columnName.equals("关联字典")) {
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
				}
				// 长度
				if (columnName.equals("长度")) {
					Object valueAt = columnTable.getValueAt(i, j);
					if (valueAt != null) {
						fieldConfig.put("columnSize", Integer.valueOf(valueAt.toString()));
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

		String jsonString = JSONObject.toJSONString(dataModel);
		System.out.println("jsonString = " + jsonString);
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
		NotificationUtil.showInfoNotification(project, "生成成功", "生成成功");
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
				} else {
					column[7] = Boolean.TRUE;
				}
				//必填
				if (Arrays.asList(GenerateConstant.requiredExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
					column[8] = Boolean.FALSE;
				} else {
					column[8] = Boolean.TRUE;
				}
				//表单类型
				if (Arrays.asList(GenerateConstant.formExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
					column[9] = defaultText;
				} else {
					if (sqlColumn.isString()) {
						column[9] = "输入框";
						} else if (sqlColumn.isDate()) {
							column[9] = "日期框";
						} else if (sqlColumn.isDatetime()) {
							column[9] = "日期时间框";
						} else if (sqlColumn.isBool()) {
							column[9] = "开关";
						} else if (sqlColumn.isNumber()) {
							column[9] = "数字输入框";
					} else {
						column[9] = defaultText;
					}
				}
				//查询
				if (Arrays.asList(GenerateConstant.queryExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
					column[10] = Boolean.FALSE;
				} else {
					// 数字类型默认支持查询 减少选择项
					if (sqlColumn.isNumber()) {
						column[10] = Boolean.TRUE;
					} else {
						column[10] = Boolean.FALSE;
					}
				}
				//查询方式
				if (Arrays.asList(GenerateConstant.queryExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
					column[11] = defaultText;
				} else {
					if (sqlColumn.isNumber()) {
						column[11] = "=";
					//} else if (sqlColumn.isDate()) {
					//	column[11] = "BETWEEN";
					//} else if (sqlColumn.isDatetime()) {
					//	column[11] = "BETWEEN";
					//} else if (sqlColumn.isBool()) {
					//	column[11] = "=";
					//} else if (sqlColumn.isString()) {
					//	column[11] = "LIKE '%s%'";
					} else {
						column[11] = defaultText;
					}
				}
				//关联字典
				column[12] = defaultText;
				data[columns.indexOf(sqlColumn)] = column;
				column[13] = sqlColumn.getCharacterMaximumLength();
			}

			// 创建表格模型
			DefaultTableModel model = new DefaultTableModel(data, columnList) {
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if (columnIndex == 6 || columnIndex == 7 || columnIndex == 8 || columnIndex == 10) {
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
			columnTable.getColumnModel().getColumn(0).setPreferredWidth(12);
			columnTable.getColumnModel().getColumn(5).setPreferredWidth(100);
			columnTable.getColumnModel().getColumn(6).setPreferredWidth(12);
			columnTable.getColumnModel().getColumn(7).setPreferredWidth(12);
			columnTable.getColumnModel().getColumn(8).setPreferredWidth(12);
			columnTable.getColumnModel().getColumn(10).setPreferredWidth(12);
			// 设置表单类型为下拉框
			TableColumn javaTypeColumn = columnTable.getColumnModel().getColumn(4);
			JComboBox<String> javaTypeComboBox = new ComboBox<>(JAVA_TYPE_OPTIONS);
			javaTypeColumn.setCellEditor(new DefaultCellEditor(javaTypeComboBox));

			TableColumn formTypeColumn = columnTable.getColumnModel().getColumn(9);
			JComboBox<String> formTypeComboBox = new ComboBox<>(FORM_TYPE_OPTIONS);
			formTypeColumn.setCellEditor(new DefaultCellEditor(formTypeComboBox));

			TableColumn queryTypeColumn = columnTable.getColumnModel().getColumn(11);
			JComboBox<String> queryTypeComboBox = new ComboBox<>(QUERY_TYPE_OPTIONS);
			queryTypeColumn.setCellEditor(new DefaultCellEditor(queryTypeComboBox));

			List<String> collect = dictNames.stream().map(SysDict::getName).collect(Collectors.toList());
			collect.add(defaultText);
			Collections.reverse(collect);
			TableColumn dictColumn = columnTable.getColumnModel().getColumn(12);
			JComboBox<String> dictComboBox = new ComboBox<>(collect.toArray(new String[0]));
			dictColumn.setCellEditor(new DefaultCellEditor(dictComboBox));

			TableColumn column = columnTable.getColumnModel().getColumn(13);
			//隐藏列
			column.setMinWidth(0);
			column.setMaxWidth(0);
			column.setWidth(0);
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
