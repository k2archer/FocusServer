package demo;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class JMeterClient extends AbstractJavaSamplerClient {

    private String OID;
    private String tradeName;
    private String tradeTitle;
    private String sessionID;
    private String stepKey;
    private String path1;
    private String path;
    private String modulName;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private Boolean judge = false;


    public Arguments getDefaultParameters() {

        Arguments args = new Arguments();
		args.addArgument("path", "http://localhost:8080/api/user/login");
        args.addArgument("path1", "http://localhost:8080/api/user/login");
//        args.addArgument("path1", "http://49.233.250.34:80/wangyang/moduleb-aase/aase/openTrade");
//        args.addArgument("path", "http://49.233.250.34:80/wangyang/moduleb-aase/aase/execTrade");
        args.addArgument("Content-Type", "application/json");
//        args.addArgument("X-ABX-ModuleName", "ModuleB");
//        args.addArgument("X-ABX-TradeName", "trade%2F%E5%AE%A2%E6%88%B7%E4%BF%A1%E6%81%AF%E7%BB%B4%E6%8A%A4");
//        args.addArgument("X-ABX-TradeTitle", "%E5%AE%A2%E6%88%B7%E4%BF%A1%E6%81%AF%E7%BB%B4%E6%8A%A4");
//        args.addArgument("X-ABX-StepKey", "");
//        args.addArgument("X-ABX-SessionID", "");
        return args;
    }


    @Override
    public void setupTest(JavaSamplerContext context) {
        String str = UUID.randomUUID().toString();
        String tempString = str.substring(0, 8) + str.substring(9, 13) +
                str.substring(14, 18) + str.substring(19, 23) +
                str.substring(24);
        OID = tempString;
        path1 = context.getParameter("path1");
        path = context.getParameter("path");
        tradeName = context.getParameter("X-ABX-TradeName");
        tradeTitle = context.getParameter("X-ABX-TradeTitle");
        sessionID = context.getParameter("X-ABX-StepKey");
        stepKey = context.getParameter("X-ABX-StepKey");
        modulName = context.getParameter("X-ABX-ModuleName");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("OID", OID);
        map.put("tradeName", tradeName);
        map.put("tradeTitle", tradeTitle);
        map.put("StepKey", stepKey);
        map.put("path", path);
        map.put("sessionID", sessionID);
        map.put("modulName", modulName);
//        WebSockets webSocket;
//        try {
//            webSocket = new WebSockets("ws://localhost:8080/websocket/token11", countDownLatch,map);
//            webSocket.createWebSocket();
//            Map<String, Object> initMessage = new HashMap<>();
//            Map<String, String> data = new HashMap<>();
////            initMessage.put("type", "init");
////            initMessage.put("id", OID);
////            initMessage.put("data", data);
//            webSocket.sendData(initMessage);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 每个线程执行多次,相当于loadrunner的Action()方法 (non-Javadoc)
     */
    public SampleResult runTest(JavaSamplerContext arg0) {
        boolean if_success = true;// 测试结果标记位
        SampleResult sr = new SampleResult(); // 为避免多线程问题，设置sr为局部变量
        sr.setSampleLabel("java请求");
        try {
            Map<String, Object> fMap = new HashMap<>();
            fMap.put("StepKey", "");
            fMap.put("sessionID", "");
            fMap.put("OID", OID);
            fMap.put("path1", path1);
            fMap.put("modulName", modulName);
            fMap.put("url", "1.txt");
            judge = true;
            fMap.put("judge", judge);
            sr.sampleStart();
            // 开始发起第一次请求
            new demo.HttpURLConnectionDemos().doPost(fMap);
//			countDownLatch.countDown();
            sr.setResponseData("响应结果", "utf-8"); // 第二个参数 为编码， 设置JMeter GUI "取样器结果" DataEncoding: utf-8
            sr.setDataType(SampleResult.TEXT); // 设置JMeter GUI "取样器结果" Data type ("text"|"bin"|""):text
            sr.setResponseMessageOK(); // 设置JMeter GUI "取样器结果" Response message: OK
            sr.setResponseCodeOK(); // 设置JMeter GUI "取样器结果" Response code: 200
            if_success = true;
//			countDownLatch.await();
        } catch (Exception e) {
            if_success = false; // 请求失败
            sr.setResponseCode("500"); // 设置JMeter GUI "取样器结果" Response code: 500
            e.printStackTrace();
        } finally {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            sr.sampleEnd();
            sr.setSuccessful(if_success);
        }
        return sr;
    }

    public void teardownTest() {

    }
}

