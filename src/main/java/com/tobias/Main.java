package com.tobias;


import com.tobias.server.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class Main {
private static Logger LOGGER = LogManager.getLogger(Main.class.getName());
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            if(socket.isConnected()) {
                LOGGER.info("Connected to server:" + socket.getRemoteSocketAddress().toString());
                ServerConnection serverConnection = new ServerConnection(socket);
                new Thread(serverConnection).start();
            }

        } catch (IOException e) {
            LOGGER.fatal("Failed to connect to server!");
        }
    }
}
