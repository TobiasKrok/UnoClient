package com.tobias.server.handlers;

import com.tobias.server.command.Command;

public interface CommandHandler {

    void process(Command command);
}
