package com.tobias.game;

import com.tobias.game.card.Card;
import com.tobias.game.card.Table;

import java.util.List;

public class GameManager {

    private List<OpponentPlayer> opponentPlayers;
    private Player player;
    private Game game;
    private Table table;


    public GameManager (Player p, List<OpponentPlayer> players) {
        this.player = p;
        this.opponentPlayers = players;
        printlol();
    }
    
    public void createNewGame() {
        this.game = new Game(player);
        this.table = game.getTable();
    }
    public Game getGame() {
        return this.game;
    }
    public void addCardToPlayer(List<Card> cards) {
        player.addToHand(cards);
    }
}
