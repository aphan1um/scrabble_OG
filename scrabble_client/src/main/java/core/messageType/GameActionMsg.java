package core.messageType;

import core.message.Message;

import java.awt.*;

public class GameActionMsg implements Message {
    private Point moveLocation;
    private Character letter;

    public GameActionMsg(Point moveLocation, Character letter) {
        super();
        this.moveLocation = moveLocation;
        this.letter = letter;
    }
}
