import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class MouseAction implements MouseListener {
    private LoginGui loginGui;
    public void addReceivers(ArrayList invate, String receiver){
        if(!(invate.contains(receiver))){
            invate.add(receiver);
        }
    }
    public MouseAction(LoginGui loginGui){
        this.loginGui = loginGui;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        addReceivers(loginGui.getReceiver(),loginGui.getUserlist().getSelectedValue().toString());
    }
    @Override
    public void mousePressed(MouseEvent e) {
        //Todo Auto-generated method
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        //Todo Auto-generated method
        loginGui.getUserlist().setSelectionForeground(Color.red);
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        //Todo Auto-generated method
    }
    @Override
    public void mouseExited(MouseEvent e) {
        //Todo Auto-generated method
    }
}
