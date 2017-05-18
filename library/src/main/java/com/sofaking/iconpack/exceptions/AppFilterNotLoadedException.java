package com.sofaking.iconpack.exceptions;

/**
 * Created by nadavfima on 18/05/2017.
 */

public class AppFilterNotLoadedException extends RuntimeException {

    public AppFilterNotLoadedException() {
        super("App Filter XML not Loaded");
    }
}
