import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;

public class JTextFieldCharLimit extends PlainDocument {

    private final int limit = 1;

    public JTextFieldCharLimit(int limitation) {

    }

    public void insertString(int offset, String str, AttributeSet set) throws BadLocationException {
        if(str == null) {
            return;
        }
        else if(getLength() + str.length() <= limit && Character.isLetter(str.toCharArray()[0])) {
            str = str.toUpperCase();
            super.insertString(offset, str, set);
        }
    }
}
