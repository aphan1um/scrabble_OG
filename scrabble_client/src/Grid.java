import java.awt.*;

public class Grid extends GridLayout {
    public Boolean getIsEmpty() {
        return isEmpty;
    }

    public void setIsEmpty(Boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    private Boolean isEmpty;
    private char letter;

//    public Grid() {
//        super();
//        setIsEmpty(true);
//        setLetter(' ');
//    }
}
