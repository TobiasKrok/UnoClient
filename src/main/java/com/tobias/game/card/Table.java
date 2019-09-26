package com.tobias.game.card;



public class Table {

    private Deck deck;
    private Card topCard;
    private int cardsLayed;


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

    public int getCardsLayed() {
        return cardsLayed;
    }

    public void setCardsLayed(int cardsLayed) {
        this.cardsLayed = cardsLayed;
    }
}
