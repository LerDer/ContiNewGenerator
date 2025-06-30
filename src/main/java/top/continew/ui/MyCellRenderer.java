package top.continew.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author lww
 * @date 2025-04-25 19:21
 */
public class MyCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        // 调用父类方法设置默认样式
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // 设置单元格不透明，确保背景色生效
        setOpaque(true);

        // 设置隔行变色
        if (!isSelected) {
            if (row % 2 == 0) {
                setBackground(new Color(179, 238, 203));
            } else {
                setBackground(new Color(207, 232, 239));
            }
        } else {
            // 选中时恢复默认背景色
            setBackground(table.getSelectionBackground());
        }

        // 设置 Tooltip：当内容被截断时显示完整内容
        String text = value == null ? "" : value.toString();
        setText(text);
        setToolTipText(isTextTruncated(table, row, column, text) ? text : null);

        return this;
    }

    // 修正Tooltip判断，考虑Insets
    private boolean isTextTruncated(JTable table, int row, int column, String text) {
        if (text == null || table == null) {
            return false;
        }
        int columnWidth = table.getColumnModel().getColumn(column).getWidth();
        // 获取单元格的Insets
        java.awt.Insets insets = getInsets();
        int cellPadding = insets.left + insets.right;
        int availableWidth = columnWidth - cellPadding;
        int textWidth = getFontMetrics(getFont()).stringWidth(text);
        return textWidth > availableWidth;
    }

}
