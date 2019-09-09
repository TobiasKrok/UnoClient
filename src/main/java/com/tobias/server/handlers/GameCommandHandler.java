package com.tobias.server.handlers;

import com.tobias.game.GameManager;
import com.tobias.server.ServerConnection;
import com.tobias.server.command.Command;
import com.tobias.server.command.CommandType;


public class GameCommandHandler implements CommandHandler {

    private GameManager gameManager;
    private ServerConnection serverConnection;
    public GameCommandHandler(GameManager gameManager, ServerConnection serverConnection) {
        this.gameManager = gameManager;
    }

    @Override
    public void process(Command command) {
        if(command.getType() == CommandType.PLAYER_DRAWCARD) {
            System.out.println("card received " + command.getData());
        }
    }

}
