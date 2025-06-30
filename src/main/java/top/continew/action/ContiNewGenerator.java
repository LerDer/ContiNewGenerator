package top.continew.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import top.continew.ui.MainGenerator;

public class ContiNewGenerator extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		MainGenerator instance = MainGenerator.getInstance(e.getProject());
		instance.show();
	}
}
