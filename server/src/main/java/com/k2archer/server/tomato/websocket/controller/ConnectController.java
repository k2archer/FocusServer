package com.k2archer.server.tomato.websocket.controller;

import com.k2archer.server.tomato.bean.dto.User;

public class ConnectController {

    public User getUser(String token) {

        if (token.equals("token")) {
            User u = new User();
            u.setId(1);
            u.setName("kwei");
            return u;
        } else {
            try {
                throw new Exception("not implementation");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

//        try {
//            throw new Exception("not implementation");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
    }
}
