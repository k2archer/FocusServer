package com.k2archer.tomato.server.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

public class DemoTest {
    public static void main(String[] args) {

//        BaseResponseBody baseResponseBody = new BaseResponseBody();
//        BaseLoginInfo baseInfo = new BaseLoginInfo();


        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(() -> {

                TickingRequestClient jMeterClient = new TickingRequestClient();
                Arguments arg0 = DefaultArguments.getArguments();
                JavaSamplerContext samplerContext = new JavaSamplerContext(arg0);
                jMeterClient.runTest(samplerContext);

//                TomatoWholeBusinessFlowRequestClient test = new TomatoWholeBusinessFlowRequestClient();
//                Arguments arg0 = DefaultArguments.getArguments();
//                JavaSamplerContext argResult = new JavaSamplerContext(arg0);
//
//                test.setupTest(argResult);
//                SampleResult result = test.runTest(argResult);
//
//                System.out.println(result.getFirstAssertionFailureMessage());
////                System.out.println(new Gson().toJson(result));
//
//
////                result.setRequestHeaders("Connection: Close\nHost: www.baidu.com:80");
//                System.out.println(result.getRequestHeaders());
//
//
////                result.getAssertionResults()[0].getFailureMessage()
//                StatisticalSampleResult statisticalSampleResult = new StatisticalSampleResult();
//                AssertionResult assertionResult = new AssertionResult();

            });
            thread.start();
        }
    }
}
