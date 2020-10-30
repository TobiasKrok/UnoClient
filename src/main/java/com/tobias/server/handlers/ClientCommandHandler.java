package com.tobias.server.handlers;


import com.tobias.Main;
import com.tobias.game.ClientPlayer;
import com.tobias.game.OpponentPlayer;
import com.tobias.server.ServerConnection;
import com.tobias.server.command.Command;
import com.tobias.server.command.CommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
                Map<Integer, List<String>> connectedPlayers = parseConnectedPlayerCommand(command.getData());
                for(Integer key : connectedPlayers.keySet()) {
                   boolean exists = Main.getLobbyController().getConnectedPlayers().stream()
                            .anyMatch(o -> o.getId() == key);
                   if(!exists) {
                       //Only add opponentplayer, clientplayer will always be present
                       // Index 0 of the passed list is the username of the client. Index 1 is the status
                       String username = connectedPlayers.get(key).get(0);
                       // We receive a string with the status of the client. If the client is not ready, "false" is sent from the server.
                       // Here we just parse the string boolean to a true boolean so we can set the status of the player
                       boolean ready = Boolean.parseBoolean(connectedPlayers.get(key).get(1));
                       OpponentPlayer player = new OpponentPlayer(key,username);
                       Main.getLobbyController().addPlayerToView(player);
                       Main.getLobbyController().setPlayerStatus(player,ready);
                   }
                }
                break;
                // Combine these two commands because they are similar in action
            case CLIENT_NOTREADY:
            case CLIENT_READY:
                // If empty, the client itself sent the command. Otherwise we received the command from server
                if(command.getData().isEmpty()) {
                    connection.write(command);
                } else {
                    int id = Integer.parseInt(command.getData());
                    boolean ready = (command.getType() == CommandType.CLIENT_READY);
                    Main.getLobbyController().getConnectedPlayers().stream()
                            .filter(o -> o.getId() == id)
                            .forEach(p -> Main.getLobbyController().setPlayerStatus(p,ready));
                    }
                break;
            case CLIENT_CONNECT:
                connection.write(command);
                break;
        }
    }
    private Map<Integer, List<String>> parseConnectedPlayerCommand(String data) {
        Map<Integer, List<String>> players = new HashMap<>();
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(data);
        String[] props;
        while (m.find()) {
            props = m.group(1).split(":");
            // Index 0 is the ID, 1 is the username amd 2 is the status of the player
            // We add the username and status to an arraylist
            List<String> playerInfo = Arrays.asList(props[1], props[2]);
            players.put(Integer.parseInt(props[0]),playerInfo);
        }
        return players;
    }
}
