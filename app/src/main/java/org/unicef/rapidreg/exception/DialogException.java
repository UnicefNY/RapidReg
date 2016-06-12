package org.unicef.rapidreg.exception;

public class DialogException extends Exception {

    public DialogException(String detailMessage) {
        super(detailMessage);
    }

    public DialogException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
