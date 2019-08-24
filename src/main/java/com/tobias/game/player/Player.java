package com.tobias.game.player;

import com.tobias.game.card.Card;

import java.util.List;

public class Player {
    private List<Card> hand;
    private int id;

    public Player(int id){
        this.id = id;
    }
}
