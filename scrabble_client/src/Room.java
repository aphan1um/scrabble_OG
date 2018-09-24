import javax.swing.*;
import java.util.ArrayList;

public class Room {
    private JList list1;
    private JPanel room;
    private JButton readyButton;
    private JTextArea textArea1;
    private ArrayList player = new ArrayList<String>();
    JFrame frame = new JFrame("Room");

    public void setPlayer(ArrayList player){
        this.player = (ArrayList) player.clone();
    }
    public ArrayList getPlayer(){
        return player;
    }

    public void initial(ArrayList Player) {
        Room roomGUI = new Room();
        roomGUI.setPlayer(Player);
        frame.setContentPane(roomGUI.room);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 500);
        DefaultListModel model = new DefaultListModel();
        for(int i = 0;i < roomGUI.player.size();i++){
            model.addElement(roomGUI.player.get(i));
        }
        roomGUI.list1.setModel(model);
        frame.setVisible(true);
    }
}
