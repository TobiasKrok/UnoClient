package com.tobias;


import com.tobias.game.Game;
import com.tobias.game.GameManager;
import com.tobias.game.Player;
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
       ServerConnection serverConnection =  startServerConnection(args[0], Integer.parseInt(args[1]));
       checkForId(serverConnection);
       if(serverConnection.idReceived()){
           GameManager gameManager = new GameManager(new Player(serverConnection.getId()));
           serverConnection.setGameManager(gameManager);
           while (serverConnection.isRunning()) {

           }
       }
    }

    private static ServerConnection startServerConnection(String ip, int port) {
        ServerConnection serverConnection = null;
        try {
            Socket socket = new Socket(ip,port);
            LOGGER.info("Connected to server:" + socket.getRemoteSocketAddress().toString());
            serverConnection = new ServerConnection(socket);
            new Thread(serverConnection).start();

        } catch (IOException e) {
            LOGGER.fatal("Failed to connect to server!",e);
        }
        return serverConnection;
    }
    private static void checkForId(ServerConnection serverConnection) {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.schedule(new Runnable() {
            @Override
            public void run() {
                if(!serverConnection.idReceived()){
                    LOGGER.fatal("Server did not accept connection, no ID was received. Disconnecting..");
                    serverConnection.close();
                }
            }
        },4, TimeUnit.SECONDS);
    }
}
