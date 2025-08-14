package top.continew.factoty;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.SpellCheckingEditorCustomizationProvider;
import com.intellij.openapi.editor.actions.AbstractToggleUseSoftWrapsAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.EditorGutterComponentEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.ErrorStripeEditorCustomization;
import java.util.Objects;

/**
 * @author lww
 * @date 2025-01-22 18:16
 */
public class DataEditorFactory {

	private Project project;

	public DataEditorFactory(Project project) {
		this.project = project;
	}

	public Editor createEditor(String fileName, FileType fileType, String text) {
		VirtualFile sourceVirtualFile = new LightVirtualFile(fileName, fileType, text);
		PsiFile sourceFile = PsiManager.getInstance(project).findFile(sourceVirtualFile);
		assert sourceFile != null;
		Document doc = PsiDocumentManager.getInstance(project).getDocument(sourceFile);

		assert doc != null;
		Editor editor = EditorFactory.getInstance().createEditor(doc, project, sourceVirtualFile, false, EditorKind.MAIN_EDITOR);

		EditorSettings editorSettings = editor.getSettings();
		// 行号显示
		editorSettings.setLineNumbersShown(true);
		// 设置显示的缩进导轨
		editorSettings.setIndentGuidesShown(true);
		// 折叠块显示
		editorSettings.setFoldingOutlineShown(true);
		// 折叠块、行号所展示的区域
		editorSettings.setLineMarkerAreaShown(false);
		// 显示设置插入符行（光标选中行会变黄）
		editorSettings.setCaretRowShown(true);
		//禁用编辑器中的错误条纹
		ErrorStripeEditorCustomization.DISABLED.customize((EditorEx) editor);
		//禁用编辑器中的拼写检查功能
		Objects.requireNonNull(SpellCheckingEditorCustomizationProvider.getInstance().getDisabledCustomization()).customize((EditorEx) editor);

		EditorGutterComponentEx gutterComponentEx = ((EditorEx) editor).getGutterComponentEx();
		// 设置绘画背景
		gutterComponentEx.setPaintBackground(false);
		//移除编辑器的边框
		//editor.setBorder(JBUI.Borders.empty());
		//json5 的编辑器 换行
		editorSettings.setUseSoftWraps(true);
		//editorSettings.setVirtualSpace(false);
		//editorSettings.setAdditionalColumnsCount(3);
		//editorSettings.setAdditionalLinesCount(3);
		// 指定配色方案
		//toggleColorSchema((EditorEx) editor, editor.getColorsScheme());
		//setPopupMenu(editor.getComponent());
		// 切换软换行状态
		AbstractToggleUseSoftWrapsAction.toggleSoftWraps(editor, null, true);
		return editor;
	}

}
