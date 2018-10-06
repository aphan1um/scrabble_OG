package core.message;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import core.game.Agent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

// TODO: Handle thread-safety issues with add/removeEvents ??
public class EventMessageList {
    private Multimap<Message.MessageType, MessageEvent> events;

    public EventMessageList() {
        events = HashMultimap.create();
    }

    public Collection<MessageWrapper> fireEvent(Message msg, Message.MessageType msgType,
                                          Agent sender) {
        Collection<MessageEvent> eventsToFire = events.get(msgType);
        List<MessageWrapper> msgs = new ArrayList<>(eventsToFire.size());

        synchronized (events) {
            for (MessageEvent e : eventsToFire) {
                MessageWrapper[] msgWraps = e.onMsgReceive(msg, sender);

                if (msgWraps != null)
                    msgs.addAll(Arrays.asList(msgWraps));
            }
        }

        return msgs;
    }

    public void addEvents(MessageEvent... eventList) {
        synchronized (events) {
            for (MessageEvent e : eventList) {
                Type generic_type = ((ParameterizedType)
                        e.getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[0];
                Class<? extends Message> t = (Class<? extends Message>)generic_type;

                events.put(Message.fromMessageClass(t), e);
            }
        }
    }

    public void removeEvents(MessageEvent... eventList) {
        synchronized (events) {
            for (MessageEvent e : eventList) {
                Type generic_type = ((ParameterizedType)
                        e.getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[0];
                Class<? extends Message> t = (Class<? extends Message>)generic_type;

                events.remove(Message.fromMessageClass(t), e);
            }
        }
    }
}
