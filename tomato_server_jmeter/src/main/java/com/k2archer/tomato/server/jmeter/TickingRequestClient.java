package com.k2archer.tomato.server.jmeter;

import com.google.gson.Gson;
import com.k2archer.common.BaseResponseBody;
import com.k2archer.common.ResponseStateCode;
import com.k2archer.common.bo.TickingInfo;
import com.k2archer.common.utils.GsonResponseParse;
import com.k2archer.common.web_socket.WebSocketMessage;
import com.k2archer.common.web_socket.WebSocketResponse;
import com.k2archer.common.web_socket.response_action.TickingAction;
import com.k2archer.tomato.server.jmeter.login_valid.LoginValid;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TickingRequestClient extends AbstractJavaSamplerClient {

    @Override
    public Arguments getDefaultParameters() {

        Arguments args = new Arguments();
        args.addArgument("path", "http://localhost:8080/api/user/login");
        args.addArgument("Content-Type", "application/json");
        return args;
    }


    @Override
    public void setupTest(JavaSamplerContext context) {
    }

    private BaseResponseBody handleRequest(Arguments parameters, SampleResult result) {

        String path = parameters.getArgumentsAsMap().get("path");
        String body = parameters.getArgumentsAsMap().get("body");

        result.sampleStart();
        String response = HttpConnection.doPost(path, body, result);
        result.sampleEnd();
        System.out.println(response);

        if (response == null) {
            return null;
        }

        GsonResponseParse<Object> parse = new GsonResponseParse<Object>() {
        };
        BaseResponseBody responseBody = null;
        try {
            responseBody = new Gson().fromJson(response, parse);
        } catch (Exception e) {
        }
        if (responseBody == null) {
            result.setSuccessful(false);
            result.setResponseData(response, "UTF-8");
            return null;
        } else {
            return responseBody;
        }
    }

    /**
     * 每个线程执行多次,相当于loadrunner的Action()方法 (non-Javadoc)
     */
    public SampleResult runTest(JavaSamplerContext arg0) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SampleResult result = new SampleResult();
        result.setContentType("application/json");

        // 正常登录
        Arguments parameters = DefaultArguments.getArguments();
        BaseResponseBody<Map<String, String>> responseBody = handleRequest(parameters, result);
        if (responseBody != null) {
            if (LoginValid.loginByAccurateInfo(responseBody, result) != null) {
                return result;
            }
        }
        if (!result.isSuccessful()) {
            return result;
        }

        String token = responseBody.getData().get("token");

        try {
            WebSocketClient client = new WebSocketClient(new URI("ws://localhost:8080/websocket/" + token)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    String text = "{\"action\":\"ticking\" , " +
                            "\"data\": {\"action\":\"startTicking\", \"ticking\": 30, " +
                            "\"device\":" + Thread.currentThread().getName() +
                            "} }";
                    send(text);
                    System.out.println("onOpen -> send(" + text + ")");
                }

                @Override
                public void onMessage(String message) {

                    System.out.println(message + " -- " + Thread.currentThread().getName());

                    WebSocketResponse<TickingInfo> response = null;
                    try {
                        response = new Gson().fromJson(message, WebSocketResponse.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        close();
                        return;
                    }

                    TickingInfo tickingInfo = null;
                    try {
                        GsonResponseParse<TickingInfo> parse = new GsonResponseParse<TickingInfo>() {
                        };
                        tickingInfo = parse.deal(message);
                    } catch (Exception e) {

                    }

                    if (response.getCode() == ResponseStateCode.FAILURE.getCode()) {
                        if (tickingInfo != null && tickingInfo.getAction().equals(TickingAction.ON_TICKING)) {
                            tickingInfo.setAction(TickingAction.CANCEL_TICKING);
                            WebSocketMessage<TickingInfo> m = new WebSocketMessage<>();
                            m.setAction(WebSocketMessage.MessageAction.TICKING);
                            m.setData(tickingInfo);
                            send(new Gson().toJson(m));
                            return;
                        }
                    } else if (response.getCode() == ResponseStateCode.SUCCESS.getCode()) {
                        if (tickingInfo != null && tickingInfo.getAction().equals(TickingAction.CANCEL_TICKING)) {
                            String text = "{\"action\":\"ticking\" , " +
                                    "\"data\": {\"action\":\"startTicking\", \"ticking\": 30, " +
                                    "\"device\":" + Thread.currentThread().getName() +
                                    "} }";
                            send(text);
//                            close();
                        }
                    }

//                    close();
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("onClose");
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("onError");
                }
            };
            client.connect();
            countDownLatch.await(5, TimeUnit.SECONDS);
            client = null;
        } catch (URISyntaxException |
                InterruptedException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        return result;
    }
}

