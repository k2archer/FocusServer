package com.k2archer.server.tomato.websocket;

import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.k2archer.common.ResponseStateCode;
import com.k2archer.server.tomato.service.UserService;
import com.k2archer.server.tomato.websocket.controller.TickingController;
import com.k2archer.server.tomato.bean.dto.User;
import com.k2archer.common.utils.GsonResponseParse;
import com.k2archer.common.bo.TickingInfo;
import com.k2archer.common.web_socket.WebSocketMessage;
import com.k2archer.common.web_socket.WebSocketResponse;
import com.k2archer.common.web_socket.response_action.TickingAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ServerEndpoint(value = "/websocket/{token}", configurator = CustomSpringConfigurator.class)
public class WebSocketServer {

    private final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);


    private Map<Long, Semaphore> setMap = new HashMap<>();
    private Map<Long, CopyOnWriteArraySet<Session>> deviceMap = new HashMap<>();

    @Autowired
    UserService userService;

    //
    //连接时执行
    @OnOpen
    public void onOpen(@PathParam("token") String token, Session session) throws IOException {

        logger.info("新连接：{}", token);
        logger.info("session_id: " + session.getId());

        User user = userService.getUserByToken(token);
        if (user == null) {
            WebSocketResponse response = new WebSocketResponse();
            response.setCode(ResponseStateCode.BAD_TOKEN);
            response.setMsg("Token 无效");
            session.getBasicRemote().sendText(new Gson().toJson(response));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            session.close();
            return;
        }


        if (!deviceMap.containsKey(user.getId())) {
            setMap.put(user.getId(), new Semaphore(1));
            deviceMap.put(user.getId(), new CopyOnWriteArraySet<>());
        }
        try {
            setMap.get(user.getId()).acquire();
            deviceMap.get(user.getId()).add(session);
            setMap.get(user.getId()).release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ClientManager.getInstance().addClient(session, token);
    }

    //    //关闭时执行
    @OnClose
    public void onClose(Session session) {

        System.out.println("连接：关闭 session_id: " + session.getId());

        String token = ClientManager.getInstance().getToken(session);
        User user = userService.getUserByToken(token);
        if (user == null) {
            return;
        }
//        deviceMap.get(user.getId()).remove(session);
        try {
            setMap.get(user.getId()).acquire();
            deviceMap.get(user.getId()).remove(session);
            System.out.println("---------------------------" + deviceMap.get(user.getId()).size());
            setMap.get(user.getId()).release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ClientManager.getInstance().removeClient(session);
    }

    @Autowired
    private TickingController tickingController;

    //
    //收到消息时执行
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
//        logger.debug("收到用户{}的消息{}", this.token, message);
        System.out.println("收到： " + message);

        if (!session.isOpen()) {
            return;
        }

        String token = ClientManager.getInstance().getToken(session);
        User user = userService.getUserByToken(token);
        if (user == null) {
            session.close();
            return;
        }

        String response = "";
        WebSocketMessage wsMessage = null;
        try {
            wsMessage = new Gson().fromJson(message, WebSocketMessage.class);
//            TickingController tickingController = new TickingController();
            if (wsMessage.action.equals(WebSocketMessage.MessageAction.TICKING)) {
                GsonResponseParse<TickingInfo> parse = new GsonResponseParse<TickingInfo>() {
                };
                TickingInfo tickingInfo = parse.deal(message);
                handleTickingMessage(user, tickingInfo, session);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();

            WebSocketResponse body = new WebSocketResponse(ResponseStateCode.FAILURE,
                    message + " 消息解析错误 " + e.getMessage(), "", "");
            body.setMsg(e.toString() + " at " + e.getStackTrace()[0].getClassName() + e.getStackTrace()[0].getMethodName() + "("
                    + e.getStackTrace()[0].getFileName() + ":" + e.getStackTrace()[0].getLineNumber() + ")");
            response = new Gson().toJson(body);
        }

//        session.getAsyncRemote().sendText(response); // 异步
        synchronized (WebSocketServer.class) {
            session.getBasicRemote().sendText(response);
        }
    }

    public void test_async() {
    }

    private void handleTickingMessage(User user, TickingInfo tickingInfo, Session session) throws IOException {
        WebSocketResponse response = null;
        if (tickingInfo.getAction().equals(TickingAction.START_TICKING)) {
            response = tickingController.startTicking(user, tickingInfo);
        } else if (tickingInfo.getAction().equals(TickingAction.FINISH_TICKING)) {
            response = tickingController.finishTicking(user, tickingInfo);
        } else if (tickingInfo.getAction().equals(TickingAction.CANCEL_TICKING)) {
            response = tickingController.cancelTicking(user, tickingInfo);
        }

        if (response.getCode() == ResponseStateCode.FAILURE.getCode()) {
            synchronized (WebSocketServer.class) {
                session.getBasicRemote().sendText(new Gson().toJson(response));
            }
        } else {
            // 获取同一用户下的全部在线客户端，并发送广播
            broadcastToUserDevice(user.getId(), new Gson().toJson(response));
        }
    }

    private void broadcastToUserDevice(long userId, String response) {
        Set<Session> deviceSessions = deviceMap.get(userId);
        StringBuilder logcat_msg = new StringBuilder();
        for (Session s : deviceSessions) {
            if (s == null || !s.isOpen()) {
                continue;
            }
            //  向单个用户的在线终端 广播消息
            try {
                synchronized (WebSocketServer.class) {
                    s.getBasicRemote().sendText(response);
                }
                logcat_msg.append(s.getId()).append(" ");
            } catch (IOException e) {
                try {
                    s.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        logger.info("boradcast: " + response + " to user:" + userId + " dev: " + logcat_msg.toString());
    }

    //
    //连接错误时执行
    @OnError
    public void onError(Session session, Throwable error) {
        String token = ClientManager.getInstance().getToken(session);
        logger.info("用户id为：{}的连接发送错误", token);
        System.out.println("连接发送错误 " + session.getId());
        logger.info(error.getMessage());
        error.printStackTrace();

        if (!session.isOpen()) {
            System.out.println("连接已关闭 ");
            return;
        }

        if (error instanceof EOFException) {
//            error.printStackTrace();
        }

    }

}