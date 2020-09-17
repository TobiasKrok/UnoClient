package com.tobias.gui.components;

import com.tobias.Main;
import com.tobias.game.card.Card;
import com.tobias.server.command.Command;
import com.tobias.server.command.CommandType;
import com.tobias.server.command.CommandWorker;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class CardColorPicker extends GridPane {


    private Card card;
    // We need a worker here to be able to send the card to client after the user has selected a color
    private CommandWorker worker;


    public void setHoverEvent() {
        for (Node n : getChildren()) {
            n.setOnMouseEntered((event) -> {
                n.setEffect(new DropShadow(70, Color.BLACK));
            });
            n.setOnMouseExited((event) -> {
                n.setEffect(null);
            });
        }
    }

    public void setCard(Card card) {
        this.card = card;
    }
    public void setWorker(CommandWorker worker) {
        this.worker = worker;
    }

    public void setClickEvent(){
        for(Node n : getChildren()) {
            n.setOnMouseClicked((event) -> {
                Main.getUnoController().clientSetColor(n.getUserData().toString());
                worker.process(new Command(CommandType.GAME_CLIENTLAYCARD,card.toString()));
                if(Main.getUnoController().isUno() && !Main.getUnoController().hasPressedUno()) {
                    worker.process(new Command(CommandType.GAME_FORGOTUNO));
                } else {
                    // Reset hasPressedUno
                    Main.getUnoController().setHasPressedUno(false);
                }
            });
        }
    }

}