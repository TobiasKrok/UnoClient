package com.tobias.gui;

import com.tobias.game.card.Card;
import com.tobias.gui.components.CardView;
import com.tobias.gui.components.TableCardView;
import com.tobias.server.command.CommandWorker;
import com.tobias.server.handlers.AbstractCommandHandler;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private TableCardView cardsOnTable;
    @FXML
    private VBox leftOpponents;
    @FXML
    private VBox rightOpponents;
    @FXML
    private HBox topOpponents;
    private Map<String,Image> cardImages;
    private CommandWorker worker;
    private boolean cardAnimationPlaying;

    public void initialize() {
        File imageDir = new File(getClass().getResource("/images/cards").getFile());
        cardImages = new HashMap<>();
        for (File f : imageDir.listFiles()) {
            String cardName = f.getName().substring(0,f.getName().indexOf("."));
            Image cardImage = new Image(f.toURI().toString(),200,250,false,false);
            cardImages.put(cardName,cardImage);
        }
        deck.setImage(cardImages.get("CARD_BACK"));
    }

    public void addCardToPlayer(List<Card> cards) {
        int delay = 0;
        for(Card c : cards) {
            Platform.runLater(() -> mainPane.getChildren().add(c.getImage()));
            animateCardToHand(c,delay);
            delay += 300;
        }
    }

    public void newWorker(Map<String, AbstractCommandHandler> handlers) {
        worker = new CommandWorker(handlers);
        Thread t = new Thread(worker);
        t.setName("UnoController-" + t.getId());
        t.start();
    }

    public ImageView getCardImageByName(String name) {
        if(cardImages.get(name) == null) {
            // Return back card image to avoid NPE
            return new ImageView(cardImages.get("CARD_BACK"));
        }
        return new ImageView(cardImages.get(name));
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
}
