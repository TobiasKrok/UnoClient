package com.tobias.server.handlers;


import com.tobias.server.ServerConnection;
import com.tobias.server.command.Command;

public class ClientCommandHandler extends AbstractCommandHandler {

    private ServerConnection connection;

    public ClientCommandHandler(ServerConnection connection) {
        this.connection = connection;
    }

    @Override
    public void process(Command command) {
        switch (command.getType()) {
            case CLIENT_REGISTERID:
                connection.setClientId(Integer.parseInt(command.getData()));
                break;
            case CLIENT_CONNECTED:
                //registers opponents
        }
    }
}
