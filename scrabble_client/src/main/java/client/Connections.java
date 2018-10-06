package client;

import client.listeners.ScrabbleClientListener;
import client.listeners.ScrabbleServerListener;
import core.game.Agent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Connections {
    private static ScrabbleClientListener listener;
    private static ScrabbleServerListener server;
    private static ObjectProperty<Agent> playerProperty;

    static {
        playerProperty = new SimpleObjectProperty<>(new Agent(Agent.AgentType.PLAYER));
    }

    /** Returns a connection that is listening to the server. */
    public static ScrabbleClientListener getListener() {
        if (listener == null) {
            listener = new ScrabbleClientListener(playerProperty.get().getName());
        }

        return listener;
    }

    /** Returns a connection represents the server/host. */
    public static ScrabbleServerListener getServer() {
        if (server == null) {
            server = new ScrabbleServerListener("Server");
        }

        return server;
    }

    public ObjectProperty<Agent> playerProperty() {
        return playerProperty;
    }
}
