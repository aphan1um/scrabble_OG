import javax.swing.*;
import java.awt.*;


public class ScrabbleBoard extends JFrame {
    private final int rows = 20;
    private final int colomns = 20;

    private GridLayout grids;
    private JPanel board;

    public ScrabbleBoard() {
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
        add(board, BorderLayout.CENTER);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        validate();
    }

    public static void main(String[] args) {
        ScrabbleBoard board = new ScrabbleBoard();
        board.setSize(500, 500);
        board.setResizable(false);
    }
}
