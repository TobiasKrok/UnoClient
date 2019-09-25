package com.tobias.server.handlers;

import com.tobias.server.command.Command;

public abstract class AbstractCommandHandler {

    public abstract void process(Command command);
}
