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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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
    private ImageView lateUnoButton; // If a client did not say uno, this image will appear. It is different from the Uno button image
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
    @FXML
    private Label messageLabel;
    @FXML
    private Label gameWonLabel;

    private List<String> clientNotificationMessages;
    private List<Node> dynamicPositionComponents;
    private Map<String, Image> cardImages;
    private Map<Integer, OpponentPlayerView> opponentPlayerViews;
    private CommandWorker worker;
    // Used to indicate if we are waiting to receive a card from server
    private boolean waitingForCard;
    // Used to indicate if client has 1 card left
    private boolean isUno;
    private boolean hasPressedUno;
    private boolean isClientsTurn;
    // Used to store how many times a client has drawn card when there are no cards that can be played.
    private int cardsDrawn;
    private final String cardImagePath = "images/cards";

    public UnoController() {
        cardImages = new HashMap<>();
        opponentPlayerViews = new HashMap<>();
        dynamicPositionComponents = new ArrayList<>();
        clientNotificationMessages = new ArrayList<>();
    }


    public void initialize() {

        File imageDir = new File(getClass().getResource("/images/cards").getFile());
        for (File f : imageDir.listFiles()) {
            String cardName = f.getName().substring(0, f.getName().indexOf("."));
            Image cardImage = new Image(f.toURI().toString(), 200, 250, false, false);
            cardImages.put(cardName, cardImage);
        }
        deck.setImage(getCardImageViewByName("CARD_BACK").getImage());
        unoButton.setImage(getCardImageViewByName("UNO_BUTTON").getImage());
        lateUnoButton.setImage(getCardImageViewByName("UNO_LATEBUTTON").getImage());
        unoButton.setVisible(false);
        lateUnoButton.setVisible(false);
        cardView.setCanSelect(false);
        colorPicker.setVisible(false);
        messageLabel.setVisible(false);
        messageLabel.setVisible(false);
        setUnoButtonsEvents();
        colorPicker.setClickEvent();
        colorPicker.setHoverEvent();
        deck.setOnMouseClicked((event) -> {
            if (!clientCanPlay() && !waitingForCard && cardsDrawn != 3) {
                    worker.process(new Command(CommandType.GAME_CLIENTDRAWCARD, "1"));
                    cardsDrawn++;
                    waitingForCard = true;
                    if(cardsDrawn == 3 && !clientCanPlay()) {
                        worker.process(new Command(CommandType.GAME_SKIPTURN));
                    }
            }
        });
        addNodesToDynamicPositioning();
        setMessageLabel();
    }

    // Loads card images from the resource folders as a stream and converts to File objects.
