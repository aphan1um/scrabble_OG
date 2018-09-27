package server;

public interface PlayerStatusListener {
    public void onPlayerStatusChanged(Player p, PlayerStatus status);
}
