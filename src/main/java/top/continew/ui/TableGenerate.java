package top.continew.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import top.continew.constant.GenerateConstant;
import top.continew.entity.SqlColumn;
import top.continew.utils.DataSourceUtils;

/**
 * @author lww
 * @date 2025-06-30 18:17
 */
public class TableGenerate extends DialogWrapper {

    private JPanel rootPanel;
    private JTable columnTable;
    private static final String[] columnList;

    static {
        columnList = new String[]{
                "序号", "列名称", "字段名称", "列类型", "Java类型", "描述", "列表", "表单", "必填", "查询", "表单类型", "查询方式", "关联字典"
        };
    }

    protected TableGenerate(Project project, VirtualFile vf, Object selectedItem) {
        super(project);
        setTitle("ContiNew Generator");
        setModal(true);
        setResizable(false);
        this.setSize(1080, 720);
        this.init();
        columnTable.setDoubleBuffered(true);
        String tableName = "";
        Object[][] data = {};
        if (selectedItem != null) {
            if (selectedItem.toString().indexOf(" - ") > 0) {
                tableName = selectedItem.toString().split(" - ")[0];
            } else {
                tableName = selectedItem.toString();
            }
            List<SqlColumn> columns = DataSourceUtils.getSqlTablesColumns(project, vf, tableName);
            data = new Object[columns.size()][];
            for (SqlColumn column : columns) {
                Object[] row = new Object[columnList.length];
                //序号
                row[0] = String.valueOf(column.getOrdinalPosition());
                //名称
                row[1] = column.getColumnName();
                //字段名称
                row[2] = column.getJavaField();
                //类型
                row[3] = column.getDataType();
                //Java类型
                row[4] = column.getJavaType();
                //描述
                row[5] = column.getColumnComment();
                //列表
                row[6] = Boolean.TRUE; // 设置为CheckBox
                //表单
                if (Arrays.asList(GenerateConstant.formExcludeFields.split(",")).contains(column.getJavaField())) {
                    row[7] = Boolean.FALSE;
                } else {
                    row[7] = Boolean.TRUE;
                }
                //必填
                if (Arrays.asList(GenerateConstant.requiredExcludeFields.split(",")).contains(column.getJavaField())) {
                    row[8] = Boolean.FALSE;
                } else {
                    row[8] = Boolean.TRUE;
                }
                //查询
                if (Arrays.asList(GenerateConstant.queryExcludeFields.split(",")).contains(column.getJavaField())) {
                    row[9] = Boolean.FALSE;
                } else {
                    row[9] = Boolean.TRUE;
                }
                //表单类型
                row[10] = "文本框"; // TODO
                //查询方式
                row[11] = "单选框"; // TODO
                //关联字典
                row[12] = ""; // TODO
                data[columns.indexOf(column)] = row;
            }
        }
        // 创建表格模型
        DefaultTableModel model = new DefaultTableModel(data, columnList) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6 || columnIndex == 7 || columnIndex == 8 || columnIndex == 9) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        columnTable.setModel(model);
    }

    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
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
