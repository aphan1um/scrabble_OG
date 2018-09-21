import javax.swing.*;
import java.awt.*;


public class ScrabbleBoard extends JFrame {
    private final int rows = 20;
    private final int colomns = 20;

    private GridLayout grids;
    private GridLayout btn_grids;
    private JPanel board;
    private JPanel buttons;

    public ScrabbleBoard() {
        boardInitialization();
        buttonsInitialization();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        validate();
    }

    private void boardInitialization() {
        grids = new GridLayout(20, 20);
        board = new JPanel();
        board.setLayout(grids);

        JTextField[][] txt = new JTextField[rows][colomns];
        for(int i = 0; i < txt.length; i++) {
            for(int j = 0; j < txt[i].length; j++) {
                JTextField tf = new JTextField();
                tf.setDocument(new JTextFieldCharLimit(1));

                txt[i][j] = tf;
                board.add(txt[i][j]);
            }
        }
        add(board, BorderLayout.WEST);
    }

    private void buttonsInitialization() {
        btn_grids = new GridLayout(3, 1);
        buttons = new JPanel();
        buttons.setLayout(btn_grids);

        JButton submit = new JButton("submit");
        submit.setSize(20, 50);
        buttons.add(submit);
        JButton pass = new JButton("pass");
        pass.setSize(20, 50);
        buttons.add(pass);
        JButton revoke = new JButton("revoke");
        revoke.setSize(20, 50);
        buttons.add(revoke);
        add(buttons, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        ScrabbleBoard board = new ScrabbleBoard();
        board.setSize(500, 500);
        board.setResizable(false);
    }
}
