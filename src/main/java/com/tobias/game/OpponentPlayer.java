package com.tobias.game;

public class OpponentPlayer {

    private int id;
    private int cardsOnHandCount;

    public OpponentPlayer(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }


    public int getCardsOnHandCount() {
        return cardsOnHandCount;
    }

    public void setCardsOnHandCount(int count) {
        this.cardsOnHandCount = count;
    }
}
