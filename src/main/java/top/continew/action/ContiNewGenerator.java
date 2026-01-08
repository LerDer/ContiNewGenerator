package top.continew.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import top.continew.ui.MainGenerator;

public class ContiNewGenerator extends AnAction {

	private static MainGenerator instance = null;

	@Override
	public void actionPerformed(AnActionEvent e) {
		Project project = e.getProject();
		if (instance == null) {
			instance = new MainGenerator(project);
		}
		instance.show();
	}
}
