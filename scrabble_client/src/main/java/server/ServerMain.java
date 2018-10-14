package server;

import core.ConnectType;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) throws IOException {
        if (args.length != 1 || !args[0].matches("^[0-9]+$")) {
            System.out.println("Please input only a port number as an argument.");
            System.exit(-1);
        }

        int port = Integer.parseInt(args[0]);
        System.out.println("Connecting as port " + port + "...");

        // for internet use
        ScrabbleServerListener server = new ScrabbleServerListener("Server",
                ConnectType.INTERNET);
        server.start(port);
    }

}
