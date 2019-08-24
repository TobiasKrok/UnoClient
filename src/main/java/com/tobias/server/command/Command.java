package com.tobias.server.command;

public class Command {

    private String command;
    private int id;
    private CommandType type;
    private String data;

    public Command(int id, CommandType type, String data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }


    public int getId() {
        return id;
    }
    public String getData(){
        return this.data;
    }

    public CommandType getType() {
        return type;
    }
}
