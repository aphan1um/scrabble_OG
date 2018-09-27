package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/*
there are four states in this game.
---------------------------------------------------
|  state  |  submit  |  pass  |  revoke  |  vote  |
|-------------------------------------------------|
|  myTurn |    F     |    T   |    F     |   F    |
|-------------------------------------------------|
|  edited |    T     |    F   |    T     |   F    |
|-------------------------------------------------|
|submitted|    F     |    F   |    F     |   T    |
|-------------------------------------------------|
|  vote   |    F     |    F   |    F     |   F    |
|-------------------------------------------------|
 */
public class ScrabbleBoard extends JFrame {
    private final int scrabble_board_width = 400;
    private final int scrabble_board_height = 500;

    private JTable table;

    private final int board_rows = 20;
    private final int board_colomns = 20;

    private final int button_count = 4;
    private final int button_layout_row = 1;

    private JFrame frame;
    private GridLayout board_grids;
    private GridLayout btn_grids;
    private GridLayout mark_grids;
    private JPanel board;
    private JPanel buttons;
    private JPanel mark;

    private ScrabbleTF[][] txt;

    private JLabel username_lb;
    private JLabel score_lb;

    private Modification modification;
    private int myScore = 0;

    private JPanel panel1;
    private JButton submitButton;
    private JButton passTurnButton;
    private JButton clearButton;
    private JButton voteButton;
    private JScrollPane scrollTable;

    public ScrabbleBoard() {
        frame = new JFrame();

        //setButtons();

        //frame.getContentPane().add(board);
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        //frame.setSize(scrabble_board_width, scrabble_board_height);
        frame.setVisible(true);

        // limit size of frame
        // TODO: magic number
        frame.setMinimumSize(new Dimension(
                (table.getColumnCount() + 3) * table.getColumnModel().getColumn(0).getMinWidth(),
                table.getRowHeight() * table.getRowCount()));

        //table.setBorder(BorderFactory.createLineBorder(Color.black));
//        board.add(table);
        //add(board, BorderLayout.CENTER);
//        setContentPane(board);
//        setVisible(true);

//        boardInitialization();
//        setButtons();
//        markInitialization();
//        this.setSize(scrabble_board_width, scrabble_board_height);
//        this.setResizable(true);
//        setVisible(true);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        validate();
    }

    private boolean is_part_of_word(int row, int col) {
        if (modification == null || isCellEmpty(row, col) ||
                (modification.getX() != row && modification.getY() != col))
            return false;

        if (modification.getX() == row) {
            int direction = (int)Math.signum(col - modification.getY());

            for (int i = modification.getY(); direction * (col - i) >= 0; i += direction) {
                if (table.getValueAt(row, i) == null)
                    return false;
            }
        }
        else { // vertical word
            int direction = (int)Math.signum(row - modification.getX());

            for (int j = modification.getX(); direction * (row - j) >= 0; j += direction) {
                if (table.getValueAt(j, col) == null)
                    return false;
            }
        }

        return true;
    }

