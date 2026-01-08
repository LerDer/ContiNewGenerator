package top.continew.ui;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import top.continew.entity.SqlTable;
import top.continew.handler.DBTypeEnum;
import top.continew.icon.PluginIcons;
import top.continew.persistent.ContiNewGeneratorPersistent;
import top.continew.utils.DataSourceUtils;
import top.continew.utils.FileChooseUtils;
import top.continew.utils.NotificationUtil;

/**
 * @author lww
 * @date 2025-06-30 11:28
 */
public class MainGenerator extends DialogWrapper {

	private JPanel rootPanel;
	private JTextField projectPathTextField;
	private JButton selectPathButton;
	private JLabel projectPathLabel;
	private JLabel authorLabel;
	private JTextField authorTextField;
	private JLabel packageNameLabel;
	private JTextField packageNameTextField;
	private JLabel tablePrefixLabel;
	private JTextField tablePrefixTextField;
	private AutoCompleteComboBox tableNameTextField;
	private JLabel tableLabel;
	private JButton cancelButton;
	private JButton nextButton;
	private JTextField configFilePathTextField;
	private JButton configFilePathButton;
	private JLabel configFilePathLabel;
	private JCheckBox overrideCheckBox;
	private JTextField businessNameTextField;
	private JLabel businessNameLabel;
	private AutoCompleteComboBox moduleComboBox;
	private JTextField vuePathTextField;
	private JButton vueSelectPathButton;
	private JButton selectPackageButton;
	private JLabel vuePathLabel;
	private AutoCompleteComboBox versionComboBox;
	private JCheckBox controllerNoApiCheckBox;
	private JCheckBox serviceMPCheckBox;
	private JCheckBox noBaseClassCheckBox;
	private JButton donationButton;
	private JButton cleanButton;

	public MainGenerator(Project project) {
		super(project);
		setTitle("ContiNew Generator");
		setModal(false);
		setResizable(false);
		this.init();
		initVersion();
		reShow(project);
		donationButton.setIcon(PluginIcons.donation);
		donationButton.setRolloverIcon(PluginIcons.donation_enter);
		donationButton.setPressedIcon(PluginIcons.donation);
		//donationButton.setContentAreaFilled(false);
		//donationButton.setBorderPainted(false);
		donationButton.setToolTipText("Donation");

		configFilePathButton.setIcon(PluginIcons.yaml);
		configFilePathButton.addActionListener(e -> chooseConfigPath(project));
		selectPathButton.setIcon(PluginIcons.springBoot);
		selectPathButton.addActionListener(e -> chooseProjectPath(project));
		cancelButton.setIcon(PluginIcons.testFailed);
		cancelButton.addActionListener(e -> dispose());
		nextButton.setIcon(PluginIcons.sendToTheRight);
		nextButton.addActionListener(e -> nextStep(project));

		//获取当前项目的所有模块信息
		//Module[] modules = ProjectUtil.getModules(project);
		//String[] moduleNames = Arrays.stream(modules).map(Module::getName).toArray(String[]::new);
		//moduleComboBox.setModel(new DefaultComboBoxModel<>(moduleNames));
		vueSelectPathButton.setIcon(PluginIcons.vue);
		vueSelectPathButton.addActionListener(e -> chooseVuePath(project));
		tableNameTextField.addActionListener(e -> setBusinessNameAndPrefix(project));
		selectPackageButton.setIcon(PluginIcons.package1);
		selectPackageButton.addActionListener(e -> choosePackage(project));
		donationButton.addActionListener(e -> {
			Donation donation = new Donation(project);
			donation.show();
		});

		cleanButton.setIcon(PluginIcons.clean);
		cleanButton.addActionListener(e -> clean());
	}

	private void clean() {
		tableNameTextField.setSelectedIndex(-1);
	}

	private void initVersion() {
		List<String> versions = new ArrayList<>();
		versions.add("3.5.0");
		versions.add("3.6.0");
		versions.add("3.7.0");
		versions.add("4.0.0");
		versions.forEach(version -> versionComboBox.addItem(version));
		versionComboBox.setSelectedIndex(2);
		versionComboBox.getComboKeyHandler().setList(versions);
	}

