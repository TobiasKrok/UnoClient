package com.tobias.game;

public class OpponentPlayer extends Player {

    private int id;
    private int cardsOnHandCount;

    public OpponentPlayer(int id) {
        super(id);
    }

    public int getCardsOnHandCount() {
        return cardsOnHandCount;
    }

    public void setCardsOnHandCount(int count) {
        this.cardsOnHandCount = count;
    }
}
