package io.chengguo.apidebugger.ui.custom;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.refactoring.ui.StringTableCellEditor;
import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.TableUtil;
import com.intellij.ui.table.TableView;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;
import io.chengguo.apidebugger.ui.action.CloseTabAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FingerArt on 2017/07/27.
 */
public class JBDebuggerTable extends TableView<JBDebuggerTable.ItemInfo> {

    public JBDebuggerTable() {
        setModelAndUpdateColumns(new ListTableModel<>(createColumnInfos(this), ContainerUtil.newSmartList(new ItemInfo())));
        setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
        getColumnModel().getColumn(0).setResizable(false);
        TableUtil.setupCheckboxColumn(getColumnModel().getColumn(0));
        getTableHeader().setReorderingAllowed(false);//禁止拖动表头
        setRowSelectionAllowed(false);
        setRowMargin(1);
        setRowHeight(20);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouse(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouse(e);
            }

            private void handleMouse(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu menu = createPopup();
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private JPopupMenu createPopup() {
        JPopupMenu popupMenu = new JPopupMenu();

//        TerminalAction.addToMenu(popupMenu, this);

        JMenuItem deleteLine = new JMenuItem("Delete", AllIcons.Actions.Delete);

        deleteLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("actionEvent = [" + actionEvent + "]");
                if (getRowCount() > 1) {
                    getListTableModel().removeRow(getSelectedRow());
                }
            }
        });

        popupMenu.add(deleteLine);
        return popupMenu;
    }

    @Override
    public void paint(@NotNull Graphics g) {
        super.paint(g);
        UIUtil.fixOSXEditorBackground(this);
    }

    public Map<String, String> getKeyValue() {
        List<ItemInfo> items = getListTableModel().getItems();
        Map<String, String> result = new LinkedHashMap<>();
        items.stream().filter(item -> item.enabled && item.checked).forEach(item -> result.put(item.key, item.value));
        return result;
    }

    private static ColumnInfo[] createColumnInfos(JBDebuggerTable table) {
        ColumnInfo[] columnInfos = {
                new ColumnInfo<ItemInfo, Boolean>(" ") {
                    @Override
                    public Class getColumnClass() {
                        return Boolean.class;
                    }

                    @Override
                    public Boolean valueOf(ItemInfo o) {
                        return o.checked;
                    }

                    @Override
                    public boolean isCellEditable(ItemInfo itemInfo) {
                        return itemInfo.enabled;
                    }

                    @Override
                    public void setValue(ItemInfo itemInfo, final Boolean value) {
                        itemInfo.checked = value;
                    }

                    @Nullable
                    @Override
                    public TableCellEditor getEditor(ItemInfo itemInfo) {
                        return new BooleanTableCellEditor();
                    }
                },
                new ColumnInfo<ItemInfo, String>("Name") {
                    @Nullable
                    @Override
                    public String valueOf(ItemInfo itemInfo) {
                        return itemInfo.key;
                    }

                    @Override
                    public boolean isCellEditable(ItemInfo itemInfo) {
                        return true;
                    }

                    @Override
                    public void setValue(ItemInfo itemInfo, String value) {
                        itemInfo.key = value;
                    }

                    @Nullable
                    @Override
                    public TableCellEditor getEditor(ItemInfo itemInfo) {
                        StringTableCellEditor editor = new StringTableCellEditor(null);
                        editor.addDocumentListener(new DocumentAdapter() {
                            @Override
                            public void documentChanged(DocumentEvent documentEvent) {
                                if (table.getRowCount() == table.getEditingRow() + 1) {
                                    itemInfo.enabled = true;
                                    itemInfo.checked = true;
                                    table.getListTableModel().addRow(new ItemInfo());
                                }
                            }
                        });
                        return editor;
                    }
                },
                new ColumnInfo<ItemInfo, String>("Value") {
                    @Nullable
                    @Override
                    public String valueOf(ItemInfo itemInfo) {
                        return itemInfo.value;
                    }

                    @Override
                    public boolean isCellEditable(ItemInfo itemInfo) {
                        return true;
                    }

                    @Override
                    public void setValue(ItemInfo itemInfo, String value) {
                        itemInfo.value = value;
                    }

                    @Nullable
                    @Override
                    public TableCellEditor getEditor(ItemInfo itemInfo) {
                        StringTableCellEditor editor = new StringTableCellEditor(null);
                        editor.addDocumentListener(new DocumentListener() {
                            @Override
                            public void beforeDocumentChange(DocumentEvent documentEvent) {
                            }

                            @Override
                            public void documentChanged(DocumentEvent documentEvent) {
                                if (table.getRowCount() == table.getEditingRow() + 1) {
                                    itemInfo.enabled = true;
                                    itemInfo.checked = true;
                                    table.getListTableModel().addRow(new ItemInfo());
                                }
                            }
                        });
                        return editor;
                    }
                }
        };
        return columnInfos;
    }

    public static class ItemInfo {
        public ItemInfo() {
            this(false, false, "", "");
        }

        public ItemInfo(boolean enabled, boolean checked, String key, String value) {
            this.enabled = enabled;
            this.checked = checked;
            this.key = key;
            this.value = value;
        }

        boolean enabled;
        boolean checked;
        String key;
        String value;
    }
}
