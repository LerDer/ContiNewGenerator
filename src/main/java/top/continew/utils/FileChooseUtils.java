package top.continew.utils;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author lww
 */
public class FileChooseUtils {

	private final Project project;
	private final FileEditorManager fileEditorManager;

	private FileChooseUtils(final Project project) {
		this.project = project;
		this.fileEditorManager = FileEditorManager.getInstance(project);
	}

	public static FileChooseUtils getInstance(Project project) {
		if (project == null) {
			return null;
		}
		return new FileChooseUtils(project);
	}

	public VirtualFile showSingleFolderSelectionDialog(String title, VirtualFile toSelect, VirtualFile... roots) {
		if (title == null) {
			return null;
		}
		final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
		descriptor.setTitle(title);
		if (null != roots) {
			descriptor.setRoots(roots);
		}
		return FileChooser.chooseFile(descriptor, this.project, toSelect);
	}

}
