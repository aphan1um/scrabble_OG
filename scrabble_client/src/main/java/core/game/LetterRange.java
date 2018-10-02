package core.game;

public class LetterRange {
    public enum LetterDirection {
        HORIZONTAL,
        VERTICAL
    }

    private int start;
    private int end;
    private LetterDirection direction;

    public LetterRange(int start, int end, LetterDirection dir) {
        this.start = start;
        this.end = end;
        this.direction = dir;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LetterRange))
            return false;

        LetterRange compare = (LetterRange)obj;

        return (start == compare.start && end == compare.end &&
                direction == ((LetterRange) obj).direction);
    }
}
