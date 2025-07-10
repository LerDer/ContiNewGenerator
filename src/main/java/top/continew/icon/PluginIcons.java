package top.continew.icon;

import com.intellij.openapi.util.IconLoader;
import javax.swing.Icon;

public class PluginIcons {

	public static final Icon logo = load("/icons/logo.svg");

	public static final Icon success = load("/icons/success.svg");
	public static final Icon success_dark = load("/icons/success_dark.svg");

	public static final Icon testFailed = load("/icons/testFailed.svg");
	public static final Icon testFailed_dark = load("/icons/testFailed_dark.svg");

	public static final Icon sendToTheRight = load("/icons/sendToTheRight.svg");
	public static final Icon sendToTheRight_dark = load("/icons/sendToTheRight_dark.svg");

	public static final Icon sendToTheLeft = load("/icons/sendToTheLeft.svg");
	public static final Icon sendToTheLeft_dark = load("/icons/sendToTheLeft_dark.svg");

	public static final Icon yaml = load("/icons/yaml.svg");
	public static final Icon yaml_dark = load("/icons/yaml_dark.svg");

	public static final Icon projectDirectory = load("/icons/projectDirectory.svg");
	public static final Icon projectDirectory_dark = load("/icons/projectDirectory_dark.svg");
	
	public static final Icon springBoot = load("/icons/springBoot.svg");
	public static final Icon springBoot_dark = load("/icons/springBoot_dark.svg");
	
	public static final Icon vue = load("/icons/vue.svg");
	public static final Icon vue_dark = load("/icons/vue_dark.svg");

	public static Icon load(String iconPath) {
		return IconLoader.getIcon(iconPath, PluginIcons.class);
	}

}
