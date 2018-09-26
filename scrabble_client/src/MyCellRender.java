import javax.swing.*;
import java.awt.*;

public class MyCellRender extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        Component c =super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
        if(isSelected){
            c.setForeground(Color.red);
        }
        return c;
    }
}
