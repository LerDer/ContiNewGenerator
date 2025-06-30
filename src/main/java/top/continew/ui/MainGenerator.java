package top.continew.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jetbrains.annotations.Nullable;
import top.continew.utils.FileChooseUtils;
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
	private JTextField versionTextField;
	private JLabel versionLabel;
	private JTextField createTimeTextField;
	private JTextField updateTimeTextField;
	private JLabel createTimeLabel;
	private JLabel updateTimeLabel;
	private JTextField logicDeleteTextField;
	private JLabel logicDeleteLabel;
	private AutoCompleteComboBox tableNameTextField;
	private JLabel tableLabel;
	private JButton cancelButton;
	private JButton nextButton;

	private static final MainGenerator instance = null;

	public static MainGenerator getInstance(Project project) {
		if (instance == null) {
			return new MainGenerator(project);
		}
		return instance;
	}

	protected MainGenerator(@Nullable Project project) {
		super(project);
		setTitle("ContiNew Generator");
		setModal(true);
		setResizable(false);
		this.init();

		selectPathButton.setIcon(PluginIconsUtils.projectStructure);
		selectPathButton.addActionListener(e -> {
			FileChooseUtils uiComponentFacade = FileChooseUtils.getInstance(project);
			VirtualFile baseDir = null;
			if (project != null) {
				baseDir = ProjectUtil.guessProjectDir(project);
			}
			final VirtualFile vf = uiComponentFacade.showSingleFolderSelectionDialog("选择项目路径", baseDir, baseDir);
			if (null != vf) {
				this.projectPathTextField.setText(vf.getPath());
			}
		});

		nextButton.setIcon(PluginIconsUtils.promptInput);
		cancelButton.setIcon(PluginIconsUtils.testFailed);
		cancelButton.addActionListener(e -> dispose());

	}

	@Override
	protected @Nullable JComponent createCenterPanel() {
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
