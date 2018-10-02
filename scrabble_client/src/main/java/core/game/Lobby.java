package core.game;

import java.util.ArrayList;
import java.util.List;

// TODO: Concurrency issues?
public class Lobby {
    private List<Player> playerList;
    private transient Player owner;

    public Lobby(Player owner) {
        this.owner = owner;
        playerList = new ArrayList<>();
        playerList.add(owner);
    }
}
