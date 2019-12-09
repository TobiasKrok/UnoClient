package com.tobias.game.card;

import javafx.scene.image.ImageView;

public class Card {
    private CardType cardType;
    private CardColor cardColor;
    private int value;
    private ImageView image;

    public Card(CardType cardType, CardColor cardColor, int value, ImageView image) {
        this.cardColor = cardColor;
        this.cardType = cardType;
        // Return zero instead of null if card is a special card. Special cards does not have a value.
        this.value = value;
        this.image = image;
    }


    public CardColor getCardColor() {
        return cardColor;
    }


    public int getValue() {
        return value;
    }

    public CardType getCardType(){
        return cardType;
    }

    public ImageView getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "["+  cardType + "," + cardColor + "," + value + "]";
    }
}
