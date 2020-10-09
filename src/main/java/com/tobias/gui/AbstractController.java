package com.tobias.gui;

import com.tobias.server.command.CommandWorker;
import com.tobias.server.handlers.CommandHandler;

import java.util.Map;

public abstract class AbstractController {

    private CommandWorker worker;

    public void newWorker(Map<String, CommandHandler> handlers) {
        worker = new CommandWorker(handlers);
        Thread t = new Thread(worker);
        t.setName("UnoController-" + t.getId());
        t.start();
    }

}
