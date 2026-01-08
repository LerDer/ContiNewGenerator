package top.continew.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import top.continew.config.ContiNewConfigPersistent;
import top.continew.constant.GenerateConstant;
import top.continew.entity.SqlColumn;
import top.continew.entity.SysDict;
import top.continew.entity.TableIndex;
import top.continew.enums.FormTypeEnum;
import top.continew.enums.QueryTypeEnum;
import top.continew.enums.TableHeaderEnum;
import top.continew.format.IdeaCodeFormatter;
import top.continew.handler.DBTypeEnum;
import top.continew.icon.PluginIcons;
import top.continew.persistent.ContiNewGeneratorPersistent;
import top.continew.persistent.TableGeneratePersistent;
import top.continew.persistent.table.FieldProperties;
import top.continew.utils.CommonUtil;
import top.continew.utils.DateUtils;
import top.continew.utils.NotificationUtil;
import top.continew.utils.RunSqlScriptUtil;
import top.continew.version.CommonTemplateEnum;
import top.continew.version.JavaTemplateEnum;
import top.continew.version.TemplateEnum;
import top.continew.version.VueTemplateEnum;

/**
 * @author lww
 * @date 2025-06-30 18:17
 */
public class TableGenerate extends DialogWrapper {

	private JPanel rootPanel;
	private JTable columnTable;
	private JButton returnButton;
	private JButton generateButton;
	private JButton previewButton;
	private JButton refreshButton;
	public static volatile String static_className = "";
	public static List<TemplateEnum> comonEnums = Arrays.asList(CommonTemplateEnum.values());
	public static List<TemplateEnum> vueEnums = Arrays.asList(VueTemplateEnum.values());
	public static List<TemplateEnum> javaEnums = Arrays.asList(JavaTemplateEnum.values());
	public static volatile List<TemplateEnum> fileList = new ArrayList<>();
	private Map<String, Object> dictMap = new HashMap<>();

	public TableGenerate(Project project, VirtualFile vf, Object selectedItem, Object moduleSelectItem) {
		super(project);
		setTitle("ContiNew Generator");
		setModal(false);
		setResizable(false);
		this.setSize(1080, 720);
		this.init();
		showTable(project, vf, selectedItem);
		generateButton.setIcon(PluginIcons.success);
		returnButton.setIcon(PluginIcons.sendToTheLeft);
		returnButton.addActionListener(e -> dispose());
		generateButton.addActionListener(e -> generateCode(project, selectedItem, moduleSelectItem));
		previewButton.setIcon(PluginIcons.showLogs);
		previewButton.addActionListener(e -> previewCode(project, selectedItem, moduleSelectItem));
		refreshButton.setIcon(PluginIcons.forceRefresh);
		refreshButton.setToolTipText("清除缓存，从数据库重新获取表结构!");
		refreshButton.addActionListener(e -> forceRefreshTable(project, vf, selectedItem));
	}

		private void forceRefreshTable(Project project, VirtualFile vf, Object selectedItem) {
		String tableName;
		if (selectedItem == null) {
			NotificationUtil.showErrorNotification(project, "错误", "未选择表");
			return;
		}
		if (selectedItem.toString().indexOf(" - ") > 0) {
			tableName = selectedItem.toString().split(" - ")[0];
		} else {
			tableName = selectedItem.toString();
		}
		TableGeneratePersistent tablePersistent = TableGeneratePersistent.getInstance(project);
		Map<String, Map<String, FieldProperties>> propertiesMap = tablePersistent.getFieldPropertiesMap();
		if (propertiesMap != null) {
			propertiesMap.remove(tableName);
		}
		showTable(project, vf, selectedItem);
	}

	private void previewCode(Project project, Object selectedItem, Object moduleSelectItem) {
		Map<String, Object> dataModel = getDataModel(project, selectedItem, moduleSelectItem);
		PreviewUI previewUI = new PreviewUI(project, dataModel, fileList);
		previewUI.show();
	}

	private Map<String, Object> getDataModel(Project project, Object selectedItem, Object moduleSelectItem) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		String projectPath = instance.getProjectPath();
		String vuePath = instance.getVuePath();
		String author = instance.getAuthor();
		String packageName = instance.getPackageName();
		String businessName = instance.getBusinessName();
		String dbType = instance.getDbType();
		String tablePrefix = instance.getTablePrefix();
		String version = instance.getVersion();

