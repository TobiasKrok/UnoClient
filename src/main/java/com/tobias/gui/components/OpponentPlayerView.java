package com.tobias.gui.components;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class OpponentPlayerView extends Region {

    private int cards;
    private ImageView card;
    private Label cardCountLabel;
    private Label userNameLabel;

    public OpponentPlayerView(ImageView card, String userName) {
        this.card = card;
        this.userNameLabel = new Label();
        this.cardCountLabel = new Label();
        userNameLabel.setText(userName);
        userNameLabel.setTextFill(Color.WHITE);
        userNameLabel.setStyle("-fx-font-weight: bold");
        getChildren().add(card);
        getChildren().add(cardCountLabel);
        getChildren().add(userNameLabel);
        adjustLabels();
    }

    private void adjustLabels() {
        Bounds cardBounds = card.localToParent(card.getBoundsInLocal());
        userNameLabel.setLayoutY(cardBounds.getMaxY() + 30);
        userNameLabel.setLayoutX(cardBounds.getMaxX() / 2);
    }
    public void rotateLabels(double v) {
        userNameLabel.setRotate(v);
        cardCountLabel.setRotate(v);
    }

    public ImageView getCard() {
        return this.card;
    }
    public void setCardCount(int n) {
        cardCountLabel.setText("Card count: " + n);
    }

    public String getLabelText() {
        return userNameLabel.getText();
    }

}