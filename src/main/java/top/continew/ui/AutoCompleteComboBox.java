package top.continew.ui;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import lombok.Getter;
import lombok.Setter;

public class AutoCompleteComboBox extends JComboBox<String> {

    private List<String> allItems;
    @Getter
    private ComboKeyHandler comboKeyHandler;
    
    public AutoCompleteComboBox() {
        this(Collections.emptyList());
    }

    public AutoCompleteComboBox(List<String> items) {
        this.allItems = items;
        setEditable(true);
        setModel(new DefaultComboBoxModel<>(items.toArray(new String[0])));
        setSelectedIndex(-1);

        // Set custom renderer for dropdown items
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null && value.toString().length() > 20) {
                    setToolTipText(value.toString());
                } else {
                    setToolTipText(null);
                }
                return c;
            }
        });

        JTextField field = (JTextField) getEditor().getEditorComponent();
        field.setText("");
        comboKeyHandler = new ComboKeyHandler(this);
        field.addKeyListener(comboKeyHandler);
        field.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                showPopup();
            }
        });
    }

}

class ComboKeyHandler extends KeyAdapter {

    private final JComboBox<String> comboBox;
    @Setter
    private List<String> list = new ArrayList<>();
    private boolean shouldHide;

    protected ComboKeyHandler(JComboBox<String> combo) {
        super();
        this.comboBox = combo;
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            list.add(comboBox.getItemAt(i));
        }
    }

    public void reset() {
        ComboBoxModel<String> m = new DefaultComboBoxModel<>(list.toArray(new String[0]));
        setSuggestionModel(comboBox, m, "");
        comboBox.hidePopup();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        String text = ((JTextField) e.getComponent()).getText();
        ComboBoxModel<String> m;
        if (text.isEmpty()) {
            m = new DefaultComboBoxModel<>(list.toArray(new String[0]));
            setSuggestionModel(comboBox, m, "");
            comboBox.hidePopup();
        } else {
            m = getSuggestedModel(list, text);
            if (m.getSize() == 0 || shouldHide) {
                comboBox.hidePopup();
            } else {
                setSuggestionModel(comboBox, m, text);
                comboBox.showPopup();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        JTextField textField = (JTextField) e.getComponent();
        String text = textField.getText();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                if (text.isEmpty()) {
                    ComboBoxModel<String> m = new DefaultComboBoxModel<>(list.toArray(new String[0]));
                    setSuggestionModel(comboBox, m, "");
                    comboBox.hidePopup();
                }
                shouldHide = false;
                break;
            case KeyEvent.VK_RIGHT:
                if (textField.hasFocus()) {
                    for (String s : list) {
                        if (s.startsWith(text)) {
                            textField.setText(s);
                            shouldHide = false;
                            return;
                        }
                    }
                }
                shouldHide = false;
                break;
            case KeyEvent.VK_ENTER:
                if (!list.contains(text)) {
                    list.add(text);
                    list.sort(Comparator.naturalOrder());
                    setSuggestionModel(comboBox, getSuggestedModel(list, text), text);
                }
                shouldHide = true;
                break;
            case KeyEvent.VK_ESCAPE:
                shouldHide = true;
                break;
            default:
                shouldHide = false;
                break;
        }
    }

    private static <E> void setSuggestionModel(JComboBox<E> combo, ComboBoxModel<E> mdl, E value) {
        combo.setModel(mdl);
        combo.setSelectedIndex(-1);
        ((JTextField) combo.getEditor().getEditorComponent()).setText(Objects.toString(value));
    }

    private static ComboBoxModel<String> getSuggestedModel(List<String> list, String text) {
        DefaultComboBoxModel<String> m = new DefaultComboBoxModel<>();
        String lowerText = text.toLowerCase();
        for (String s : list) {
            if (s.toLowerCase().contains(lowerText)) {
                m.addElement(s);
            }
        }
        return m;
    }
}
