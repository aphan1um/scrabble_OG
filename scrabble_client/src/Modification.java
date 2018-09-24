public class Modification {
    private int x;
    private int y;
    private char letter;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getLetter() {
        return letter;
    }

    public Modification(int x, int y, char letter) {
        this.x = x;
        this.y = y;
        this.letter = letter;
    }
}
