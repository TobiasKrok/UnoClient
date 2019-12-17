package com.tobias.gui;

import com.tobias.game.OpponentPlayer;
import com.tobias.game.card.Card;
import com.tobias.gui.components.CardView;
import com.tobias.gui.components.OpponentPlayerView;
import com.tobias.gui.components.TableCardView;
import com.tobias.server.command.Command;
import com.tobias.server.command.CommandType;
import com.tobias.server.command.CommandWorker;
import com.tobias.server.handlers.AbstractCommandHandler;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

public class UnoController {
    @FXML
    private CardView cardView;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView deck;
    @FXML
    private TableCardView cardsOnTable;
    @FXML
    private VBox leftOpponents;
    @FXML
    private VBox rightOpponents;
    @FXML
    private HBox topOpponents;
    private Map<String,Image> cardImages;
    // Used to
    private Map<Integer, OpponentPlayerView> opponentPlayerViews;
    private CommandWorker worker;


    public void initialize() {
        File imageDir = new File(getClass().getResource("/images/cards").getFile());
        cardImages = new HashMap<>();
        opponentPlayerViews = new HashMap<>();
        for (File f : imageDir.listFiles()) {
            String cardName = f.getName().substring(0,f.getName().indexOf("."));
            Image cardImage = new Image(f.toURI().toString(),200,250,false,false);
            cardImages.put(cardName,cardImage);
        }
        deck.setImage(getCardImageViewByName("CARD_BACK").getImage());
    }

    public void addCardToPlayer(List<Card> cards) {
        int delay = 0;
        for(Card c : cards) {
            Platform.runLater(() -> mainPane.getChildren().add(c.getImage()));
            animateCardToHand(c,delay);
            delay += 300;
        }
    }
    public void addCardToOpponent(int opponentId, int cardCount) {
        int delay = 0;
        for(int i = 0; i < cardCount; i++) {
            animateCardToOpponent(opponentId,delay);
            delay += 300;
        }
    }
    public void clientAddToTable(Card c) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(700), c.getImage());
        Bounds bounds = cardsOnTable.localToParent(cardsOnTable.getBoundsInLocal());
        Bounds cBounds = c.getImage().localToScene(c.getImage().getBoundsInLocal());
        double pos = mainPane.getHeight() - bounds.getMaxY();
        tt.setFromX(c.getImage().getX());
        tt.setFromY(c.getImage().getY());
        tt.setToY(-pos + 20);
        tt.setToX(bounds.getMinX() - cBounds.getMinX());
        tt.setOnFinished((actionEvent -> {
            cardView.getChildren().remove(c.getImage());
            c.getImage().setTranslateX(0);
            c.getImage().setTranslateY(0);
            cardsOnTable.addCard(c.getImage());
        }));
        tt.play();
        worker.process(new Command(CommandType.GAME_CLIENTLAYCARD,c.toString()));
    }

    public void opponentAddCardToTable(int opponentId, Card c) {
        cardView.setCardProperties(c.getImage());
        Platform.runLater(() -> cardsOnTable.addCard(c.getImage()));
    }



    public void addOpponent(OpponentPlayer player) {
        ImageView backCard = getCardImageViewByName("CARD_BACK");
        backCard.setFitWidth(80);
        backCard.setFitHeight(120);
        OpponentPlayerView view = new OpponentPlayerView(backCard,player.getUsername());
        HBox.setMargin(view, new Insets(0, 30, 0, 0));
        if (topOpponents.getChildren().size() == 3) {
            if (leftOpponents.getChildren().size() < 3) {
                view.setRotate(270);
                view.getCard().setRotate(180);
                view.rotateLabels(90);
                Platform.runLater(() -> leftOpponents.getChildren().add(view));
            } else if (rightOpponents.getChildren().size() < 3) {
                view.rotateLabels(270);
                view.setRotate(90);
                Platform.runLater(() -> rightOpponents.getChildren().add(view));
            }
        } else {
            Platform.runLater(() -> topOpponents.getChildren().add(view));
        }
        opponentPlayerViews.put(player.getId(),view);
    }

    public void newWorker(Map<String, AbstractCommandHandler> handlers) {
        worker = new CommandWorker(handlers);
        Thread t = new Thread(worker);
        t.setName("UnoController-" + t.getId());
        t.start();
    }

    public ImageView getCardImageViewByName(String name) {
        if(cardImages.get(name) == null) {
            // Return back card image to avoid NPE
            return new ImageView(cardImages.get("CARD_BACK"));
        }
        return new ImageView(cardImages.get(name));
    }
    public OpponentPlayerView getOpponentPlayerViewById(int id) {
        if(!opponentPlayerViews.containsKey(id)) {
            return null;
        }
        return opponentPlayerViews.get(id);
    }

    private void animateCardToHand(Card card, int delay) {
        Bounds deckBounds = deck.localToScene(deck.getBoundsInLocal());
        // Set configured width / height specified by CardView.
        cardView.setCardProperties(card.getImage());
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(400), card.getImage());
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), card.getImage());
        ParallelTransition parallelTransition = new ParallelTransition(rotateTransition, translateTransition);

        rotateTransition.setFromAngle(-90);
        rotateTransition.setToAngle(0);

        translateTransition.setFromX(deckBounds.getMaxX());
        translateTransition.setFromY(deckBounds.getMinY());

        double pos = mainPane.getHeight() - cardView.getHeight();
        translateTransition.setToX(mainPane.getWidth() / 2);
        translateTransition.setToY(pos);
        parallelTransition.setDelay(Duration.millis(delay));
        parallelTransition.setOnFinished((actionEvent -> {
            cardView.addItem(card);
            card.getImage().setTranslateY(0);
            card.getImage().setTranslateX(0);
        }));
        parallelTransition.play();
    }

    private void animateCardToOpponent(int opponentId, int delay) {

        OpponentPlayerView view = getOpponentPlayerViewById(opponentId);
        Bounds bounds = view.getCard().localToScene(view.getCard().getBoundsInLocal());
        Bounds deckBounds = deck.localToScene(deck.getBoundsInLocal());
        ImageView backCard = getCardImageViewByName("CARD_BACK");
        cardView.setCardProperties(backCard);
        mainPane.getChildren().add(backCard);
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(500), backCard);
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), backCard);
        ParallelTransition parallelTransition = new ParallelTransition(rotateTransition, translateTransition);
        int angle;
        if (leftOpponents.getChildren().contains(view)) {
            angle = -90;
            translateTransition.setToY(bounds.getMinY() - 30);
            translateTransition.setToX(bounds.getMinX());
        } else if (rightOpponents.getChildren().contains(view)) {
            translateTransition.setToY(bounds.getMinY() - 30);
            translateTransition.setToX(bounds.getMinX());
            angle = 90;
        } else {
            translateTransition.setToY(bounds.getMinY());
            translateTransition.setToX(bounds.getMinX());
            angle = 180;
        }
        parallelTransition.setDelay(Duration.millis(delay));
        rotateTransition.setFromAngle(90);
        rotateTransition.setToAngle(angle);
        translateTransition.setFromY(deckBounds.getMinY());
        translateTransition.setFromX(deckBounds.getMaxX());

        parallelTransition.setOnFinished((actionEvent -> mainPane.getChildren().remove(backCard)));
        parallelTransition.play();
    }

    }
