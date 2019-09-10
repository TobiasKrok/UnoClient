package com.tobias.game.card;

public class Card {
    private CardType cardType;
    private CardColor cardColor;
    private int value;

    public Card(CardType cardType, CardColor cardColor, int value) {
        this.cardColor = cardColor;
        this.cardType = cardType;
        // Return zero instead of null if card is a special card. Special cards does not have a value.
        this.value = value;
    }
    // If card is not a special card, CardType will be set to NORMAL as default.


    public CardColor getCardColor() {
        return cardColor;
    }


    public int getValue() {
        return value;
    }
    public CardType getCardType(){
        return cardType;
    }


    @Override
    public String toString() {
        return "cardColor: " + cardColor + " value: " + value + " cardType: " + cardType;
    }
}
