package com.tobias.game;

public class OpponentPlayer extends Player {

    private int cardsOnHandCount;

    public OpponentPlayer(int id, String username) {
        super(id, username);
    }

    public int getCardsOnHandCount() {
        return cardsOnHandCount;
    }

    void setCardsOnHandCount(int count) {
        this.cardsOnHandCount = count;
    }

}
