package top.continew.ui;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import top.continew.factoty.DataEditorFactory;
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
		DefaultListModel<TemplateEnum> listModel = new DefaultListModel<>();
		files.forEach(listModel::addElement);
		fileList.setModel(listModel);
		fileList.setCellRenderer(new TemplateListCellRender());
		fileList.setSelectedIndex(0);
		TemplateEnum templateEnum = files.get(0);
		String previewCodeString = TableGenerate.previewCodeString(dataModel, templateEnum);
		DataEditorFactory dataEditorFactory = new DataEditorFactory(project);
		Editor editor = dataEditorFactory.createEditor(templateEnum.getFileName().formatted(dataModel.get("ClassName") + "")
				, getFileType(templateEnum.getFileName()), previewCodeString);
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
		cancelButton.addActionListener(e -> dispose());
		fileList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				TemplateEnum selectedValue = fileList.getSelectedValue();
				String previewCodeString1 = TableGenerate.previewCodeString(dataModel, selectedValue);
				textPanel.removeAll();
				Editor newEditor = dataEditorFactory.createEditor(templateEnum.getFileName().formatted(dataModel.get("ClassName") + "")
						, getFileType(selectedValue.getFileName()), previewCodeString1);
				currentEditor = newEditor; // 更新当前Editor引用
				textPanel.add(newEditor.getComponent(), BorderLayout.CENTER);
				textPanel.repaint();
			}
		});
		generateButton.addActionListener(e -> {
			TemplateEnum templateEnum1 = fileList.getSelectedValue();
			// 使用保存的Editor引用来获取文本内容
			if (currentEditor != null) {
				String className = dataModel.get("ClassName") + "";
				String moduleName = dataModel.get("moduleName") + "";
				String text = currentEditor.getDocument().getText();
				TableGenerate.generateCode(project, text, templateEnum1, className, moduleName);
			}
		});
	}

	public static FileType getFileType(String fileName) {
		if (fileName.endsWith(".java")) {
			//return JavaFileType.INSTANCE;
			return PlainTextFileType.INSTANCE;
		} else if (fileName.endsWith(".vue") || fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return HtmlFileType.INSTANCE;
		} else if (fileName.endsWith(".xml")) {
			return XmlFileType.INSTANCE;
		} else {
			return PlainTextFileType.INSTANCE;
		}
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