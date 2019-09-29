package com.tobias.game;

public class OpponentPlayer {

    private int id;
    private int cardsOnHand;

    public OpponentPlayer(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }


    public int getCardsOnHand() {
        return cardsOnHand;
    }

    public void setCardsOnHand(int cardsOnHand) {
        this.cardsOnHand = cardsOnHand;
    }
}
