package com.tobias.server.command;



import com.tobias.server.handlers.CommandHandler;

import java.util.LinkedList;
import java.util.Map;

public class CommandWorker implements Runnable{


    private Map<String, CommandHandler> handlers;
    private LinkedList<String> queue;
    public CommandWorker(Map<String,CommandHandler> handlers ){
        this.queue = new LinkedList<>();
        this.handlers = handlers;
    }


    public void run(){
        while (true) {

            synchronized (queue) {
                while (queue.isEmpty()){
                    try {
                        queue.wait();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Command c = parseCommand(queue.get(0));
            if(!(c.getType() == CommandType.WORKER_UNKNOWNCOMMAND)) {
                getHandlerForCommand(c).process(c);
            }
            queue.remove(0);
        }
    }

    public void processCommand(String command) {
        synchronized (queue) {
            queue.add(command);
            queue.notify();
        }
    }

    private Command parseCommand(String command) {
        // Set the commandType to an unknown commandType. If we can't convert the passed commandType to a CommandType, WORKER_UNKNOWNCOMMAND will be returned.
        CommandType cmdType = CommandType.WORKER_UNKNOWNCOMMAND;
        String data = "";
        try {
           cmdType = CommandType.valueOf(command.substring(command.indexOf("TYPE:") + 5,command.indexOf("DATA:") - 1));
           data = command.substring(command.indexOf("DATA:") + 5);
        } catch (IllegalArgumentException e){
            System.out.println("Could not bind Type: " + (command.substring(command.indexOf("TYPE:") + 5,command.indexOf("DATA:"))) + " to a CommandType");
        }
        return new Command(cmdType,data);
    }

    private CommandHandler getHandlerForCommand(Command command){
        String prefix = command.getType().name().substring(0,command.getType().name().indexOf("_"));
        return handlers.get(prefix);
    }
}
