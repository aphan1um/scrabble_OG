package core.messageType;

import core.game.Player;
import core.message.Message;

// Request Player Data (PD) Message
public class PlayerStatusMsg implements Message {
    private Player player;
    private NewStatus status;

    public enum NewStatus {
        DISCONNECTED,
        JOINED
    }

    public PlayerStatusMsg(Player player, NewStatus status) {
        super();
        this.player = player;
        this.status = status;
    }
}
