package com.tobias.game;

import com.tobias.game.card.Table;

import java.util.List;

public class Game {

    private Player player;
    private Table table;
    private boolean inProgress;
    private List<OpponentPlayer> opponentPlayers;


    public Game (Player p, List<OpponentPlayer> opponentPlayers) {
        this.player = p;
        this.opponentPlayers =  opponentPlayers;
        this.table = new Table();
    }

    boolean isInProgress() {
        return this.inProgress;
    }

    Table getTable() {
        return this.table;
    }

    Player getPlayer() {
        return this.player;
    }

    OpponentPlayer getOpponentPlayerById(int id) {
        for(OpponentPlayer player : opponentPlayers) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }
    void start() {

    }

    public void
}
