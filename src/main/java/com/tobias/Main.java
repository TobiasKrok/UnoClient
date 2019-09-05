package com.tobias;


import com.tobias.server.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
private static Logger LOGGER = LogManager.getLogger(Main.class.getName());
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            LOGGER.info("Connected to server:" + socket.getRemoteSocketAddress().toString());
            ServerConnection serverConnection = new ServerConnection(socket);
            new Thread(serverConnection).start();
            ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            ses.schedule(new Runnable() {
                @Override
                public void run() {
                    if(!serverConnection.isIdReceived()){
                        LOGGER.fatal("Server did not accept connection, no ID was received. Disconnecting..");
                        serverConnection.close();
                    }
                }
            },2, TimeUnit.SECONDS);
        } catch (IOException e) {
            LOGGER.fatal("Failed to connect to server!",e);
        }
    }
}
