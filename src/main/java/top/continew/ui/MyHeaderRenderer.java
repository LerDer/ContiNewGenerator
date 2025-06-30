package top.continew.ui;

import com.intellij.ui.JBColor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import top.continew.utils.PluginIconsUtils;

/**
 * @author lww
 * @date 2025-04-25 18:47
 */
public class MyHeaderRenderer extends DefaultTableCellRenderer {

	private final Map<Integer, Icon> icons = new HashMap<>();
	private final JLabel label = new JLabel();

	public MyHeaderRenderer() {
		// 居中对齐
		label.setHorizontalAlignment(SwingConstants.CENTER);
		// 设置为不透明
		label.setOpaque(true);
		// 背景色
		label.setBackground(JBColor.getHSBColor(0.797f, 0.135f, 0.929f));
		// 前景色
		label.setForeground(JBColor.BLACK);
		// 设置图标与文本之间的间距
		label.setIconTextGap(5);
		// 设置字体大小
		label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// 设置文本
		String text = value == null ? "" : value.toString();
		label.setText(text);
		// 设置图标
		Icon icon = icons.get(column);
		if (icon == null) {
			icon = PluginIconsUtils.propertiesFile;
		}
		//label.setIcon(icon);

		// 设置右侧分隔线（除了最后一列）
		int columnCount = table.getColumnCount();
		if (column < columnCount - 1) {
			label.setBorder(new MatteBorder(0, 0, 0, 2, new Color(200, 200, 200)));
		} else {
			label.setBorder(new MatteBorder(0, 0, 0, 0, new Color(200, 200, 200)));
		}

		return label;
	}

	// 公共方法：为指定列设置图标
	public void setIconForColumn(int columnIndex, Icon icon) {
		if (icon != null) {
			icons.put(columnIndex, icon);
		} else {
			icons.remove(columnIndex);
		}
	}
}
