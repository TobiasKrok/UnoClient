package com.tobias.gui;

import com.tobias.game.OpponentPlayer;
import com.tobias.game.card.Card;
import com.tobias.game.card.CardColor;
import com.tobias.game.card.CardType;
import com.tobias.gui.components.CardColorPicker;
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
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UnoController {
    @FXML
    private CardView cardView;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView deck;
    @FXML
    private ImageView unoButton;
    @FXML
    private TableCardView cardsOnTable;
    @FXML
    private VBox leftOpponents;
    @FXML
    private VBox rightOpponents;
    @FXML
    private HBox topOpponents;
    @FXML
    private CardColorPicker colorPicker;

    private Map<String, Image> cardImages;
    private Map<Integer, OpponentPlayerView> opponentPlayerViews;
    private CommandWorker worker;
    // Used to indicate if we are waiting to receive a card from server
    private boolean waitingForCard;
    // Used to indicate if the client has pressed uno when 1 card is left.
    private boolean hasPressedUno;


    public void initialize() {
        File imageDir = new File(getClass().getResource("/images/cards").getFile());
        cardImages = new HashMap<>();
        opponentPlayerViews = new HashMap<>();
        for (File f : imageDir.listFiles()) {
            String cardName = f.getName().substring(0, f.getName().indexOf("."));
            Image cardImage = new Image(f.toURI().toString(), 200, 250, false, false);
            cardImages.put(cardName, cardImage);
        }
        deck.setImage(getCardImageViewByName("CARD_BACK").getImage());
        colorPicker.setVisible(false);
        colorPicker.setClickEvent();
        colorPicker.setHoverEvent();
        deck.setOnMouseClicked((event) -> {
            if (!clientCanDraw() && !waitingForCard) {
                worker.process(new Command(CommandType.GAME_CLIENTDRAWCARD, "1"));
                waitingForCard = true;
            }
        });
    }

    public void addCardToPlayer(List<Card> cards) {
        int delay = 0;
        for (Card c : cards) {
            Platform.runLater(() -> mainPane.getChildren().add(c.getImage()));
            animateCardToHand(c, delay);
            delay += 300;
        }
    }

    public void addCardToOpponent(int opponentId, int cardCount) {
        int delay = 0;
        for (int i = 0; i < cardCount; i++) {
            animateCardToOpponent(opponentId, delay);
            delay += 300;
        }
    }

    public void setUno(boolean setUno) {

    }
    public void setWaitingForCard(boolean waitingForCard) {
        this.waitingForCard = waitingForCard;
    }

    public void setNextColor(CardColor color) {
        Platform.runLater(() -> cardView.setForceColor(color));
    }

    public void setNextPlayerTurn(int id) {
        // If id is -1, it means that it is the clients turn, not an opponent. If the id is anything other than -1 then its a opponent.
        if (id == -1) {
            cardView.setCanSelect(true);
            opponentPlayerViews.forEach((key, opv) -> opv.getCard().setEffect(null));
        } else {
            DropShadow ds = new DropShadow();
            ds.setColor(Color.KHAKI);
            ds.setHeight(70);
            ds.setWidth(70);
            opponentPlayerViews.get(id).getCard().setEffect(ds);
            cardView.setCanSelect(false);
        }
    }

    public void clientSetColor(String color) {
        worker.process(new Command(CommandType.GAME_CLIENTSETCOLOR, color));
        colorPicker.setVisible(false);
    }

    private boolean clientCanDraw() {
        for (Card card : cardView.getCards()) {
            if (isCardAllowed(card)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCardAllowed(Card card) {
        Card topCard = cardsOnTable.getTopCard();
        if (topCard == null) {
            return !(card.getCardType() == CardType.WILDDRAWFOUR || card.getCardType() == CardType.WILD);
            // If CardColor is not equal to NONE, it means that the next card must be a specific color because a WILD card has been used.
        } else if (cardView.getForceColor() != CardColor.NONE) {
            if (card.getCardColor() == cardView.getForceColor()) {
                return true;
            }
            return (card.getCardType() == CardType.WILD) || (card.getCardType() == CardType.WILDDRAWFOUR);

        } else {
            return (card.getCardColor() == topCard.getCardColor()) || (card.getValue() == topCard.getValue())
            || (card.getCardType() == CardType.WILDDRAWFOUR) || (card.getCardType() == CardType.WILD);
        }
    }


    public void clientAddToTable(Card card) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(700), card.getImage());
        Bounds bounds = cardsOnTable.localToParent(cardsOnTable.getBoundsInLocal());
        Bounds cBounds = card.getImage().localToScene(card.getImage().getBoundsInLocal());
        double pos = mainPane.getHeight() - bounds.getMaxY();
        tt.setFromX(card.getImage().getX());
        tt.setFromY(card.getImage().getY());
        tt.setToY(-pos);
        tt.setToX(bounds.getMinX() - cBounds.getMinX());
        tt.setOnFinished((actionEvent -> {
            cardView.getChildren().remove(card.getImage());
            card.getImage().setTranslateX(0);
            card.getImage().setTranslateY(0);
            cardsOnTable.addCard(card);
        }));
        tt.play();
//        if (card.getCardType() != CardType.REVERSE && card.getCardType() != CardType.SKIP) {
//            setNextPlayerTurn(-1);
//        }
        if (card.getCardType() == CardType.WILD || card.getCardType() == CardType.WILDDRAWFOUR) {
            colorPicker.setCard(card);
            colorPicker.setWorker(worker);
            colorPicker.setVisible(true);
            // Disable cardView so player cannot lay cards while the colorPicker prompt is being shown
            cardView.setCanSelect(false);
        } else {
            // Reset color for all clients
            worker.process(new Command(CommandType.GAME_CLIENTSETCOLOR, String.valueOf(CardColor.NONE)));
            // Send card to server
            worker.process(new Command(CommandType.GAME_CLIENTLAYCARD, card.toString()));
        }
    }


    public void opponentAddCardToTable(int opponentId, Card card) {
        OpponentPlayerView view = getOpponentPlayerViewById(opponentId);
        if (!(view == null)) {
            cardView.setCardProperties(card.getImage());
            Platform.runLater(() -> mainPane.getChildren().add(card.getImage()));
            Bounds bounds = view.getCard().localToScene(view.getCard().getBoundsInLocal());
            Bounds cardsOnTableBounds = cardsOnTable.localToScene(cardsOnTable.getBoundsInLocal());
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(500), card.getImage());
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), card.getImage());
            ParallelTransition parallelTransition = new ParallelTransition(rotateTransition, translateTransition);
            int angle = getAngleForOpponentView(view);
            translateTransition.setFromY(bounds.getMaxY());
            translateTransition.setFromX(bounds.getMinX());
            translateTransition.setToY(cardsOnTableBounds.getMinY() + 20);
            translateTransition.setToX(cardsOnTableBounds.getMinX() + 50);
            rotateTransition.setFromAngle(angle);
            rotateTransition.setToAngle(0);
            parallelTransition.setOnFinished((event) -> {
                Platform.runLater(() -> mainPane.getChildren().remove(card.getImage()));
                Platform.runLater(() -> cardsOnTable.addCard(card));
            });
            parallelTransition.play();
        }

    }


    public synchronized void addOpponent(OpponentPlayer player) {
        ImageView backCard = getCardImageViewByName("CARD_BACK");
        backCard.setFitWidth(80);
        backCard.setFitHeight(120);
        OpponentPlayerView view = new OpponentPlayerView(backCard, player.getUsername());
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
        opponentPlayerViews.put(player.getId(), view);
    }

    public void newWorker(Map<String, AbstractCommandHandler> handlers) {
        worker = new CommandWorker(handlers);
        Thread t = new Thread(worker);
        t.setName("UnoController-" + t.getId());
        t.start();
    }

    public ImageView getCardImageViewByName(String name) {
        if (cardImages.get(name) == null) {
            // Return back card image to avoid NPE
            return new ImageView(cardImages.get("CARD_BACK"));
        }
        return new ImageView(cardImages.get(name));
    }

    public OpponentPlayerView getOpponentPlayerViewById(int id) {
        if (!opponentPlayerViews.containsKey(id)) {
            return null;
        }
        return opponentPlayerViews.get(id);
    }

    private int getAngleForOpponentView(OpponentPlayerView view) {
        int angle;
        if (leftOpponents.getChildren().contains(view)) {
            angle = -90;
        } else if (rightOpponents.getChildren().contains(view)) {
            angle = 90;
        } else {
            angle = 180;
        }
        return angle;
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
        int angle = getAngleForOpponentView(view);
        if (topOpponents.getChildren().contains(view)) {
            translateTransition.setToY(bounds.getMinY());
        } else {
            translateTransition.setToY(bounds.getMinY() - 30);
        }
        translateTransition.setToX(bounds.getMinX());
        parallelTransition.setDelay(Duration.millis(delay));
        rotateTransition.setFromAngle(90);
        rotateTransition.setToAngle(angle);
        translateTransition.setFromY(deckBounds.getMinY());
        translateTransition.setFromX(deckBounds.getMaxX());

        parallelTransition.setOnFinished((actionEvent -> mainPane.getChildren().remove(backCard)));
        parallelTransition.play();
    }

    public void adjustComponentWidth(double stageWidth) {
        if (stageWidth < 1000) {
            AnchorPane.setLeftAnchor(cardsOnTable, stageWidth / 2 + 20);
            AnchorPane.setLeftAnchor(colorPicker, stageWidth / 2 + 20);
            AnchorPane.setLeftAnchor(deck, stageWidth / 4 + 40);
        } else {
            AnchorPane.setLeftAnchor(cardsOnTable, stageWidth / 2 + 100);
            AnchorPane.setLeftAnchor(colorPicker, stageWidth / 2 + 100);
            AnchorPane.setLeftAnchor(deck, stageWidth / 4 + 100);
        }
    }

    public void adjustComponentHeight(double stageHeight) {
        if (stageHeight > 900) {
            AnchorPane.setTopAnchor(colorPicker, 400d);
            AnchorPane.setTopAnchor(cardsOnTable, 400d);
            AnchorPane.setTopAnchor(deck, 400d);
        } else {
            AnchorPane.setTopAnchor(deck, 200d);
            AnchorPane.setTopAnchor(colorPicker, 200d);
            AnchorPane.setTopAnchor(cardsOnTable, 200d);
        }
    }
}
