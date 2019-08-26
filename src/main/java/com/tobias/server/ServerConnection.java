package com.tobias.server;


import com.tobias.server.command.CommandType;
import com.tobias.server.command.CommandWorker;
import com.tobias.server.handlers.ClientCommandHandler;
import com.tobias.server.handlers.CommandHandler;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerConnection implements Runnable {

    private BufferedWriter output;
    private BufferedReader input;
    private Socket socket;
    private CommandWorker worker;
    private Map<String,CommandHandler> handlers;
    private int clientId;

    public ServerConnection(Socket socket) {
        try {
            this.handlers = new HashMap<>();
            this.worker = new CommandWorker(handlers);
            this.socket = socket;
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.handlers.put("CLIENT",new ClientCommandHandler(this));
        new Thread(worker).start();
        while (true) {
            System.out.println(clientId);
            try {
                if(input.ready()){
                    worker.processCommand(read());
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("input.ready() thread sleep interrupted: " + e.getMessage());
                    }

                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
    }
   public void setClientId(int id) {
        this.clientId = id;
   }

    public void write(String data, CommandType type) {
        try {
            output.write("TYPE:" + type.toString() + " DATA:" + data);

        } catch (IOException e) {
            System.out.println("GameServer write error: " + e.getMessage());
        } finally {
            try {
                output.newLine();
                output.flush();
            } catch (IOException e) {
                System.out.println("Output close error: " + e.getMessage());
            }

        }
    }

    private void close() {
        try {
            output.close();
            input.close();
        } catch (IOException e) {
            System.out.println("Input/Output close error: " + e.getMessage());
        }
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
