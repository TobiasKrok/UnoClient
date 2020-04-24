package com.tobias.gui.components;

import com.tobias.game.card.Card;
import javafx.scene.layout.StackPane;

import java.util.Random;

public class TableCardView extends StackPane {

    private Card topCard;

    public void addCard(Card card) {
        topCard = card;
        Random r = new Random();
        // Remove any card effects
        card.getImage().setEffect(null);
        card.getImage().setRotate(r.nextInt(30 + 10 ) - 30);
        getChildren().add(card.getImage());
        card.getImage().setTranslateY(0);
        card.getImage().setTranslateX(0);
        // Remove bottom card if there are more than 3 cards on the table.
        if(getChildren().size() > 3) {
            getChildren().remove(0);
        }
    }

    public Card getTopCard() {
        return topCard;
    }
}
