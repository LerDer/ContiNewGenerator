package top.continew.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.File;
import java.util.List;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;
import top.continew.entity.SqlTable;
import top.continew.persistent.ContiNewGeneratorPersistent;
import top.continew.utils.DataSourceUtils;
import top.continew.utils.FileChooseUtils;
import top.continew.utils.NotificationUtil;
import top.continew.utils.PluginIconsUtils;

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

	public MainGenerator(Project project) {
		super(project);
		setTitle("ContiNew Generator");
		setModal(true);
		setResizable(false);
		this.init();
		reShow(project);
		configFilePathButton.setIcon(PluginIconsUtils.yaml);
		configFilePathButton.addActionListener(e -> chooseConfigPath(project));
		selectPathButton.setIcon(PluginIconsUtils.projectDirectory);
		selectPathButton.addActionListener(e -> chooseProjectPath(project));
		nextButton.setIcon(PluginIconsUtils.sendToTheRight);
		cancelButton.setIcon(PluginIconsUtils.testFailed);
		cancelButton.addActionListener(e -> dispose());
		nextButton.addActionListener(e -> nextStep(project));
	}

	private void reShow(Project project) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		//回显数据
		String projectPath = instance.getProjectPath();
		if (StringUtils.isNotEmpty(projectPath)) {
			this.projectPathTextField.setText(projectPath);
		}
		String configPath = instance.getConfigPath();
		if (StringUtils.isNotEmpty(configPath)) {
			this.configFilePathTextField.setText(configPath);
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
		String tablePrefix = instance.getTablePrefix();
		if (StringUtils.isNotEmpty(tablePrefix)) {
			this.tablePrefixTextField.setText(tablePrefix);
		}
		String businessName = instance.getBusinessName();
		if (StringUtils.isNotEmpty(businessName)) {
			this.businessNameTextField.setText(businessName);
		}
		if (instance.isOverride()) {
			this.overrideCheckBox.setSelected(true);
		}
		if (instance.isMysql()) {
			this.mysqlCheckBox.setSelected(true);
		}
		if (instance.isPg()) {
			this.postgresCheckBox.setSelected(true);
		}
	}

	private void nextStep(Project project) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		instance.setProjectPath(projectPathTextField.getText());
		instance.setConfigPath(configFilePathTextField.getText());
		instance.setAuthor(authorTextField.getText());
		instance.setPackageName(packageNameTextField.getText());
		instance.setBusinessName(businessNameTextField.getText());
		instance.setOverride(overrideCheckBox.isSelected());
		instance.setMysql(mysqlCheckBox.isSelected());
		instance.setPg(postgresCheckBox.isSelected());
		instance.setTablePrefix(tablePrefixTextField.getText());
		TableGenerate tableGenerate = new TableGenerate(project, LocalFileSystem.getInstance().findFileByIoFile(new File(this.configFilePathTextField.getText())),
				this.tableNameTextField.getSelectedItem());
		tableGenerate.show();
	}

	private void chooseProjectPath(Project project) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		FileChooseUtils uiComponentFacade = FileChooseUtils.getInstance(project);
		VirtualFile baseDir = ProjectUtil.guessProjectDir(project);;
		final VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("选择项目路径", baseDir, baseDir);
		if (null != vf) {
			this.projectPathTextField.setText(vf.getPath());
			instance.setProjectPath(vf.getPath());
		}
	}

	private void chooseConfigPath(Project project) {
		ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
		FileChooseUtils uiComponentFacade = FileChooseUtils.getInstance(project);
		VirtualFile baseDir = null;
		baseDir = ProjectUtil.guessProjectDir(project);
		String text = configFilePathTextField.getText();
		if (StringUtils.isNotEmpty(text)) {
			File file = new File(text);
			File parentFile = file.getParentFile();
			if (parentFile != null && parentFile.exists()) {
				baseDir = LocalFileSystem.getInstance().findFileByIoFile(parentFile);
			}
		}
		final VirtualFile vf = uiComponentFacade.showSingleFileSelectionDialog("选择配置文件", baseDir, baseDir);
		if (null != vf) {
			String path = vf.getPath();
			this.configFilePathTextField.setText(path);
			instance.setConfigPath(path);
			fillTableSelect(project, vf);
		}
	}

	private void fillTableSelect(Project project, VirtualFile vf) {
		List<SqlTable> sqlTables = DataSourceUtils.getSqlTables(project, vf);
		if (sqlTables == null) {
			NotificationUtil.showWarningNotification("查询表失败", "查询表失败结果为空");
			return;
		}
		List<String> tables = sqlTables
				.stream()
				.map(SqlTable::getTableNameComment)
				.distinct()
				.toList();
		tableNameTextField.setModel(new DefaultComboBoxModel<>(tables.toArray(new String[0])));
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
