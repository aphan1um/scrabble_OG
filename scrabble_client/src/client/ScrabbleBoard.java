package client;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    private final int board_rows = 20;
    private final int board_colomns = 20;

    private final int button_count = 4;
    private final int button_layout_row = 1;

    private GridLayout grids;
    private GridLayout btn_grids;
    private JPanel board;
    private JPanel buttons;

    private ScrabbleTF[][] txt;

    private JButton submit;
    private JButton pass;
    private JButton revoke;
    private JButton vote;

    private Modification modification;

    public ScrabbleBoard() {
        boardInitialization();
        buttonsInitialization();
        this.setSize(scrabble_board_width, scrabble_board_height);
        this.setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        validate();
    }

    private void boardInitialization() {
        grids = new GridLayout(20, 20);
        board = new JPanel();
        board.setLayout(grids);

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
                        highlightTouchedLetters(tf.getStf_x(), tf.getStf_y(), new Color(255, 0, 0));
                        disableEditing();
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

    private void buttonsInitialization() {
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
        add(buttons, BorderLayout.SOUTH);
    }

    private void setSubmit() {
        // submit button
        submit = new JButton("submit");
        submit.setName("submit");
        submit.setEnabled(true);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("submit " + modification.getX() + " " + modification.getY() + " " + modification.getLetter());
                highlightTouchedLetters(modification.getX(), modification.getY(), new Color(0,0,0));
                ableEditing();
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
                highlightTouchedLetters(modification.getX(), modification.getY(), new Color(0, 0, 0));
                removeChar(modification.getX(), modification.getY());
                ableEditing();
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

    private void highlightTouchedLetters(int x, int y, Color color) {
        highlightTouchedLetters_South(x, y, color);
        highlightTouchedLetters_North(x, y, color);
        highlightTouchedLetters_East(x, y, color);
        highlightTouchedLetters_West(x, y, color);
    }

    private void highlightTouchedLetters_West(int x, int y, Color color) {
        if(txt[x][y].getText().isEmpty()) {
            return;
        }
        else {
            txt[x][y].setForeground(color);
            if(y - 1 >= 0) {
                highlightTouchedLetters_West(x, y - 1, color);
            }
        }
    }

    private void highlightTouchedLetters_East(int x, int y, Color color) {
        if(txt[x][y].getText().isEmpty()) {
            return;
        }
        else {
            txt[x][y].setForeground(color);
            if(y + 1 <= board_colomns - 1) {
                highlightTouchedLetters_East(x, y + 1, color);
            }
        }
    }

    private void highlightTouchedLetters_North(int x, int y, Color color) {
        if(txt[x][y].getText().isEmpty()) {
            return;
        }
        else {
            txt[x][y].setForeground(color);
            if(x - 1 >= 0) {
                highlightTouchedLetters_North(x - 1, y, color);
            }
        }
    }

    private void highlightTouchedLetters_South(int x, int y, Color color) {
        if(txt[x][y].getText().isEmpty()) {
            return;
        }
        else {
            txt[x][y].setForeground(color);
            if(x + 1 <= board_rows - 1) {
                highlightTouchedLetters_South(x + 1, y, color);
            }
        }
    }

    private void disableEditing() {
        // disable editing
        for(int i = 0; i < txt.length; i++) {
            for(int j = 0; j < txt[i].length; j++) {
                txt[i][j].setEditable(false);
            }
        }
    }

    private void ableEditing() {
        for(int i = 0; i < txt.length; i++) {
            for(int j = 0; j < txt[i].length; j++) {
                if(txt[i][j].getText().isEmpty()) {
                    txt[i][j].setEditable(true);
                }
            }
        }
    }

    private void removeChar(int x, int y) {
        txt[x][y].setText(null);
    }

    public static void main(String[] args) {
        ScrabbleBoard scrabbleBoard = new ScrabbleBoard();
    }
}
