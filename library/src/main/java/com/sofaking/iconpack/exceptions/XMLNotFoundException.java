package com.sofaking.iconpack.exceptions;

import java.io.IOException;

/**
 * Created by nadavfima on 14/05/2017.
 */

public class XMLNotFoundException extends IOException {

    public XMLNotFoundException() {
        super();
    }

    public XMLNotFoundException(String message) {
        super(message);
    }

    public XMLNotFoundException(String filename, Throwable cause) {
        super(filename + " not found", cause);
    }

    public XMLNotFoundException(Throwable cause) {
        super(cause);
    }
}
