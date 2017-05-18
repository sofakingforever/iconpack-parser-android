package com.sofaking.iconpack.exceptions;

/**
 * Created by nadavfima on 18/05/2017.
 */

public class IconMaskingUnavailableException extends RuntimeException {

    public IconMaskingUnavailableException() {
        super("maskFallback is true while the mIconMasking is null (Did you set it to true in initAppFilter()?)");
    }
}