	private void choosePackage(Project project) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		PackageChooserDialog dialog = new PackageChooserDialog("选择包名", project);
		dialog.show();
		if (dialog.isOK()) {
			String packageName = dialog.getSelectedPackage().getQualifiedName();
			packageNameTextField.setText(packageName);
			instance.setPackageName(packageName);
		}
	}

	private void setBusinessNameAndPrefix(Project project) {
		if (tableNameTextField.getSelectedItem() != null) {
			String tableNameSelect = tableNameTextField.getSelectedItem().toString();

			ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
			instance.setSelectTable(tableNameSelect);
			if (tableNameSelect.indexOf(" - ") > 0) {
				String tableComment = tableNameSelect.split(" - ")[1];
				businessNameTextField.setText(tableComment);
			}

			if (tableNameSelect.indexOf("_") > 0) {
				String tablePrefix = tableNameSelect.split("_")[0];
				tablePrefixTextField.setText(tablePrefix + "_");
			}
		}
	}

	private void reShow(Project project) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		//回显数据
		String projectPath = instance.getProjectPath();
		this.overrideCheckBox.setSelected(instance.isOverride());
		this.noBaseClassCheckBox.setSelected(instance.isNoBaseClass());
		this.controllerNoApiCheckBox.setSelected(instance.isControllerNoApi());
		this.serviceMPCheckBox.setSelected(instance.isServiceMP());
		if (StringUtils.isNotEmpty(instance.getVersion())) {
			this.versionComboBox.setSelectedItem(instance.getVersion());
		}
		if (StringUtils.isNotEmpty(projectPath)) {
			this.projectPathTextField.setText(projectPath);
			this.projectPathTextField.setToolTipText(projectPath);
		}
		String vuePath = instance.getVuePath();
		if (StringUtils.isNotBlank(vuePath)) {
			vuePathTextField.setText(vuePath);
			vuePathTextField.setToolTipText(vuePath);
			fillModules(project, LocalFileSystem.getInstance().findFileByIoFile(new File(vuePath)));
			String moduleName = instance.getModuleName();
			if (StringUtils.isNotBlank(moduleName)) {
				moduleComboBox.setSelectedItem(moduleName);
			}
		}
		String configPath = instance.getConfigPath();
		if (StringUtils.isNotEmpty(configPath)) {
			this.configFilePathTextField.setText(configPath);
			configFilePathTextField.setToolTipText(configPath);
			DataSourceUtils.setDataSource(null);
			DataSourceUtils.setDbName(null);
			fillTableSelect(project, LocalFileSystem.getInstance().findFileByIoFile(new File(configPath)));
		}
		String author = instance.getAuthor();
		if (StringUtils.isNotEmpty(author)) {
			this.authorTextField.setText(author);
		}
		String packageName = instance.getPackageName();
		if (StringUtils.isNotEmpty(packageName)) {
			this.packageNameTextField.setText(packageName);
		}
		String tableNameSelect = tableNameTextField.getSelectedItem() + "";
		if (StringUtils.isNotBlank(tableNameSelect)) {
			instance.setSelectTable(tableNameSelect);
		}
	}

	private void nextStep(Project project) {
		Object selectedItem = tableNameTextField.getSelectedItem();
		if (selectedItem == null) {
			NotificationUtil.showErrorNotification(project, "请选择表", "请选择表");
			return;
		}
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		instance.setProjectPath(projectPathTextField.getText());
		instance.setVuePath(vuePathTextField.getText());
		instance.setConfigPath(configFilePathTextField.getText());
		instance.setAuthor(authorTextField.getText());
		instance.setPackageName(packageNameTextField.getText());
		instance.setBusinessName(businessNameTextField.getText());
		instance.setOverride(overrideCheckBox.isSelected());
		instance.setNoBaseClass(noBaseClassCheckBox.isSelected());
		instance.setControllerNoApi(controllerNoApiCheckBox.isSelected());
		instance.setServiceMP(serviceMPCheckBox.isSelected());
		instance.setVersion(versionComboBox.getSelectedItem() + "");
		instance.setTablePrefix(tablePrefixTextField.getText());
		instance.setModuleName(moduleComboBox.getSelectedItem() + "");
		TableGenerate tableGenerate = new TableGenerate(project,
				LocalFileSystem.getInstance().findFileByIoFile(new File(this.configFilePathTextField.getText())),
				selectedItem,
				moduleComboBox.getSelectedItem());
		tableGenerate.show();
		this.dispose();
	}

	private void chooseProjectPath(Project project) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		FileChooseUtils uiComponentFacade = FileChooseUtils.getInstance(project);
		VirtualFile baseDir = ProjectUtil.guessProjectDir(project);
		final VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("选择项目路径", baseDir);
		if (null != vf) {
			this.projectPathTextField.setText(vf.getPath());
			this.projectPathTextField.setToolTipText(vf.getPath());
			instance.setProjectPath(vf.getPath());
		}
	}

	private void chooseConfigPath(Project project) {
		DataSourceUtils.resetDataSources();
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		FileChooseUtils uiComponentFacade = FileChooseUtils.getInstance(project);
		VirtualFile baseDir = ProjectUtil.guessProjectDir(project);
		String text = configFilePathTextField.getText();
		if (StringUtils.isNotEmpty(text)) {
			File file = new File(text);
			File parentFile = file.getParentFile();
			if (parentFile != null && parentFile.exists()) {
				baseDir = LocalFileSystem.getInstance().findFileByIoFile(parentFile);
			}
		}
		final VirtualFile vf = uiComponentFacade.showSingleFileSelectionDialog("选择配置文件", baseDir);
		if (null != vf) {
			String path = vf.getPath();
			this.configFilePathTextField.setText(path);
			configFilePathTextField.setToolTipText(path);
			instance.setConfigPath(path);
			fillTableSelect(project, vf);
			String version = getVersion(path, project);
			instance.setVersion(version);
		}
	}

	private String getVersion(String path, Project project) {
		try {
			//判断系统
			String property = System.getProperties().getProperty("os.name");
			String mainYml;
			if (property.contains("Windows")) {
				//} else if (property.contains("Mac")) {
				mainYml = path.substring(0, path.lastIndexOf("\\")) + "/application.yml";
			} else {
				mainYml = path.substring(0, path.lastIndexOf("/")) + "/application.yml";
			}
			if (new File(mainYml).exists()) {
				List<Map<String, Object>> docsList = new ArrayList<>();
				Yaml yaml = new Yaml();
				Iterable<Object> objects = yaml.loadAll(new FileReader(new File(mainYml)));
				for (Object doc : objects) {
					if (doc instanceof Map) {
						docsList.add((Map<String, Object>) doc);
					}
				}
				Optional<Map<String, Object>> first = docsList.stream().filter(dl -> dl.get("application") != null).findFirst();
				if (first.isPresent()) {
					Map<String, Object> application = (Map<String, Object>) first.get().get("application");
					String version = (String) application.get("version");
					if (version.startsWith("3.5")) {
						versionComboBox.setSelectedItem("3.5.0");
					} else if (version.startsWith("3.6")) {
						versionComboBox.setSelectedItem("3.6.0");
					} else if (version.startsWith("3.7")) {
						versionComboBox.setSelectedItem("3.7.0");
					} else if (version.startsWith("4.")) {
						versionComboBox.setSelectedItem("4.0.0");
					} else {
						versionComboBox.setSelectedItem("3.7.0");
					}
				} else {
					NotificationUtil.showErrorNotification(project, "未找到版本信息", "未找到版本信息");
				}
			}
		} catch (FileNotFoundException e) {
			NotificationUtil.showErrorNotification(project, "文件不存在", "application.yml文件不存在");
		}
		return versionComboBox.getSelectedItem() + "";
	}

	private void chooseVuePath(Project project) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		FileChooseUtils uiComponentFacade = FileChooseUtils.getInstance(project);
		VirtualFile baseDir = ProjectUtil.guessProjectDir(project);
		String text = vuePathTextField.getText();
		if (StringUtils.isNotEmpty(text)) {
			File file = new File(text);
			File parentFile = file.getParentFile();
			if (parentFile != null && parentFile.exists()) {
				baseDir = LocalFileSystem.getInstance().findFileByIoFile(parentFile);
			}
		}
		final VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("选择前端项目路径", baseDir);
		if (null != vf) {
			String path = vf.getPath();
			this.vuePathTextField.setText(path);
			vuePathTextField.setToolTipText(path);
			instance.setVuePath(path);
			fillModules(project, vf);
		}
	}

	private void fillModules(Project project, VirtualFile vf) {
		if (vf == null) {
			NotificationUtil.showWarningNotification(project, "vue项目路径为空", "vue项目路径为空");
			return;
		}
		String path = vf.getPath();
		File file = new File(path + "/src/views");
		List<String> modules = new ArrayList<>();
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File viewFile : files) {
					if (viewFile.isDirectory()) {
						modules.add(viewFile.getName());
						moduleComboBox.addItem(viewFile.getName());
					}
				}
				moduleComboBox.getComboKeyHandler().setList(modules);
			}
		}
	}

	private void fillTableSelect(Project project, VirtualFile vf) {
		DBTypeEnum dbTypeEnum = DataSourceUtils.getDbType(project, vf);
		if (dbTypeEnum == null) {
			return;
		}
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		instance.setDbType(dbTypeEnum.name());
		List<SqlTable> sqlTables = dbTypeEnum.getHandler().getSqlTables(project, vf);
		if (sqlTables == null) {
			NotificationUtil.showWarningNotification(project, "查询表失败", "查询表失败结果为空");
			return;
		}
		List<String> tables = sqlTables
				.stream()
				.map(SqlTable::getTableNameComment)
				.distinct()
				.toList();
		for (String table : tables) {
			tableNameTextField.addItem(table);
		}
		tableNameTextField.getComboKeyHandler().setList(tables);
		String selectTable = instance.getSelectTable();
		if (selectTable != null) {
			tableNameTextField.setSelectedItem(selectTable);
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
