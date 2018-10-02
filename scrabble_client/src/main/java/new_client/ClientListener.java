package new_client;

import core.SocketListener;
import core.message.MessageWrapper;
import core.messageType.ChatMsg;

import java.io.IOException;
import java.net.Socket;

public class ClientListener extends SocketListener {
    public Socket socket;

    public ClientListener() {
        super();
    }

    public void start(String ip, int port) throws IOException {
        socket = this.startListener(ip, port);
    }

    public void sendChatMessage(String txt) {
        try {
            sendMessage(new ChatMsg(txt), socket);
        } catch (IOException e) {
            e.printStackTrace();
            triggerDisconnect(socket);
        }
    }

    @Override
    protected void onUserConnect(Socket s) {

    }

    @Override
    protected void prepareEvents() {

    }

    @Override
    protected boolean onMessageReceived(MessageWrapper msgRec, Socket s) {
        return false;
    }
}
