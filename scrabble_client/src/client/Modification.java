package client;

public class Modification {
    private int x;
    private int y;
    private char letter;
    private int score = 0;
    private boolean hori = false;

    public boolean isHori() {
        return hori;
    }

    public void setHori(boolean hori) {
        this.hori = hori;
    }

    public boolean isVerti() {
        return verti;
    }

    public void setVerti(boolean verti) {
        this.verti = verti;
    }

    private boolean verti = false;

    public int getScore() {
        return score;
    }

    public void addScore() {
        this.score++;
    }

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
