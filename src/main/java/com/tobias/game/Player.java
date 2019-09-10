package com.tobias.game;

import com.tobias.game.card.Card;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Card> hand;
    private int id;

    public Player(int id){
        this.hand = new ArrayList<>();
        this.id = id;
    }

    public void addToHand(List<Card> cards) {
        this.hand.addAll(cards);
    }
}
