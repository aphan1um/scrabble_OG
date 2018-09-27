package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

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
    private final int scrabble_board_width = 500;
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
    JScrollPane tableContainer;
    private JPanel panel;
    private JPanel buttons;
    private JPanel scoreBoard;

    private JTextField tf;

    private JButton submit;
    private JButton pass;
    private JButton revoke;
    private JButton vote;

    private JLabel username_lb;
    private JLabel user;
    private JLabel score_lb;
    private JLabel score;

    private Modification modification;
    private int myScore = 0;
    private boolean myscore_flag = false;

    public ScrabbleBoard() {
        frame = new JFrame();
        frame.setPreferredSize(new Dimension(510, 610));
        setBoard();
        setScoreBoard();
        setTable();
        setButtons();
        frame.pack();
        frame.setVisible(true);
    }

    private void setBoard() {
        panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setLayout(new FlowLayout());
        frame.getContentPane().add(panel);
    }

    private void setTable() {
        table = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component comp = super.prepareRenderer(r, row, col);

                if (modification != null &&
                        row == modification.getX() &&
                        col == modification.getY()) {
                    comp.setBackground(Color.green);
                }
                else if (is_part_of_word(row, col)) {
                    comp.setBackground(Color.yellow);
                }
                else
                    comp.setBackground(Color.white);

                comp.setForeground(Color.black);
                return comp;
            }
        };

        DefaultTableModel tmodel = new DefaultTableModel(20, 20) {
            // set cell non-editable if it's not empty
            @Override
            public boolean isCellEditable(int row, int column) {
                if(table.getValueAt(row, column) == null || table.getValueAt(row, column).toString().isEmpty())
                    return true;
                else
                    return false;
            }
        };
        table.setModel(tmodel);

        table.setTableHeader(null);
        table.setRowHeight(500 / 20);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        MatteBorder matteBorder = new MatteBorder(1, 1, 0, 0, Color.black);
        table.setBorder(matteBorder);
        table.setGridColor(Color.black);

        tableContainer = new JScrollPane(table);
        tableContainer.setPreferredSize(new Dimension(510, 505));
        panel.add(tableContainer, BorderLayout.CENTER);
        setTableCellListener();

        setTableCellEditor();
    }

    private boolean is_part_of_word(int row, int col) {
        if (modification == null ||
                (modification.getX() != row && modification.getY() != col))
            return false;
        if (modification.getX() == row) {
            int direction = (int)Math.signum(col - modification.getY());

            for (int i = modification.getY(); direction * (col - i) >= 0; i += direction) {
                if (table.getValueAt(row, i) == null || table.getValueAt(row, i).toString().isEmpty())
                    return false;
            }
        }
        else { // vertical word
            int direction = (int)Math.signum(row - modification.getX());

            for (int j = modification.getX(); direction * (row - j) >= 0; j += direction) {
                if (table.getValueAt(j, col) == null || table.getValueAt(j, col).toString().isEmpty())
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

                if ((!tcl.getNewValue().toString().isEmpty())) {
                    modification = new Modification(tcl.getRow(), tcl.getColumn(), tcl.getNewValue().toString().toCharArray()[0]);
//                    System.out.println("new modi: " + modification.getX() + " " + modification.getY() + " " + modification.getLetter());
//
//                    System.out.println("Row   : " + tcl.getRow());
//                    System.out.println("Column: " + tcl.getColumn());
//                    System.out.println("Old   : " + tcl.getOldValue());
//                    System.out.println("New   : " + tcl.getNewValue());
                    table.repaint();
                    submit.setEnabled(true);
                    revoke.setEnabled(true);
                    tf.setEnabled(false);
                }
            }
        };
        TableCellListener tcl = new TableCellListener(table, action);
    }

    private void setTableCellEditor() {
        tf = new JTextField();
        tf.setDocument(new JTextFieldCharLimit());
//        tf.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
////                tf.setEnabled(false);
//                System.out.println("insert");
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                System.out.println("remove");
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                System.out.println("update");
//            }
//        });
        for(int i = 0; i < board_colomns; i ++) {
            table.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(tf));
        }
    }

    private void setButtons() {
        btn_grids = new GridLayout(button_layout_row, button_count);
        buttons = new JPanel();
        buttons.setSize(new Dimension(510, 50));
        buttons.setLayout(btn_grids);

        setSubmit();
        submit.setEnabled(false);
        buttons.add(submit);

        setPass();
        buttons.add(pass);

        setRevoke();
        revoke.setEnabled(false);
        buttons.add(revoke);

        setVote();
        vote.setEnabled(false);
        buttons.add(vote);

        panel.add(buttons);
        //add(buttons, BorderLayout.SOUTH);
    }

    private void setScoreBoard() {
        mark_grids = new GridLayout(1, 4);
        scoreBoard = new JPanel();
        scoreBoard.setSize(new Dimension(510, 50));
        scoreBoard.setLayout(mark_grids);

        String myname = "Murphy";
        username_lb = new JLabel("username: " );
        user = new JLabel(myname);
        score_lb = new JLabel("score: ");
        score = new JLabel(String.valueOf(myScore));
        scoreBoard.add(username_lb);
        scoreBoard.add(user);

        scoreBoard.add(score_lb);
        scoreBoard.add(score);
        panel.add(scoreBoard);
    }

    private void setSubmit() {
        // submit button
        submit = new JButton("submit");
        submit.setName("submit");
        submit.setEnabled(true);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myScore += getScore();
                System.out.println("score: " + String.valueOf(myScore));
                score.setText(String.valueOf(myScore));

                tf.setEnabled(true);
                submit.setEnabled(false);
                revoke.setEnabled(false);
                modification = null;
                table.repaint();


                System.out.println("submit");
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
                tf.setEnabled(true);
                revoke.setEnabled(false);
                submit.setEnabled(false);
                table.setValueAt(null, modification.getX(), modification.getY());

                modification = null;
                System.out.println("revoke");
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

    private int getScore() {
        int offset = 1;
        boolean flag_N = true, flag_E = true, flag_W = true, flag_S = true;
        while(flag_E || flag_N || flag_S || flag_W) {
            if(flag_E) {
                if (modification.getX() + offset <= 19) {
                    if(table.getValueAt(modification.getX() + offset, modification.getY()) == null
                    || table.getValueAt(modification.getX() + offset, modification.getY()).toString().isEmpty()) {
                        flag_E = false;
                    }
                    else {
                        modification.setHori(true);
                        modification.addScore();
                    }

                }
                else
                    flag_E = false;
            }
            if(flag_W) {
                if (modification.getX() - offset >= 0) {
                    if(table.getValueAt(modification.getX() - offset, modification.getY()) == null
                            || table.getValueAt(modification.getX() - offset, modification.getY()).toString().isEmpty()) {
                        flag_W = false;
                    }
                    else {
                        modification.setHori(true);
                        modification.addScore();
                    }

                }
                else
                    flag_W = false;
            }
            if(flag_N) {
                if (modification.getY() - offset >= 0) {
                    if(table.getValueAt(modification.getX(), modification.getY() - offset) == null
                            || table.getValueAt(modification.getX(), modification.getY() - offset).toString().isEmpty()) {
                        flag_N = false;
                    }
                    else {
                        modification.setVerti(true);
                        modification.addScore();
                    }

                }
                else
                    flag_N = false;
            }
            if(flag_S) {
                if (modification.getY() + offset <= 19) {
                    if(table.getValueAt(modification.getX(), modification.getY() + offset) == null
                            || table.getValueAt(modification.getX(), modification.getY() + offset).toString().isEmpty()) {
                        flag_S = false;
                    }
                    else {
                        modification.setVerti(true);
                        modification.addScore();
                    }

                }
                else
                    flag_S = false;
            }
            offset++;
        }

        System.out.println("bonus: " + String.valueOf(modification.getScore()));

        if(modification.isHori() && modification.isVerti()) {
            modification.addScore();
            modification.addScore();
        }
        else
            modification.addScore();

        return modification.getScore();
    }

    public static void main(String[] args) {
        ScrabbleBoard scrabbleBoard = new ScrabbleBoard();
    }
}
