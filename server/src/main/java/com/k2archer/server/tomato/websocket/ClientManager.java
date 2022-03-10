package com.k2archer.server.tomato.websocket;

import javax.crypto.Cipher;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class ClientManager {

    private static ClientManager instance;
    private Map<Session, String> clients;

    private ClientManager() {
        clients = new HashMap<>();
    }

    public static ClientManager getInstance() {
        if (instance == null) {
            synchronized (ClientManager.class) {
                if (instance == null) {
                    instance = new ClientManager();
                }
            }
        }
        return instance;
    }


    public void addClient( Session session, String token) {
        clients.put(session, token);
    }

//    public void removeClient(String token) {
//        clients.remove(token);
//    }

    public void removeClient(Session session) {
        clients.remove(session);
    }

    public String getToken(Session session) {
        return clients.get(session);
    }
}
