package core.game;

public class Player {
    public String name;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Player && ((Player)obj).name.equals(name));
    }
}
