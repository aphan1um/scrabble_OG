package new_client;

import client.Modification;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ScrabbleTable extends JTable {
    private JScrollPane scroll_pane;
    private Modification modification;

    public ScrabbleTable(int rows, int cols) {
        super(rows, cols);

        scroll_pane = new JScrollPane(this);
        setTableHeader(null);

        // when resizing window, resize fonts
        getParent().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                setRowHeight(getParent().getHeight() / getRowCount());

                Font curr_font = getFont();
                double dist = Math.sqrt(
                        Math.pow(getColumnModel().getColumn(0).getWidth(), 2) +
                                Math.pow(getRowHeight(), 2));

                // TODO: MAGIC NUMBER
                setFont(new Font(curr_font.getFontName(), Font.PLAIN, (int)(dist/2.4)));
            }
        });

        // center letters on cell
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        setDefaultRenderer(Object.class, centerRenderer);

        // better visuals
        MatteBorder matteBorder = new MatteBorder(1, 1, 0, 0, Color.black);
        setBorder(matteBorder);
        setGridColor(Color.black);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer r, int row, int col) {
        Component comp = super.prepareRenderer(r, row, col);

        if (modification != null &&
                row == modification.getX() &&
                col == modification.getY())
            comp.setBackground(Color.green);
        else if (is_part_of_word(row, col))
            comp.setBackground(Color.yellow);
        else if (modification != null)
            comp.setBackground(Color.lightGray);
        else
            comp.setBackground(getBackground());

        comp.setForeground(Color.black);

        return comp;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        /** TODO: Add in final product; we're debugging for now.
         if (modification != null && (modification.getX() != row ||
         modification.getY() != col)) {
         return false;
         }
         */

        return true;
    }

    private boolean is_part_of_word(int row, int col) {
        if (modification == null || isCellEmpty(row, col) ||
                (modification.getX() != row && modification.getY() != col))
            return false;

        if (modification.getX() == row) {
            int direction = (int)Math.signum(col - modification.getY());

            for (int i = modification.getY(); direction * (col - i) >= 0; i += direction) {
                if (getValueAt(row, i) == null)
                    return false;
            }
        }
        else { // vertical word
            int direction = (int)Math.signum(row - modification.getX());

            for (int j = modification.getX(); direction * (row - j) >= 0; j += direction) {
                if (getValueAt(j, col) == null)
                    return false;
            }
        }

        return true;
    }

    private boolean isCellEmpty(int row, int col) {
        Object val = getValueAt(row, col);

        return val == null || val.equals("");
    }
}
