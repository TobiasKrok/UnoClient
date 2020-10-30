package com.tobias.server.command;

public class Command {

    private CommandType type;
    private String data;

    public Command(CommandType type) {
        this(type,"");
    }
    public Command(CommandType type, int id) {
        this(type,String.valueOf(id));
    }
    public Command(CommandType type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getData(){
        return this.data;
    }

    public CommandType getType() {
        return type;
    }

    public String toString() {
        return "TYPE:" + type + " DATA:" + data;
    }
}