//    private void loadImageResources() throws IOException {
//        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
//        if(jarFile.isFile()) {
//            final JarFile jar = new JarFile(jarFile);
//            final Enumeration<JarEntry> entries = jar.entries();
//            while (entries.hasMoreElements()) {
//                final String name = entries.nextElement().getName();
//                if (name.startsWith(cardImagePath + "/")) { //filter according to the path
//                    File file = new File(name);
//
//                    System.out.println(name + " : " + file.exists());
//                    if(file.isFile()) {
//                        System.out.println("hehe");
//                        String cardName = file.getName().substring(0, file.getName().indexOf("."));
//                        Image cardImage = new Image(file.toURI().toString(), 200, 250, false, false);
//                        cardImages.put(cardName, cardImage);
//                    }
//                }
//            }
//        }
//    }


    private void  setUnoButtonsEvents() {
        unoButton.setOnMouseEntered((event) -> {
            unoButton.setEffect(new DropShadow(70, Color.KHAKI));
        });
        unoButton.setOnMouseExited((event) ->{
            unoButton.setEffect(null);
        });
        unoButton.setOnMouseClicked((event) -> {
            if(isUno) {
                worker.process(new Command(CommandType.GAME_UNO));
                hasPressedUno = true;
                showUnoButton(false);
            }
        });
        lateUnoButton.setOnMouseEntered((event) -> {
            lateUnoButton.setEffect(new DropShadow(70,Color.KHAKI));
        });
        lateUnoButton.setOnMouseExited((event) -> {
            lateUnoButton.setEffect(null);
        });
        lateUnoButton.setOnMouseClicked((event) -> {
            // Send a FORGOTUNO command. That means that an opponent player forgot to press UNO.
            // OPPONENT tag is used to indicate for the command handler that an opponent player forgot to say UNO.
            worker.process(new Command(CommandType.GAME_FORGOTUNO,"OPPONENT"));
            lateUnoButton.setVisible(false);
        });
    }
    public void addCardToPlayer(List<Card> cards) {
        int delay = 0;
        for (Card c : cards) {
            Platform.runLater(() -> mainPane.getChildren().add(c.getImage()));
            animateCardToHand(c, delay);
            delay += 300;
        }
        checkForUno();
    }

    // Loops every 3 seconds to display messages in the clientNotificationMessages list
    private void setMessageLabel() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(() -> {
            if(clientNotificationMessages.size() > 0) {
                messageLabel.setVisible(true);
                Platform.runLater(() -> {
                    messageLabel.setText(clientNotificationMessages.get(0));
                    clientNotificationMessages.remove(0);
                });
            } else {
                messageLabel.setVisible(false);
            }
        },0,5,TimeUnit.SECONDS);
    }

    public void showGameWonLabel(String username) {
        gameWonLabel.setText(username + "WON THE GAME!!!");
        gameWonLabel.setVisible(true);
        cardView.setCanSelect(false);
    }
    public void addClientNotificationMessage(String message) {
        clientNotificationMessages.add(message);
    }

    public void addCardToOpponent(int opponentId, int cardCount) {
        int delay = 0;
        for (int i = 0; i < cardCount; i++) {
            animateCardToOpponent(opponentId, delay);
            delay += 300;
        }
    }

    private void showUnoButton(boolean uno) {
        if(uno) {
            isUno = true;
            unoButton.setVisible(true);
        } else {
            isUno = false;
            unoButton.setVisible(false);
        }
    }

    public void showForgotUnoButton() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.submit(() -> Platform.runLater(() -> lateUnoButton.setVisible(true)));
        ses.schedule(() -> Platform.runLater(()  -> lateUnoButton.setVisible(false)),1500, TimeUnit.MILLISECONDS);
    }

    public boolean isUno() {
        return isUno;
    }

    public boolean hasPressedUno() {
        return hasPressedUno;
    }
    public void setHasPressedUno(boolean hasPressedUno) {
        this.hasPressedUno = hasPressedUno;
    }

    public void setWaitingForCard(boolean waitingForCard) {
        this.waitingForCard = waitingForCard;
    }

    public void setNextColor(CardColor color) {
        Platform.runLater(() -> cardView.setForceColor(color));
    }

    public void setNextPlayerTurn(int id) {
        // If id is -1, it means that it is the clients turn, not an opponent. If the id is anything other than -1 then its an opponent.
        if (id == -1) {
            cardsDrawn = 0;
            isClientsTurn = true;
            cardView.setCanSelect(true);
            opponentPlayerViews.forEach((key, opv) -> opv.getCard().setEffect(null));
            checkForUno();
        } else {
            showUnoButton(false);
            isClientsTurn = false;
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

    private boolean clientCanPlay() {
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
        // Create animation
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
            // Reset values otherwise it will not position correctly on the table.
            card.getImage().setTranslateX(0);
            card.getImage().setTranslateY(0);
            cardsOnTable.addCard(card);
        }));
        // Play animation
        tt.play();

        // If the size is one and the client has not pressed Uno, we must send a GAME_FORGOTUNO command
        if((cardView.getCards().size() == 1 && !hasPressedUno) && !(card.getCardType() == CardType.WILD || card.getCardType() == CardType.WILDDRAWFOUR )) {
            worker.process(new Command(CommandType.GAME_FORGOTUNO));
        }
        // Show colorPicker view so the player can select color of the card.
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
            // Reset hasPressedUno and unoButton
            showUnoButton(false);
            hasPressedUno = false;
        }
    }

    public void deckAddCardToTable(Card card) {
        // Set card width/height
        cardView.setCardProperties(card.getImage());
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(500), card.getImage());
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), card.getImage());
        ParallelTransition parallelTransition = new ParallelTransition(rotateTransition, translateTransition);
        Bounds bounds = cardsOnTable.localToParent(cardsOnTable.getBoundsInLocal());
        Bounds deckBounds = deck.localToParent(deck.getBoundsInLocal());
        translateTransition.setToY(deckBounds.getMinY());
        translateTransition.setToX(bounds.getMinX() + 25);
        rotateTransition.setToAngle(90);
        rotateTransition.setToAngle(0);
        parallelTransition.setOnFinished((event) -> {
            card.getImage().setTranslateY(0);
            card.getImage().setTranslateX(0);
            cardsOnTable.addCard(card);
        });
        parallelTransition.play();
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
                cardsOnTable.addCard(card);
                checkForUno();
            });
            parallelTransition.play();
        }

    }

    // Enables the UNO button if the requirements are hit
    private void checkForUno() {
        if(isClientsTurn && (cardView.getCards().size() == 2) && clientCanPlay()) {
            showUnoButton(true);
        } else {
            showUnoButton(false);
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
        cardView.getCards().add(card);
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

    // Method that adds all nodes that are to be moved according to screen size to
    // the dynamicPositionComponents list. All nodes in that list will be dynamically positioned according to
    // the nodes' userData which should be the percentage of the width/height that the node should have.
    private void addNodesToDynamicPositioning() {
        dynamicPositionComponents.add(cardsOnTable);
        dynamicPositionComponents.add(deck);
        dynamicPositionComponents.add(colorPicker);
        dynamicPositionComponents.add(unoButton);
        dynamicPositionComponents.add(lateUnoButton);
        dynamicPositionComponents.add(messageLabel);
    }

    private int getNodeYUserData(Node node) {
        return Integer.parseInt(node.getUserData().toString().split(":")[0]);
    }
    private int getNodeXUserData(Node node) {
        return Integer.parseInt(node.getUserData().toString().split(":")[1]);
    }

    public void adjustComponentXPosition(double stageWidth) {
        for(Node node : dynamicPositionComponents) {
                double anchorPercentage = getNodeXUserData(node) * stageWidth / 100;
                AnchorPane.setLeftAnchor(node, anchorPercentage);

        }
    }

    public void adjustComponentYPosition(double stageHeight) {
        for(Node node : dynamicPositionComponents) {
            double anchorPercentage = getNodeYUserData(node) * stageHeight / 100;
            AnchorPane.setTopAnchor(node, anchorPercentage);
        }
    }
}
