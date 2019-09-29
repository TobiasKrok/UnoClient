package com.tobias.game;

import com.tobias.game.card.Table;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Player player;
    private int players;
    private Table table;
    private boolean inProgress;
    private List<OpponentPlayer> opponentPlayers;

    public Game (Player p) {
        this.player = p;
        this.opponentPlayers =  new ArrayList<>();
        this.table = new Table();
    }

    boolean isInProgress() {
        return inProgress;
    }

    public Table getTable() {
        return table;
    }

    void setOpponentPlayers(List<OpponentPlayer> players) {
        this.opponentPlayers = players;
    }

    void start() {

    }
}
