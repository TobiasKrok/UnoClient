package com.tobias.server.handlers;


import com.tobias.Main;
import com.tobias.game.ClientPlayer;
import com.tobias.game.OpponentPlayer;
import com.tobias.game.Player;
import com.tobias.server.ServerConnection;
import com.tobias.server.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientCommandHandler implements CommandHandler {

    private ServerConnection connection;
    private static final Logger LOGGER = LogManager.getLogger(ClientCommandHandler.class.getName());

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
                //creates player object and adds it to view

                String[] playerInfo = command.getData().split(":");
                int playerId = Integer.parseInt(playerInfo[0]);
                if(playerId == connection.getId()) {
                    Main.getLobbyController().addPlayerToView(new ClientPlayer(playerId,playerInfo[1]));
                } else {
                    Main.getLobbyController().addPlayerToView(new OpponentPlayer(playerId,playerInfo[1]));
                }

                LOGGER.info("A client has connected! ID:" + Integer.parseInt(playerInfo[0]) + " Username:" + playerInfo[1]);
                break;
            case CLIENT_CONNECTEDPLAYERS:
                for(Map.Entry <Integer, String> entry : parseConnectedPlayerCommand(command.getData()).entrySet()) {
                   for(Player p : Main.getLobbyController().getConnectedPlayers()) {
                       if(!(p instanceof ClientPlayer) &&  ) {

                       }
                   }
                }
                break;
            case CLIENT_CONNECT:
                connection.write(command);
                break;
        }
    }

    private Map<Integer, String> parseConnectedPlayerCommand(String data) {
        Map<Integer, String> players = new HashMap<>();
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(data);
        String[] props;
        while (m.find()) {
            props = m.group(1).split(",");
            // Index 0 is the ID, 1 is the username.
            players.put(Integer.parseInt(props[0]),props[1]);
        }
        return players;
    }
}
