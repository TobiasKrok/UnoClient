package com.tobias.gui.components;


import javafx.scene.layout.HBox;

public class CardView extends HBox {
    private int cardHeight;
    private int cardWidth;
    private double margin;
    private int padding;


    public CardView() {
        cardHeight = 150;
        cardWidth = 100;
        margin = -30;
        padding = 40;
//        widthProperty().addListener((obv, oldVal, newVal) -> {
//            fitCards();
//        });
//        getChildren().addListener((ListChangeListener<Node>) change -> {
//            while (change.next()) {
//                if (change.wasRemoved() || change.wasAdded()) {
//                    fitCards();
//                }
//            }
//        });
    }


//    public void addItem(Card card) {
//        card.getImage().setFitHeight(cardHeight);
//        card.getImage().setFitWidth(cardWidth);
//        HBox.setMargin(card.getImage(), new Insets(0, margin, 0, 0));
//        setCardEvents(card);
//        getChildren().add(card.getImage());;
//
//    }
//
//    private void setCardEvents(Card card) {
//        card.getImage().setOnMouseEntered((event) -> {
//
//            TranslateTransition tt = new TranslateTransition(Duration.millis(100), card.getImage());
//            tt.setToY(-37);
//            tt.play();
//
//        });
//        card.getImage().setOnMouseClicked((event) ->{
//            card.getImage().setOnMouseEntered(null);
//            card.getImage().setOnMouseExited(null);
//            Main.controller.addToTable(card);
//            card.getImage().setOnMouseClicked(null);
//        });
//
//        card.getImage().setOnMouseExited((event) -> {
//            TranslateTransition tt = new TranslateTransition(Duration.millis(100), card.getImage());
//            tt.setToY(0);
//            tt.play();
//
//        });
//    }
//    public void setCardProperties(ImageView v) {
//        v.setFitHeight(cardHeight);
//        v.setFitWidth(cardWidth);
//    }
//
//    public void fitCards() {
//        double length = (getChildren().size() * (cardWidth + margin)) - margin;
//        double desiredLength = getWidth() - padding - cardWidth;
//        double overlappedCardWidth = desiredLength / getChildren().size() - 1;
//        if (length + padding > getWidth()) {
//            margin = -(cardWidth - overlappedCardWidth);
//            for (Node n : getChildren()) {
//                HBox.setMargin(n, new Insets(0, margin, 0, 0));
//            }
//        } else if (length + padding < getWidth()) {
//            if (margin < -30) {
//                margin = cardWidth - overlappedCardWidth;
//            }
//        }
//    }

}

