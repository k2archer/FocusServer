package com.k2archer.server.tomato.websocket.controller;

import com.google.gson.internal.$Gson$Preconditions;
import com.k2archer.server.tomato.TomatoApplication;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.java_websocket.client.WebSocketClient;
import org.omg.CORBA.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;


@SpringBootTest(classes = {TomatoApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TickingControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wc;

    @BeforeEach
    public void beforeSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wc).build();
    }


    CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    void testStartTicking() throws MalformedURLException {

        URI url = null;
        try {
            String port = wc.getEnvironment().getProperty("local.server.port");

            url = new URI("ws://localhost:" + port + "/websocket/token11");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        WebSocketClient webSocketClient = new WebSocketClient(url) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {

                countDownLatch.countDown();
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

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        WebSocketClient webSocketClient = new WebSocetC
    }

    @Test
    void startTicking() {
    }

    @Test
    void finishTicking() {


    }
}