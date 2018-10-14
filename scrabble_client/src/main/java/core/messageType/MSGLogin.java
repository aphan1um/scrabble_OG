package core.messageType;

import core.game.Agent;
import core.message.Message;

public class MSGLogin implements Message {
    private Agent player;

    public MSGLogin(Agent player) {
        this.player = player;
    }

    public Agent getPlayer() { return player; }
}
