package top.continew.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import java.awt.Dimension;
import java.util.ArrayList;
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
        String[][] data = {};
        if (selectedItem != null) {
            if (selectedItem.toString().indexOf(" - ") > 0) {
                tableName = selectedItem.toString().split(" - ")[0];
            } else {
                tableName = selectedItem.toString();
            }
            List<SqlColumn> columns = DataSourceUtils.getSqlTablesColumns(project, vf, tableName);
            data = new String[columns.size()][];
            for (SqlColumn column : columns) {
                String[] row = new String[columnList.length];
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
                row[6] = "是";
                //表单
                row[7] = "是";
                //必填
                row[8] = column.isNULLAble() ? "是" : "否";
                //查询
                row[9] = "是";
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
        DefaultTableModel model = new DefaultTableModel(data, columnList);
        columnTable.setModel(model);
        System.out.println("selectedItem = " + selectedItem);
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
