package com.tobias.game;

import com.tobias.game.card.Card;
import com.tobias.game.card.Table;

import java.util.List;

public class GameManager {

    private Game game;
    private Table table;

    public void createNewGame(Player player, List<OpponentPlayer> opponentPlayers) {
        this.game = new Game(player,opponentPlayers);
        this.table = game.getTable();
    }
    public Game getGame() {
        return this.game;
    }
    public void addCardToPlayer(List<Card> cards) {
        game.getPlayer().addToHand(cards);
    }

    public void setDeckCount(int count) {
        table.getDeck().setCount(count);
    }
    public void setTopCard(Card card) {
        table.setTopCard(card);
    }
    public void setCardsOnTableCount(int count) {
        table.setCardsLayedCount(count);
    }

}
