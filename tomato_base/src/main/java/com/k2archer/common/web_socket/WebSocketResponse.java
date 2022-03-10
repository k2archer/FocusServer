package com.k2archer.common.web_socket;

import com.k2archer.common.ResponseStateCode;

public class WebSocketResponse<T> {

    private int code;
    private String msg;
    private String action;
    private T data;


    public class Action {
        public static final String TICKING = "ticking";
    }

    public WebSocketResponse() {
    }

    public WebSocketResponse(ResponseStateCode code, String message, String action, T data) {
        this.code = code.getCode();
        this.msg = message;
        this.action = action;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setCode(ResponseStateCode code) {
        this.code = code.getCode();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
