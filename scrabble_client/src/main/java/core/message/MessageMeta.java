package core.message;

import core.game.Agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MessageMeta {
    private List<Long> timeStamps;

    public MessageMeta(List<Long> timeStamps) {
        this.timeStamps = timeStamps;
    }

    public List<Long> getTimeStamps() {
        return Collections.unmodifiableList(timeStamps);
    }

    public void appendTimeStamps(Collection<Long> times) {
        timeStamps.addAll(times);
    }

    public void appendRecentTime() {
        timeStamps.add(System.nanoTime());
    }
}
