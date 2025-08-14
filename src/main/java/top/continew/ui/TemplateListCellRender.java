package top.continew.ui;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import top.continew.version.TemplateEnum;

/**
 * @author Administrator
 * @date 2025-08-12 23:30
 */
public class TemplateListCellRender extends JLabel implements ListCellRenderer<TemplateEnum> {

	@Override
	public Component getListCellRendererComponent(JList<? extends TemplateEnum> list, TemplateEnum value, int index, boolean isSelected, boolean cellHasFocus) {
		// 设置显示文本
		setText(value.getFileName().formatted(TableGenerate.static_className));
		// 设置背景颜色
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		// 设置边框
		setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		return this;
	}
}
