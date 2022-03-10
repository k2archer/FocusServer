package demo;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

public class TestJarClass {

    public static void main(String[] args) {



        for (int i = 0; i < 1; i++) {
            Thread thread = new Thread(() -> {  // lamda表达式
                JMeterClient test = new JMeterClient();
                Arguments arg0 = test.getDefaultParameters();
                JavaSamplerContext argResult = new JavaSamplerContext(arg0);
                test.setupTest(argResult);
                test.runTest(argResult);
            });
            thread.start();
        }

    }
}