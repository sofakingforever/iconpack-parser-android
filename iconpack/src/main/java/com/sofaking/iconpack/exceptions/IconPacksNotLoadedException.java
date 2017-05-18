package com.sofaking.iconpack.exceptions;

/**
 * Created by nadavfima on 18/05/2017.
 */

public class IconPacksNotLoadedException extends RuntimeException {

    public IconPacksNotLoadedException() {
        super("You must call loadInstalledIconPacksAsync() first");
    }
}
