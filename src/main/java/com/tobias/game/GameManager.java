package com.tobias.game;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    List<Integer> opponentIds;

    public GameManager () {
        this.opponentIds = new ArrayList<>();
    }

    public void addOpponentId(int id) {
        if(!opponentIds.contains(id)) {
            opponentIds.add(id);
        }
    }
}
