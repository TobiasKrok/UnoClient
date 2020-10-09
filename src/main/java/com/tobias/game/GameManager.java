package com.tobias.game;

import com.tobias.game.card.Card;
import com.tobias.game.card.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameManager {

    private Game game;
    private Table table;
    private List<Player> players;

    public GameManager() {
        this.players = new ArrayList<>();
    }

    public void createNewGame(ClientPlayer clientPlayer, List<OpponentPlayer> opponentPlayers) {
        this.game = new Game(clientPlayer, opponentPlayers);
        this.table = game.getTable();
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public Game getGame() {
        return this.game;
    }

    public void addCardToPlayer(List<Card> cards) {
        game.getClientPlayer().addToHand(cards);
    }


    public void setDeckCount(int count) {
        table.getDeck().setCount(count);
    }

    public void setOpponentPlayerCardCount(Map<Integer, Integer> count) {
        int oppId = (Integer) count.keySet().toArray()[0];
        if (game.getOpponentPlayerById((oppId)) != null) {
            game.getOpponentPlayerById(oppId).setCardsOnHandCount(count.get(oppId));
        }
    }

    public void setNextTurn(int id) {
        if (id == game.getClientPlayer().getId()) {
            game.setCurrentPlayerTurn(game.getClientPlayer());
        } else {
            game.setCurrentPlayerTurn(game.getOpponentPlayerById(id));
        }
    }

    public boolean isClientPlayerTurn() {
        return (game.getCurrentPlayerTurn() == game.getClientPlayer());
    }
    public int getClientPlayerId(){
        return game.getClientPlayer().getId();
    }

    public void disconnectPlayer(int id) {
        // TODO disconnect ClientPlayer
        game.getOpponentPlayers().remove(game.getOpponentPlayerById(id));
    }

}
