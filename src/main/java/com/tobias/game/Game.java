package com.tobias.game;

public class Game {

    private Player player;
    private int players;
    private boolean inProgress;

    public Game (Player p) {
        this.player = p;
    }

    protected boolean isInProgress() {
        return inProgress;
    }
}
