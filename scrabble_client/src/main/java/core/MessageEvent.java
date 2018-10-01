package core;

public interface MessageEvent<T extends Message> {
    public Message onServerReceive(T recMessage);
    public Message onClientReceive(T recMessage);
}
