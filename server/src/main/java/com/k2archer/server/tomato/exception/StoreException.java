package com.k2archer.server.tomato.exception;

import lombok.Getter;
import lombok.Setter;

public class StoreException extends Exception {

    @Getter @Setter private String data;

    /**
     *
     */
    private static final long serialVersionUID = -7433867455009571185L;

    public StoreException(String message) {
        super(message);
    }

    public StoreException(String message, String data) {
        super(message);
        this.data = data;
    }
}