		boolean override = instance.isOverride();
		boolean noBaseClass = instance.isNoBaseClass();
		boolean serviceMP = instance.isServiceMP();
		boolean controllerNoApi = instance.isControllerNoApi();

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
		static_className = className;
		Map<String, Object> dataModel = new HashMap<>();

		List<Object> dictCodes = new ArrayList<>();
		dataModel.put("noBase", noBaseClass);
		dataModel.put("NoApi", controllerNoApi);
		dataModel.put("mpService", serviceMP);

		dataModel.put("dbType", dbType);
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

		dataModel.put("hasBigDecimalField", false);
		dataModel.put("hasTimeField", false);
		dataModel.put("hasDictField", false);
		dataModel.put("hasRequiredField", false);

		dataModel.put("doExcludeFields", GenerateConstant.doExcludeFields.split(","));
		dataModel.put("respExcludeFields", GenerateConstant.respExcludeFields.split(","));
		dataModel.put("detailRespExcludeFields", GenerateConstant.detailRespExcludeFields.split(","));

		TableGeneratePersistent tablePersistent = TableGeneratePersistent.getInstance(project);
		Map<String, Map<String, FieldProperties>> propertiesMap = tablePersistent.getFieldPropertiesMap();
		if (propertiesMap == null) {
			propertiesMap = new HashMap<>();
			tablePersistent.setFieldPropertiesMap(propertiesMap);
		}
		//先移除之前的配置
		propertiesMap.remove(tableName);
		Map<String, FieldProperties> fieldPropertiesMap = new HashMap<>();
		propertiesMap.put(tableName, fieldPropertiesMap);
		List<Map<String, Object>> fieldConfigs = new ArrayList<>();
		dataModel.put("fieldConfigs", fieldConfigs);
		Map<String, Object> fieldConfig;
		TableColumnModel columnModel = columnTable.getColumnModel();
		int rowCount = columnTable.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			FieldProperties fieldProperties = new FieldProperties();
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
					fieldPropertiesMap.put(columnTable.getValueAt(i, j).toString(), fieldProperties);
					continue;
				}
				// 字段名称
				if (columnName.equals(TableHeaderEnum.CODE_NAME.getDescription())) {
					fieldConfig.put("fieldName", columnTable.getValueAt(i, j).toString());
					fieldProperties.setCodeName(columnTable.getValueAt(i, j).toString());
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
					fieldProperties.setCodeType(columnTable.getValueAt(i, j).toString());
					if (columnTable.getValueAt(i, j).toString().equalsIgnoreCase("BigDecimal")) {
						dataModel.put("hasBigDecimalField", true);
					}
					continue;
				}
				// 描述
				if (columnName.equals(TableHeaderEnum.DESCRIPTION.getDescription())) {
					fieldConfig.put("comment", columnTable.getValueAt(i, j).toString());
					fieldProperties.setDescription(columnTable.getValueAt(i, j).toString());
					continue;
				}
				// 列表
				if (columnName.equals(TableHeaderEnum.TABLE_LIST.getDescription())) {
					fieldConfig.put("showInList", columnTable.getValueAt(i, j));
					fieldProperties.setTableList((Boolean) columnTable.getValueAt(i, j));
					continue;
				}
				// 表单
				if (columnName.equals(TableHeaderEnum.TABLE_FORM.getDescription())) {
					fieldConfig.put("showInForm", columnTable.getValueAt(i, j));
					fieldProperties.setTableForm((Boolean) columnTable.getValueAt(i, j));
					continue;
				}
				// 必填
				if (columnName.equals(TableHeaderEnum.REQUIRED.getDescription())) {
					fieldConfig.put("isRequired", columnTable.getValueAt(i, j));
					fieldProperties.setRequired((Boolean) columnTable.getValueAt(i, j));
					if (columnTable.getValueAt(i, j).equals(Boolean.TRUE)) {
						dataModel.put("hasRequiredField", true);
					}
					continue;
				}
				// 查询
				if (columnName.equals(TableHeaderEnum.QUERY_FIELD.getDescription())) {
					fieldConfig.put("showInQuery", columnTable.getValueAt(i, j));
					fieldProperties.setQueryField((Boolean) columnTable.getValueAt(i, j));
					continue;
				}
				// 查询方式
				if (columnName.equals(TableHeaderEnum.QUERY_TYPE.getDescription())) {
					String queryType = columnTable.getValueAt(i, j).toString();
					if (StringUtils.isNotBlank(queryType)) {
						QueryTypeEnum typeEnum = QueryTypeEnum.getByDes(queryType);
						if (typeEnum != null) {
							fieldConfig.put("queryType", typeEnum.name());
							fieldProperties.setQueryType(typeEnum.name());
						} else {
							fieldConfig.put("queryType", "");
							fieldProperties.setQueryType("NO");
						}
					} else {
						fieldConfig.put("queryType", "");
						fieldProperties.setQueryType("NO");
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
							fieldProperties.setFormShowType(typeEnum.name());
						} else {
							fieldConfig.put("formType", "");
							fieldProperties.setFormShowType("NO");
						}
					} else {
						fieldConfig.put("formType", "");
						fieldProperties.setFormShowType("NO");
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
							fieldProperties.setRelationDict((String) o);
							dictCodes.add(o);
						} else {
							fieldConfig.put("dictCode", "");
							fieldProperties.setRelationDict("");
						}
					} else {
						fieldConfig.put("dictCode", "");
						fieldProperties.setRelationDict("");
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
					continue;
				}
				if (columnName.equals(TableHeaderEnum.UNIQUE.getDescription())) {
					Object valueAt = columnTable.getValueAt(i, j);
					if (Boolean.TRUE.equals(valueAt)) {
						fieldConfig.put("isUnique", Boolean.TRUE);
					} else {
						fieldConfig.put("isUnique", Boolean.FALSE);
					}
					continue;
				}
			}
		}
		fileList.clear();
		if (StringUtils.isNotBlank(projectPath)) {
			fileList.addAll(Stream.of(JavaTemplateEnum.values()).filter(e -> e.getVersion().equals(version)).toList());
			fileList.addAll(Stream.of(CommonTemplateEnum.values()).filter(e -> e.getVersion().equals(version)).toList());
		}
		if (StringUtils.isNotBlank(vuePath)) {
			fileList.addAll(Stream.of(VueTemplateEnum.values()).filter(e -> e.getVersion().equals(version)).toList());
		}
		return dataModel;
	}

	public static String previewCodeString(Map<String, Object> dataModel, TemplateEnum template) {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
		BeansWrapper build = new BeansWrapperBuilder(Configuration.VERSION_2_3_28).build();
		cfg.setSharedVariable("statics", build.getStaticModels());
		// 设置模板所在目录
		cfg.setClassForTemplateLoading(TableGenerate.class, "/templates");
		return generateFilString(cfg, dataModel, template.getTemplatePath());
	}

	private static String generateFilString(Configuration cfg, Map<String, Object> dataModel, String templatePath) {
		try (Writer out = new StringWriter()) {
			Template template = cfg.getTemplate(templatePath);
			template.process(dataModel, out);
			// 输出结果
			return out.toString();
		} catch (IOException | TemplateException e) {
			throw new RuntimeException(e);
		}
	}

	public static void generateCode(Project project, String content, TemplateEnum templateEnum, String className, String moduleName) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		String projectPath = instance.getProjectPath();
		String vuePath = instance.getVuePath();
		String packageName = instance.getPackageName();
		String javaPath = projectPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + packageName.replace(".", File.separator);
		String resourcesPath = projectPath + File.separator + "src" + File.separator + "main" + File.separator + "resources";
		String fileName = templateEnum.getFileName().formatted(className);
		String packageName1 = templateEnum.getPackageName().formatted(className).replace(".", File.separator);
		File file = null;
		if (comonEnums.contains(templateEnum)) {
			if (StringUtils.isNotBlank(packageName1)) {
				file = new File(resourcesPath + File.separator + packageName1 + File.separator + fileName);
			} else {
				file = new File(resourcesPath + File.separator + fileName);
			}
		} else if (vueEnums.contains(templateEnum)) {
			String packageName2 = templateEnum.getPackageName().formatted(className)
					.replace(".", File.separator)
					+ (StringUtils.isNotBlank(moduleName) ? File.separator + moduleName : "");
			if (templateEnum.className2Folder()) {
				packageName2 += File.separator + className;
			}
			file = new File(vuePath + File.separator + packageName2 + File.separator + fileName);
		} else if (javaEnums.contains(templateEnum)) {
			file = new File(javaPath + File.separator + packageName1 + File.separator + fileName);
		} else {
			NotificationUtil.showWarningNotification(project, "模板类型错误", "模板类型错误");
			return;
		}
		try {
			FileUtils.write(file, content, StandardCharsets.UTF_8);
		} catch (IOException e) {
			NotificationUtil.showErrorNotification(project, "生成失败", "生成失败：" + e.getMessage());
		}
		NotificationUtil.showInfoNotification(project, "生成成功", "生成成功");
	}

	private void generateCode(Project project, Object selectedItem, Object moduleSelectItem) {
		Map<String, Object> dataModel = getDataModel(project, selectedItem, moduleSelectItem);
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		ContiNewConfigPersistent configPersistent = ContiNewConfigPersistent.getInstance();
		String projectPath = instance.getProjectPath();
		String vuePath = instance.getVuePath();
		String packageName = instance.getPackageName();
		boolean isOverride = instance.isOverride();
		Boolean autoRunSql = configPersistent.getAutoRunSql();
		String version = instance.getVersion();
		String className = dataModel.get("className") + "";
		// 使用与依赖相同的版本
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
		BeansWrapper build = new BeansWrapperBuilder(Configuration.VERSION_2_3_28).build();
		cfg.setSharedVariable("statics", build.getStaticModels());
		// 设置模板所在目录
		cfg.setClassForTemplateLoading(TableGenerate.class, "/templates");
		AtomicReference<File> sqlFile = new AtomicReference<>(null);
		//String jsonString = JSONObject.toJSONString(dataModel);
		//System.out.println("jsonString = " + jsonString);
		//java
		if (StringUtils.isNotBlank(projectPath)) {
			String javaTargetPath =
					projectPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + packageName.replace(".", File.separator);
			String resourcesPath = projectPath + File.separator + "src" + File.separator + "main" + File.separator + "resources";
			String finalClassName = className;
			Arrays.stream(JavaTemplateEnum.values())
					.filter(e -> e.getVersion().equals(version))
					.forEach(e -> {
						String fileName = e.getFileName().formatted(finalClassName);
						if (e.firstToLowerCase()) {
							fileName = CommonUtil.firstToLowerCase(fileName);
						}
						String packageName1 = e.getPackageName().formatted(finalClassName).replace(".", File.separator);
						String templatePath = e.getTemplatePath();
						File file = new File(javaTargetPath + File.separator + packageName1 + File.separator + fileName);
						if (!file.exists() || isOverride) {
							generateFile(cfg, dataModel, templatePath, file);
						}
					});
			Arrays.stream(CommonTemplateEnum.values())
					.filter(e -> e.getVersion().equals(version))
					.forEach(e -> {
						String fileName = e.getFileName().formatted(finalClassName);
						if (e.firstToLowerCase()) {
							fileName = CommonUtil.firstToLowerCase(fileName);
						}
						String packageName1 = e.getPackageName().formatted(finalClassName).replace(".", File.separator);
						String templatePath = e.getTemplatePath();
						File file = new File(resourcesPath + File.separator + packageName1 + File.separator + fileName);
						if (!file.exists() || isOverride) {
							generateFile(cfg, dataModel, templatePath, file);
						}
						if (file.getName().endsWith("Menu.sql")) {
							sqlFile.set(file);
						}
					});
		}
		if (StringUtils.isNotBlank(vuePath)) {
			String finalClassName1 = className;
			Arrays.stream(VueTemplateEnum.values())
					.filter(e -> e.getVersion().equals(version))
					.forEach(e -> {
						String fileName = e.getFileName().formatted(finalClassName1);
						if (e.firstToLowerCase()) {
							fileName = CommonUtil.firstToLowerCase(fileName);
						}
						String packageName1 = e.getPackageName().formatted(finalClassName1).replace(".", File.separator)
								+ (StringUtils.isNotBlank(moduleSelectItem + "") ? File.separator + moduleSelectItem : "");
						if (e.className2Folder()) {
							packageName1 += File.separator + CommonUtil.firstToLowerCase(finalClassName1);
						}
						String templatePath = e.getTemplatePath();
						File file = new File(vuePath + File.separator + packageName1 + File.separator + fileName);
						if (!file.exists() || isOverride) {
							generateFile(cfg, dataModel, templatePath, file);
						}
					});
		}
		JOptionPane.showMessageDialog(rootPanel, "代码生成成功,快去看看吧!", "生成成功!", JOptionPane.INFORMATION_MESSAGE);
		if (autoRunSql != null && autoRunSql) {
			RunSqlScriptUtil.runSqlScript(sqlFile.get(), project);
		}
		this.dispose();
	}

	private void generateFile(Configuration cfg, Map<String, Object> dataModel, String templatePath, File file) {
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
			System.out.println(file.getPath() + ":" + result);
			IdeaCodeFormatter.formatFile(file);
		} catch (IOException | TemplateException e) {
			throw new RuntimeException(e);
		}
	}

	private void showTable(Project project, VirtualFile vf, Object selectedItem) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		ContiNewConfigPersistent configPersistent = ContiNewConfigPersistent.getInstance();
		String resExcludeFields =
				StringUtils.isNotBlank(configPersistent.getResponseExcludeText()) ? configPersistent.getResponseExcludeText() : GenerateConstant.resExcludeFields;
		String formExcludeFields =
				StringUtils.isNotBlank(configPersistent.getRequestExcludeText()) ? configPersistent.getRequestExcludeText() : GenerateConstant.formExcludeFields;
		String requiredExcludeFields =
				StringUtils.isNotBlank(configPersistent.getRequiredExcludeText()) ? configPersistent.getRequiredExcludeText() : GenerateConstant.requiredExcludeFields;
		String queryExcludeFields =
				StringUtils.isNotBlank(configPersistent.getQueryExcludeText()) ? configPersistent.getQueryExcludeText() : GenerateConstant.queryExcludeFields;
		String stringType = StringUtils.isNotBlank(configPersistent.getStringType()) ? configPersistent.getStringType() : GenerateConstant.stringTypeLike;
		String numberType = StringUtils.isNotBlank(configPersistent.getNumberType()) ? configPersistent.getNumberType() : GenerateConstant.numberType;
		String dateType = StringUtils.isNotBlank(configPersistent.getDateType()) ? configPersistent.getDateType() : GenerateConstant.dateType;
		String booleanType = StringUtils.isNotBlank(configPersistent.getBooleanType()) ? configPersistent.getBooleanType() : GenerateConstant.booleanType;

		DBTypeEnum dbTypeEnum = DBTypeEnum.valueOf(instance.getDbType());
		columnTable.setDoubleBuffered(true);
		String tableName;
		Object[][] data;
		if (selectedItem != null) {
			if (selectedItem.toString().indexOf(" - ") > 0) {
				tableName = selectedItem.toString().split(" - ")[0];
			} else {
				tableName = selectedItem.toString();
			}
			List<SqlColumn> columns = dbTypeEnum.getHandler().getSqlTablesColumns(project, vf, tableName);
			List<TableIndex> sqlTablesIndex = dbTypeEnum.getHandler().getSqlTablesIndex(project, vf, tableName);
			List<SysDict> dictNames = dbTypeEnum.getHandler().getDictNames(project, vf);

			List<String> uniqueIndexList = sqlTablesIndex
					.stream()
					.filter(e -> e.getNonUnique() == 0)
					.map(TableIndex::getColumnName)
					.toList();
			dictMap = dictNames.stream().collect(Collectors.toMap(SysDict::getName, SysDict::getCode));
			data = new Object[columns.size()][];

			TableGeneratePersistent tablePersistent = TableGeneratePersistent.getInstance(project);
			Map<String, Map<String, FieldProperties>> propertiesMap = tablePersistent.getFieldPropertiesMap();
			if (propertiesMap == null) {
				propertiesMap = new HashMap<>();
			}
			Map<String, FieldProperties> fieldPropertiesMap = propertiesMap.get(tableName);
			if (fieldPropertiesMap != null) {
				for (int i = 0; i < columns.size(); i++) {
					SqlColumn sqlColumn = columns.get(i);
					Object[] column = new Object[GenerateConstant.COLUMN_LIST.length];
					//序号
					column[0] = i + 1;
					//名称
					column[1] = sqlColumn.getColumnName();
					//字段名称
					FieldProperties properties = fieldPropertiesMap.get(sqlColumn.getColumnName());
					column[2] = properties.getCodeName();
					//类型
					column[3] = sqlColumn.getDataType();
					//Java类型
					column[4] = properties.getCodeType();
					//描述
					column[5] = properties.getDescription();
					//列表
					column[6] = properties.isTableList();
					//表单
					column[7] = properties.isTableForm();
					//必填
					column[8] = properties.isRequired();
					//查询
					column[9] = properties.isQueryField();
					//查询方式
					String queryType = properties.getQueryType();
					QueryTypeEnum queryTypeEnum = QueryTypeEnum.valueOf(queryType);
					column[10] = queryTypeEnum.getDescription();
					//表单类型
					String formShowType = properties.getFormShowType();
					FormTypeEnum formTypeEnum = FormTypeEnum.valueOf(formShowType);
					column[11] = formTypeEnum.getDescription();
					//关联字典
					String relationDict = properties.getRelationDict();
					if (StringUtils.isNotBlank(relationDict)) {
						dictMap.entrySet()
								.stream()
								.filter(e -> e.getValue().equals(relationDict))
								.findFirst()
								.ifPresent(e -> column[12] = e.getKey());
					} else {
						column[12] = GenerateConstant.DEFAULT_TEXT;
					}
					data[columns.indexOf(sqlColumn)] = column;
					column[13] = sqlColumn.getCharacterMaximumLength();
					column[14] = sqlColumn.isPrimaryKey();
					column[15] = sqlColumn.isNULLAble();
					if (uniqueIndexList.contains(sqlColumn.getColumnName()) && !sqlColumn.isPrimaryKey()) {
						column[16] = Boolean.TRUE;
					} else {
						column[16] = Boolean.FALSE;
					}
				}
			} else {
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
					if (Arrays.asList(resExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
						column[6] = Boolean.FALSE; // 设置为CheckBox
					} else {
						column[6] = Boolean.TRUE;
					}
					//表单
					if (Arrays.asList(formExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
						column[7] = Boolean.FALSE;
					} else {
						column[7] = Boolean.TRUE;
					}
					//必填
					if (Arrays.asList(requiredExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
						column[8] = Boolean.FALSE;
					} else {
						column[8] = Boolean.TRUE;
					}
					//查询
					if (Arrays.asList(queryExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
						column[9] = Boolean.FALSE;
					} else {
						// 数字类型默认支持查询 减少选择项
						column[9] = Boolean.TRUE;
						//if (sqlColumn.isNumber()) {
						//} else {
						//	column[9] = Boolean.FALSE;
						//}
					}
					//查询方式
					if (Arrays.asList(queryExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
						column[10] = GenerateConstant.DEFAULT_TEXT;
					} else {
						if (sqlColumn.isNumber()) {
							column[10] = numberType;
						} else if (sqlColumn.isDate() || sqlColumn.isDatetime()) {
							column[10] = dateType;
						} else if (sqlColumn.isBool()) {
							column[10] = booleanType;
						} else if (sqlColumn.isString()) {
							column[10] = stringType;
						} else {
							column[10] = GenerateConstant.DEFAULT_TEXT;
						}
					}
					//表单类型
					if (Arrays.asList(formExcludeFields.split(",")).contains(sqlColumn.getJavaField())) {
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
					if (uniqueIndexList.contains(sqlColumn.getColumnName()) && !sqlColumn.isPrimaryKey()) {
						column[16] = Boolean.TRUE;
					} else {
						column[16] = Boolean.FALSE;
					}
				}
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
					return collect.contains(column);
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
