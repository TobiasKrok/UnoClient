package com.tobias.gui.components;


//import com.tobias.Main;

import com.tobias.Main;
import com.tobias.game.card.Card;
import com.tobias.game.card.CardColor;
import com.tobias.game.card.CardType;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class CardView extends HBox {
    private int cardHeight;
    private int cardWidth;
    private double margin;
    private int padding;
    private ColorAdjust colorAdjust;
    private boolean canSelect;
    // Private used to store card objects so we can reference specific cards
    private List<Card> cards;
    private CardColor forceColor;


    public CardView() {
        colorAdjust = new ColorAdjust();
        cardHeight = 150;
        cardWidth = 100;
        margin = -30;
        padding = 40;
        forceColor = CardColor.NONE;
        this.cards = new ArrayList<>();
        widthProperty().addListener((obv, oldVal, newVal) -> {
            fitCards();
        });
        getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                if (change.wasRemoved() || change.wasAdded()) {
                    fitCards();
                }
            }
        });
    }


    public void addItem(Card card) {
        cards.add(card);
        card.getImage().setFitHeight(cardHeight);
        card.getImage().setFitWidth(cardWidth);
        HBox.setMargin(card.getImage(), new Insets(0, margin, 0, 0));
        setCardEvents(card);
        getChildren().add(card.getImage());

    }

    private void setCardEvents(Card card) {
        card.getImage().setOnMouseEntered((event) -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(100), card.getImage());
            // Move card up when hovered over
            tt.setToY(-37);
            tt.play();
        });
        card.getImage().setOnMouseClicked((event) -> {
            if (canSelect && Main.getUnoController().isCardAllowed(card) ) {
                if (forceColor == CardColor.NONE || card.getCardColor() == forceColor || card.getCardType() == CardType.WILDDRAWFOUR || card.getCardType() == CardType.WILD) {
                    Platform.runLater(() -> card.getImage().setEffect(null));
                    // Clear forceColor
                    forceColor = CardColor.NONE;
                    // If the player has two cards left, we must tell the UnoController to show the Uno button for all clients.
                    if(cards.size() == 2) {
                        Main.getUnoController().setUno(true);
                    }
                    // Remove the card from the internal lists
                    cards.remove(card);
                    // Remove any events
                    card.getImage().setOnMouseEntered(null);
                    card.getImage().setOnMouseExited(null);
                    Main.getUnoController().clientAddToTable(card);
                    card.getImage().setOnMouseClicked(null);
                }
            }
        });

        card.getImage().setOnMouseExited((event) -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(100), card.getImage());
            tt.setToY(0);
            tt.play();

        });
    }

    public void setCardProperties(ImageView v) {
        v.setFitHeight(cardHeight);
        v.setFitWidth(cardWidth);
    }

    public void setCanSelect(boolean canSelect) {
        this.canSelect = canSelect;
        setColorGray(!canSelect);
    }

    public CardColor getForceColor() {
        return this.forceColor;
    }

    private void fitCards() {
        double length = (getChildren().size() * (cardWidth + margin)) - margin;
        double desiredLength = getWidth() - padding - cardWidth;
        double overlappedCardWidth = desiredLength / getChildren().size() - 1;
        if (length + padding > getWidth()) {
            margin = -(cardWidth - overlappedCardWidth);
            for (Node n : getChildren()) {
                HBox.setMargin(n, new Insets(0, margin, 0, 0));
            }
        } else if (length + padding < getWidth()) {
            if (margin < -30) {
                margin = cardWidth - overlappedCardWidth;
            }
        }
    }

    public void setForceColor(CardColor color) {
        forceColor = color;
        for (Card c : cards) {
            for (Node n : getChildren()) {
                if (c.getImage() == n) {
                    if (c.getCardColor() != color) {
                        colorAdjust.setSaturation(-0.8);
                        n.setEffect(colorAdjust);
                    }
                }
            }
        }
    }

    public List<Card> getCards() {
        return this.cards;
    }

    private void setColorGray(boolean grey) {
        if (grey) {
            colorAdjust.setSaturation(-0.8);
        } else {
            colorAdjust.setSaturation(0);
        }
        for (Node n : getChildren()) {
            n.setEffect(colorAdjust);
        }
    }
}

