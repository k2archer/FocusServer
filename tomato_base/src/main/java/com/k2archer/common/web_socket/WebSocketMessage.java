package com.k2archer.common.web_socket;

public class WebSocketMessage<T> {
    public String action;

    private T data;


    public class MessageAction {
        public static final String TICKING = "ticking";
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
