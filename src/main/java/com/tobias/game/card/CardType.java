package com.tobias.game.card;

public enum CardType {
    NORMAL(0),
    SKIP(11),
    REVERSE(12),
    DRAWTWO(13),
    WILD(14),
    WILDDRAWFOUR(15);

    private final int value;

    CardType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}