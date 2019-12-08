package com.tobias.gui;

import com.tobias.game.card.Card;
import com.tobias.gui.components.CardView;
import com.tobias.gui.components.TableCardView;
import com.tobias.server.command.CommandWorker;
import com.tobias.server.handlers.AbstractCommandHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UnoController {
    @FXML
    private CardView playerHand;
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

    public void addCardToPlayer(Card c) {

    }

    public void newWorker(Map<String, AbstractCommandHandler> handlers) {
        worker = new CommandWorker(handlers);
        Thread t = new Thread(worker);
        t.setName("UnoController-" + t.getId());
        t.start();
    }

    public Image getCardImageByName(String name) {
        if(cardImages.get(name) == null) {
            // Return back card image to avoid null
            return cardImages.get("CARD_BACK");
        }
        return cardImages.get(name);
    }
}
