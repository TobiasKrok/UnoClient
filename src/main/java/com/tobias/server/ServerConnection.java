package com.tobias.server;


import com.tobias.game.GameManager;
import com.tobias.server.command.Command;
import com.tobias.server.command.CommandWorker;
import com.tobias.server.handlers.ClientCommandHandler;
import com.tobias.server.handlers.CommandHandler;
import com.tobias.server.handlers.GameCommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerConnection implements Runnable {

    private BufferedWriter output;
    private BufferedReader input;
    private Socket socket;
    private CommandWorker worker;
    private Thread workerThread;
    private Map<String,CommandHandler> handlers;
    private boolean idReceived = false;
    private int clientId;
    private static final Logger LOGGER = LogManager.getLogger(ServerConnection.class.getName());

    public ServerConnection(Socket socket) {
        this.handlers = new HashMap<>();
        this.worker = new CommandWorker(handlers);
        this.socket = socket;
        try {
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
           LOGGER.fatal("Error initializing Reader/Writer",e);
        }
    }

    public void run() {
        this.handlers.put("CLIENT",new ClientCommandHandler(this));
        this.handlers.put("PLAYER",new GameCommandHandler(new GameManager(),this));
        LOGGER.debug("ServerConnection started");
        this.workerThread = new Thread(this.worker);
        this.workerThread.setName("CommandWorker-"+ workerThread.getId());
        LOGGER.debug("CommandWorker started");
        workerThread.start();
        while (true) {
            try {
                if(input.ready()){
                    worker.processCommand(read());
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LOGGER.error("Interrupted during sleep!",e);
                    }

                }
            } catch (IOException e) {
                LOGGER.fatal("Error while waiting for data from server!",e);
            }

        }
    }
   public void setClientId(int id) {
        this.clientId = id;
        this.idReceived = true;
   }

   public boolean idReceived(){
        return this.idReceived;
   }

    public void write(Command command) {
        try {
            output.write(command.toString());
            LOGGER.debug("Command sent to server: " + command.toString());
        } catch (IOException e) {
           LOGGER.fatal("Write failed to server!",e);
        } finally {
            try {
                output.newLine();
                output.flush();
            } catch (IOException e) {
                LOGGER.fatal("Connection to server lost, disconnecting..");
                close();
            }

        }
    }

    public void close() {
        try {
            socket.close();
            workerThread.interrupt();
            LOGGER.warn("Gracefully disconnected from server..");
            Thread.currentThread().interrupt();
        } catch (IOException e) {
           LOGGER.error("Error during disconnecting procedure",e);
        }
    }

    public int getId() {
        return this.clientId;
    }

    private String read() {
        try {
            return input.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
