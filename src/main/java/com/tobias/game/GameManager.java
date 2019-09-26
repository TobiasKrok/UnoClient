package com.tobias.game;

import com.tobias.game.card.Card;
import com.tobias.game.card.Table;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private List<Integer> opponentIds;
    private Player player;
    private Game game;
    private Table table;


    public GameManager (Player p) {
        this.player = p;
        this.opponentIds = new ArrayList<>();
    }

    public void addOpponentId(int id) {
        if(!opponentIds.contains(id)) {
            opponentIds.add(id);
        }
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
