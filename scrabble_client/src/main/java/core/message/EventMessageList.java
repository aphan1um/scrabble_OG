package core.message;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import core.game.Agent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

// TODO: Handle thread-safety issues with add/removeEvent ??
public class EventMessageList {
    private Multimap<Message.MessageType, MessageEvent> events;

    public EventMessageList() {
        events = HashMultimap.create();
    }

    public List<MessageWrapper> fireEvent(Message msg, Message.MessageType msgType,
                                          Set<Agent> agents, Agent sender) {
        Collection<MessageEvent> eventsToFire = events.get(msgType);
        List<MessageWrapper> msgs = new ArrayList<>(eventsToFire.size());

        for (MessageEvent e: eventsToFire) {
            msgs.add(e.onMsgReceive(msg, agents, sender));
        }

        return msgs;
    }

    public <T extends Message> void addEvent(MessageEvent<T> event) {
        Type generic_type = ((ParameterizedType)
                event.getClass().getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
        Class<? extends Message> t = (Class<? extends Message>)generic_type;

        synchronized (events) {
            events.put(Message.fromMessageClass(t), event);
        }
    }

    public <T extends Message> void removeEvent(MessageEvent<T> event) {
        Type generic_type = ((ParameterizedType)
                event.getClass().getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
        Class<? extends Message> t = (Class<? extends Message>)generic_type;

        synchronized (events) {
            events.remove(Message.fromMessageClass(t), event);
        }
    }
}
