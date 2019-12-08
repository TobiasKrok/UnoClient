package com.tobias.gui.components;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Random;

public class TableCardView extends StackPane {

    public void addCard(ImageView v) {

        Random r = new Random();
        v.setRotate(r.nextInt(30 + 10 ) - 30);
        getChildren().add(v);
        if(getChildren().size() > 3) {
            getChildren().remove(0);
        }
    }

}
