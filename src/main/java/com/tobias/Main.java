package com.tobias;


import com.tobias.server.ServerConnection;

import java.io.IOException;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            ServerConnection serverConnection = new ServerConnection(socket);
            new Thread(serverConnection).start();

        } catch (IOException e) {
            System.out.println("Socket error: " + e.getMessage());
        }
    }
}
