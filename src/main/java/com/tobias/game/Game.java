package com.tobias.game;

import com.tobias.game.card.Table;

public class Game {

    private Player player;
    private int players;
    private Table table;
    private boolean inProgress;

    public Game (Player p) {
        this.player = p;
        this.table = new Table();
    }

    boolean isInProgress() {
        return inProgress;
    }

    public Table getTable() {
        return table;
    }

    void start() {

    }
}
