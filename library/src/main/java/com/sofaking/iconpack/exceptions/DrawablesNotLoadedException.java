package com.sofaking.iconpack.exceptions;

/**
 * Created by nadavfima on 18/05/2017.
 */

public class DrawablesNotLoadedException extends RuntimeException {

    public DrawablesNotLoadedException() {
        super("Drawables XML not Loaded");
    }
}
