package com.tobias.game;

import com.tobias.game.card.Card;

import java.util.ArrayList;
import java.util.List;

public class ClientPlayer extends Player {
    private List<Card> hand;
    private int id;

    public ClientPlayer(int id) {
        super(id);
        this.hand = new ArrayList<>();
    }

    public void addToHand(List<Card> cards) {
        this.hand.addAll(cards);
    }
}
