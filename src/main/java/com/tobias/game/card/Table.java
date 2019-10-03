package com.tobias.game.card;



public class Table {

    private Deck deck;
    private Card topCard;
    private int cardsLayedCount;


    public Table() {
        this.deck = new Deck();
    }

    public Deck getDeck() {
        return deck;
    }

    public Card getTopCard() {
        return topCard;
    }

    public void setTopCard(Card topCard) {
        this.topCard = topCard;
    }

    public int getCardsLayedCount() {
        return cardsLayedCount;
    }

    public void setCardsLayedCount(int cardsLayed) {
        this.cardsLayedCount = cardsLayed;
    }
}
