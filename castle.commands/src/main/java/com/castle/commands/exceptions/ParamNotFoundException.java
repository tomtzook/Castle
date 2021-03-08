package com.castle.commands.exceptions;

public class ParamNotFoundException extends CommandException {

    public ParamNotFoundException(String key) {
        super(key);
    }
}
