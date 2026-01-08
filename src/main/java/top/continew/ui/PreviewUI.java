package top.continew.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import top.continew.config.ContiNewConfigPersistent;
import top.continew.factoty.DataEditorFactory;
import top.continew.persistent.ContiNewGeneratorPersistent;
import top.continew.version.TemplateEnum;

public class PreviewUI extends DialogWrapper {

	private JPanel rootPanel;
	private JList<TemplateEnum> fileList;
	private JButton cancelButton;
	private JButton generateButton;
	private JPanel textPanel;
	private JPanel filePanel;
	private Project project;
	private Editor currentEditor; // 添加当前Editor的引用

	public PreviewUI(Project project, Map<String, Object> dataModel, List<TemplateEnum> files) {
		super(project);
		setTitle("预览");
		setModal(false);
		setResizable(false);
		ContiNewConfigPersistent configPersistent = ContiNewConfigPersistent.getInstance();
		boolean highLight = configPersistent.getHighLight() != null && configPersistent.getHighLight();
		DefaultListModel<TemplateEnum> listModel = new DefaultListModel<>();
		files.forEach(listModel::addElement);
		fileList.setModel(listModel);
		fileList.setCellRenderer(new TemplateListCellRender());
		fileList.setSelectedIndex(0);
		TemplateEnum templateEnum = files.get(0);
		String previewCodeString = TableGenerate.previewCodeString(dataModel, templateEnum);
		DataEditorFactory dataEditorFactory = new DataEditorFactory(project);
		Editor editor;
		if (highLight) {
			editor = dataEditorFactory.createEditor(templateEnum.getFileName().formatted(dataModel.get("className") + ""), previewCodeString);
		} else {
			editor = dataEditorFactory.createEditor("temp.txt", previewCodeString);
		}
		currentEditor = editor; // 保存Editor引用
		textPanel.add(editor.getComponent(), BorderLayout.CENTER);
		this.setSize(1200, 800);
		filePanel.setPreferredSize(new Dimension(200, 730));
		filePanel.setMinimumSize(new Dimension(200, 730));
		filePanel.setMaximumSize(new Dimension(200, 730));
		textPanel.setPreferredSize(new Dimension(965, 750));
		textPanel.setMinimumSize(new Dimension(965, 750));
		textPanel.setMaximumSize(new Dimension(965, 750));
		this.init();
		this.project = project;
		cancelButton.addActionListener(e -> {
			this.dispose();
			ContiNewGeneratorPersistent instance = ContiNewGeneratorPersistent.getInstance(project);
			TableGenerate tableGenerate = new TableGenerate(project,
					LocalFileSystem.getInstance().findFileByIoFile(new File(instance.getConfigPath())),
					instance.getSelectTable(),
					instance.getModuleName());
		tableGenerate.show();
		});
		fileList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				ContiNewConfigPersistent configPersistent1 = ContiNewConfigPersistent.getInstance();
				boolean hightLight1 = configPersistent1.getHighLight() != null && configPersistent1.getHighLight();
				TemplateEnum selectedValue = fileList.getSelectedValue();
				String previewCodeString1 = TableGenerate.previewCodeString(dataModel, selectedValue);
				textPanel.removeAll();
				Editor newEditor;
				if (hightLight1) {
					newEditor = dataEditorFactory.createEditor(selectedValue.getFileName().formatted(dataModel.get("className") + ""), previewCodeString1);
				} else {
					newEditor = dataEditorFactory.createEditor(selectedValue.getFileName().formatted(dataModel.get("ClassName") + "") + ".txt", previewCodeString1);
				}
				currentEditor = newEditor; // 更新当前Editor引用
				textPanel.add(newEditor.getComponent(), BorderLayout.CENTER);
				textPanel.repaint();
			}
		});
		generateButton.addActionListener(e -> {
			fileList.getSelectedValuesList().forEach(selected -> {
				// 使用保存的Editor引用来获取文本内容
				if (currentEditor != null) {
					String className = dataModel.get("className") + "";
					String moduleName = dataModel.get("apiModuleName") + "";
					//String text = currentEditor.getDocument().getText();
					String text = TableGenerate.previewCodeString(dataModel, selected);
					TableGenerate.generateCode(project, text, selected, className, moduleName);
				}
			});
		});
	}

	@Override
	protected JComponent createCenterPanel() {
		return rootPanel;
	}

	public void initFileList(Project project) {

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