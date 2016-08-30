package com.sam_chordas.android.stockhawk.event;

/**
 * Created by arun on 26/8/16.
 */

public class MessageEvent {
    private String message;
    public static final String SYMBOL_NOT_FOUND_MSG = "symbol not found";

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
