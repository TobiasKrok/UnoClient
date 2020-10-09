package com.tobias.game;

import com.tobias.game.card.Card;
import com.tobias.game.card.Table;

import java.util.ArrayList;
import java.util.List;

 class Game {

    private ClientPlayer clientPlayer;
    private Table table;
    private boolean inProgress;
    private List<OpponentPlayer> opponentPlayers;
    private Player currentPlayerTurn;
    private List<Player> players;


    Game(ClientPlayer p, List<OpponentPlayer> opponentPlayers) {
        this.clientPlayer = p;
        this.opponentPlayers = opponentPlayers;
        this.table = new Table();
        this.players = new ArrayList<>();
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


     public Player getCurrentPlayerTurn() {
         return currentPlayerTurn;
     }

     List<OpponentPlayer> getOpponentPlayers() {
        return opponentPlayers;
    }

    void setCurrentPlayerTurn(Player player) {
        this.currentPlayerTurn = player;
    }


    List<Card> getClientPlayerHand() {
        return clientPlayer.getHand();
    }
}
