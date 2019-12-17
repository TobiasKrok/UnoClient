package com.tobias.game;

import com.tobias.game.card.Card;
import com.tobias.game.card.Table;

import java.util.List;

 class Game {

    private ClientPlayer clientPlayer;
    private Table table;
    private boolean inProgress;
    private List<OpponentPlayer> opponentPlayers;
    private Player currentPlayerTurn;


    Game(ClientPlayer p, List<OpponentPlayer> opponentPlayers) {
        this.clientPlayer = p;
        this.opponentPlayers = opponentPlayers;
        this.table = new Table();
    }

    boolean isInProgress() {
        return this.inProgress;
    }

    Table getTable() {
        return this.table;
    }

    ClientPlayer getClientPlayer() {
        return this.clientPlayer;
    }

    OpponentPlayer getOpponentPlayerById(int id) {
        for (OpponentPlayer player : opponentPlayers) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }

    void start() {
    }
    List<OpponentPlayer> getOpponentPlayers() {
        return opponentPlayers;
    }

    void setCurrentPlayerTurn(Player player) {
        this.currentPlayerTurn = player;
    }

    int getDeckCount() {
        return table.getDeck().getCount();
    }
    Card getTopCard() {
        return table.getTopCard();
    }

    List<Card> getClientPlayerHand() {
        return clientPlayer.getHand();
    }
}
