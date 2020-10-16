package com.tobias.game;


public class Player {

    private int id;
    private String username;

    Player(int id) {
        this.id = id;
    }

    public Player(int id, String username){
        this(id);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }
}
