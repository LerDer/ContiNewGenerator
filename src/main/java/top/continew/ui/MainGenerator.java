package top.continew.ui;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;
import top.continew.entity.SqlTable;
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
	private JCheckBox mysqlCheckBox;
	private JCheckBox postgresCheckBox;
	private JTextField businessNameTextField;
	private JLabel businessNameLabel;
	private AutoCompleteComboBox moduleComboBox;
	private JTextField vuePathTextField;
	private JButton vueSelectPathButton;
	private JButton selectPackageButton;
	private JLabel vuePathLabel;
	private AutoCompleteComboBox versionComboBox;

	public MainGenerator(Project project) {
		super(project);
		setTitle("ContiNew Generator");
		setModal(false);
		setResizable(false);
		this.init();
		reShow(project);
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
		tableNameTextField.addActionListener(e -> setBusinessNameAndPrefix());
		selectPackageButton.setIcon(PluginIcons.package1);
		selectPackageButton.addActionListener(e -> choosePackage(project));
		initVersion();
	}

	private void initVersion() {
		List<String> versions = new ArrayList<>();
		versions.add("3.5.0");
		versions.add("3.6.0");
		versions.add("3.7.0-");
		versions.add("4.0.0-多租户");
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

	private void setBusinessNameAndPrefix() {
		if (tableNameTextField.getSelectedItem() != null) {
			String tableNameSelect = tableNameTextField.getSelectedItem().toString();
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
		if (StringUtils.isNotEmpty(projectPath)) {
			this.projectPathTextField.setText(projectPath);
			this.projectPathTextField.setToolTipText(projectPath);
		}
		String vuePath = instance.getVuePath();
		if (StringUtils.isNotBlank(vuePath)) {
			vuePathTextField.setText(vuePath);
			vuePathTextField.setToolTipText(vuePath);
			fillModules(project, LocalFileSystem.getInstance().findFileByIoFile(new File(vuePath)));
		}
		String configPath = instance.getConfigPath();
		if (StringUtils.isNotEmpty(configPath)) {
			this.configFilePathTextField.setText(configPath);
			configFilePathTextField.setToolTipText(configPath);
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
		this.overrideCheckBox.setSelected(instance.isOverride());
		this.mysqlCheckBox.setSelected(instance.isMysql());
		this.postgresCheckBox.setSelected(instance.isPg());
	}

	private void nextStep(Project project) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		instance.setProjectPath(projectPathTextField.getText());
		instance.setVuePath(vuePathTextField.getText());
		instance.setConfigPath(configFilePathTextField.getText());
		instance.setAuthor(authorTextField.getText());
		instance.setPackageName(packageNameTextField.getText());
		instance.setBusinessName(businessNameTextField.getText());
		instance.setOverride(overrideCheckBox.isSelected());
		instance.setMysql(mysqlCheckBox.isSelected());
		instance.setPg(postgresCheckBox.isSelected());
		instance.setTablePrefix(tablePrefixTextField.getText());
		TableGenerate tableGenerate = new TableGenerate(project, LocalFileSystem.getInstance().findFileByIoFile(new File(this.configFilePathTextField.getText())),
				this.tableNameTextField.getSelectedItem(), moduleComboBox.getSelectedItem());
		tableGenerate.show();
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
		}
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
		List<SqlTable> sqlTables = DataSourceUtils.getSqlTables(project, vf);
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
