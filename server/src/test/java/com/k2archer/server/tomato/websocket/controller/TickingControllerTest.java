package com.k2archer.server.tomato.websocket.controller;

import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TickingControllerTest {

    @Test
    void testStartTicking() throws MalformedURLException {

        URI url = null;
        try {
            url = new URI("ws://localhost:8080/websocket/token11");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        WebSocketClient webSocketClient = new WebSocketClient(url) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {


            }

            @Override
            public void onMessage(String message) {

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {

            }

            @Override
            public void onError(Exception ex) {

            }
        };
        webSocketClient.connect();

//        WebSocketClient webSocketClient = new WebSocetC
    }

    @Test
    void startTicking() {
    }

    @Test
    void finishTicking() {


    }
}