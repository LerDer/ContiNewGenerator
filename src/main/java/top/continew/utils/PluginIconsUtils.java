package top.continew.utils;

import com.intellij.openapi.util.IconLoader;
import javax.swing.Icon;

public class PluginIconsUtils {

	public static final Icon logo = load("/icons/logo.svg");

	public static final Icon projectStructure = load("/icons/projectStructure.svg");
	public static final Icon projectStructure_dark = load("/icons/projectStructure_dark.svg");

	public static final Icon success = load("/icons/success.svg");
	public static final Icon success_dark = load("/icons/success_dark.svg");

	public static final Icon testFailed = load("/icons/testFailed.svg");
	public static final Icon testFailed_dark = load("/icons/testFailed_dark.svg");

	public static final Icon promptInput = load("/icons/promptInput.svg");
	public static final Icon promptInput_dark = load("/icons/promptInput_dark.svg");

	public static Icon load(String iconPath) {
		return IconLoader.getIcon(iconPath, PluginIconsUtils.class);
	}

}
