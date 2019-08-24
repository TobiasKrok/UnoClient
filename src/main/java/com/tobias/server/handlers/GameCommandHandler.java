package com.tobias.server.handlers;

import com.tobias.game.GameManager;
import com.tobias.server.command.Command;


public class GameCommandHandler implements CommandHandler {

    private GameManager gameManager;
    public GameCommandHandler(GameManager gameManager)
    {
        this.gameManager = gameManager;
    }

    @Override
    public void process(Command command) {

    }

}