    private void setTableCellListener() {
        Action action = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                TableCellListener tcl = (TableCellListener)e.getSource();

                if (!isCellEmpty(tcl.getRow(), tcl.getColumn())) {
                    modification = new Modification(
                            tcl.getRow(), tcl.getColumn(),
                            tcl.getNewValue().toString().toCharArray()[0]);

                    System.out.println("Row   : " + tcl.getRow());
                    System.out.println("Column: " + tcl.getColumn());
                    System.out.println("Old   : " + tcl.getOldValue());
                    System.out.println("New   : " + tcl.getNewValue());

                    table.repaint();
                }
            }
        };

        TableCellListener tcl = new TableCellListener(table, action);
    }

    private boolean isCellEmpty(int row, int col) {
        Object val = table.getValueAt(row, col);

        return val == null || val.equals("");
    }

    private void setTableCellEditor() {
        JTextField tf = new JTextField();
        tf.setDocument(new JTextFieldCharLimit());
        for(int i = 0; i < board_colomns; i ++) {
            table.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(tf));
        }
    }

    private void boardInitialization() {
        board_grids = new GridLayout(20, 20);
        board = new JPanel();
        board.setLayout(board_grids);

        txt = new ScrabbleTF[board_rows][board_colomns];
        for(int i = 0; i < txt.length; i++) {
            for(int j = 0; j < txt[i].length; j++) {
                ScrabbleTF tf = new ScrabbleTF(i, j);
                tf.setDocument(new JTextFieldCharLimit());
                //tf.getDocument().addDocumentListener(this);
                tf.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        modification = new Modification(tf.getStf_x(), tf.getStf_y(), tf.getText().toCharArray()[0]);
                        //highlightTouchedLetters(tf.getStf_x(), tf.getStf_y(), new Color(255, 0, 0));
                        //disableEditing();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        System.out.println("remove layout");
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {

                    }
                });
                txt[i][j] = tf;
                board.add(txt[i][j]);
            }
        }
        add(board, BorderLayout.CENTER);
    }

    /*
    private void setButtons() {
        btn_grids = new GridLayout(button_layout_row, button_count);
        buttons = new JPanel();
        buttons.setLayout(btn_grids);

        setSubmit();
        buttons.add(submit);

        setPass();
        buttons.add(pass);

        setRevoke();
        buttons.add(revoke);

        setVote();
        buttons.add(vote);

        board.add(buttons);
        //add(buttons, BorderLayout.SOUTH);
    }
    */

    private void markInitialization() {
        mark_grids = new GridLayout(1, 2);
        mark = new JPanel();
        mark.setLayout(mark_grids);

        username_lb = new JLabel("username");
        score_lb = new JLabel("0");
        mark.add(username_lb);
        mark.add(score_lb);
        add(mark, BorderLayout.NORTH);
    }

    /*
    private void setSubmit() {
        // submit button
        submit = new JButton("submit");
        submit.setName("submit");
        submit.setEnabled(true);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("submit " + modification.getX() + " " + modification.getY() + " " + modification.getLetter());
//                int bonus = highlightTouchedLetters(modification.getX(), modification.getY(), new Color(0,0,0)) - 3;
//                int current = Integer.parseInt(score_lb.getText());
//                score_lb.setText(String.valueOf(bonus + current));
//                ableEditing();
            }
        });
    }

    private void setPass() {
        // pass button
        pass = new JButton("pass");
        pass.setName("pass");
        //pass.setSize(20, 50);
        pass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("pass");
            }
        });
    }

    private void setRevoke() {
        // revoke button
        revoke = new JButton("revoke");
        revoke.setName("revoke");
        //revoke.setSize(20, 50);
        revoke.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("revoke");
//                highlightTouchedLetters(modification.getX(), modification.getY(), new Color(0, 0, 0));
//                removeChar(modification.getX(), modification.getY());
//                ableEditing();
            }
        });
    }

    private void setVote() {
        vote = new JButton("vote");
        vote.setName("vote");
        vote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("vote");
            }
        });
    }
    */

    private void removeChar(int x, int y) {
        txt[x][y].setText(null);
    }

    public static void main(String[] args) {
        ScrabbleBoard scrabbleBoard = new ScrabbleBoard();
    }

    private void createUIComponents() {
        //board = new JPanel();
        //board.setBorder(new EmptyBorder(5, 5, 5, 5));
        //board.setLayout(new FlowLayout());

        //DefaultTableModel defaultTableModel = new DefaultTableModel(board_rows, board_colomns);
        table = new JTable(board_rows, board_colomns) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component comp = super.prepareRenderer(r, row, col);

                if (modification != null &&
                        row == modification.getX() &&
                        col == modification.getY())
                    comp.setBackground(Color.green);
                else if (is_part_of_word(row, col))
                    comp.setBackground(Color.yellow);
                else
                    comp.setBackground(Color.white);

                comp.setForeground(Color.black);

                return comp;
            }
        };

        // this is the parent of table
        scrollTable = new JScrollPane(table);

        table.getParent().getParent().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                table.setRowHeight(table.getParent().getHeight() / table.getRowCount());

                Font curr_font = table.getFont();
                double dist = Math.sqrt(
                        Math.pow(table.getColumnModel().getColumn(0).getWidth(), 2) +
                        Math.pow(table.getRowHeight(), 2));

                // TODO: MAGIC NUMBER
                table.setFont(new Font(curr_font.getFontName(), Font.PLAIN, (int)(dist/2.4)));
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        //table.setPreferredSize(new Dimension(20, 20));
        MatteBorder matteBorder = new MatteBorder(1, 1, 0, 0, Color.black);
        table.setBorder(matteBorder);
        table.setGridColor(Color.black);

        table.setTableHeader(null);
        //table.setPreferredScrollableViewportSize(table.getPreferredSize());


        //JScrollPane tableContainer = new JScrollPane(table);
        //board.add(tableContainer, BorderLayout.CENTER);

        setTableCellListener();

        setTableCellEditor();
    }
}
