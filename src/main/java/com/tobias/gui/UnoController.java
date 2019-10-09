package com.tobias.gui;

import com.tobias.game.Player;
import com.tobias.game.card.Card;
import com.tobias.server.command.Command;
import com.tobias.server.command.CommandType;
import com.tobias.server.command.CommandWorker;
import com.tobias.server.handlers.AbstractCommandHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.Map;

public class UnoController {
    @FXML
    private Button drawButton;
    @FXML
    private Button layCardButton;
    @FXML
    private Label deckCountLabel;
    @FXML
    private Label topCardLabel;
    @FXML
    private ListView cardList;

    private CommandWorker worker;

    public void initialize() {

    }

    public void setDeckCount(int count) {
        Platform.runLater(() -> {
            deckCountLabel.setText(String.valueOf(count));
        });
    }
    public void setTopCard(Card c) {
        Platform.runLater(() -> {
            topCardLabel.setText(c.toString());
        });
    }

    public void onDrawButtonClick() {
        worker.process(new Command(CommandType.GAME_REQUESTCARD,"94"));
    }
    public void newWorker(Map<String, AbstractCommandHandler> handlers) {
        worker = new CommandWorker(handlers);
        Thread t = new Thread(worker);
        t.setName("UnoController-" + t.getId());
        t.start();
    }

}