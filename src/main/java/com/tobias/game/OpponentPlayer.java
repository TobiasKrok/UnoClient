package com.tobias.game;

public class OpponentPlayer extends Player {

    private int cardsOnHandCount;
    private String username;

    public OpponentPlayer(int id, String username) {
        super(id);
        this.username = username;
    }

    public int getCardsOnHandCount() {
        return cardsOnHandCount;
    }

    public void setCardsOnHandCount(int count) {
        this.cardsOnHandCount = count;
    }

    public String getUsername() {
        return username;
    }
}
