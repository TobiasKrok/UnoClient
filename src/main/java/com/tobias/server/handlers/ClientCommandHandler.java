package com.tobias.server.handlers;


import com.tobias.server.ServerConnection;
import com.tobias.server.command.Command;
import com.tobias.server.command.CommandType;

public class ClientCommandHandler implements CommandHandler {

    private ServerConnection connection;

    public ClientCommandHandler(ServerConnection connection) {
        this.connection = connection;
    }
    @Override
    public void process(Command command) {
    if (command.getType() == CommandType.CLIENT_CONNECT) {
        connection.write("",1,CommandType.CLIENT_CONNECT);
    }
    }
}
