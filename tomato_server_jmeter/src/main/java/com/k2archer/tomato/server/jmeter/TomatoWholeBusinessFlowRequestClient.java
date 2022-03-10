package com.k2archer.tomato.server.jmeter;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.k2archer.common.BaseResponseBody;
import com.k2archer.common.utils.GsonResponseParse;
import com.k2archer.tomato.server.jmeter.login_valid.LoginValid;
import demo.LoginInfo;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;

public class TomatoWholeBusinessFlowRequestClient extends AbstractJavaSamplerClient {
    @Override
    public Arguments getDefaultParameters() {
        Arguments args = DefaultArguments.getArguments();
        return args;
    }

    /*
    todo 登录、开始Ticking、取消Ticking、开始Ticking、Finish Ticking、退出登录
     */

    @Override
    public void setupTest(JavaSamplerContext context) {
//        super.setupTest(context);
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

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        result.setContentType("application/json");

        // 正常登录
        Arguments parameters = DefaultArguments.getArguments();
        BaseResponseBody responseBody = handleRequest(parameters, result);
        if (responseBody != null) {
            if (LoginValid.loginByAccurateInfo(responseBody, result) != null) {
                return result;
            }
        }
        if (!result.isSuccessful()) {
            return result;
        }

        // 无密码登录测试
        result = new SampleResult();
        result.setContentType("application/json");
        Arguments args = new Arguments();
        args.addArgument("path", "http://localhost:8080/api/user/login");
        args.addArgument("body", "{ \"username\" : \"kwei\" }");
        responseBody = handleRequest(args, result);
        if (responseBody != null) {
            LoginValid.loginByErrorInfo(responseBody, result);
        }
        if (!result.isSuccessful()) {
            return result;
        }

        // 无用户名登录测试
        result = new SampleResult();
        result.setContentType("application/json");
        args = new Arguments();
        args.addArgument("path", "http://localhost:8080/api/user/login");
        args.addArgument("body", "{ \"password\" : \"123\" }");
        responseBody = handleRequest(args, result);
        if (responseBody != null) {
            LoginValid.loginByErrorInfo(responseBody, result);
        }
        if (!result.isSuccessful()) {
            return result;
        }

        // 密码错误登录测试
        result = new SampleResult();
        result.setContentType("application/json");
        args = new Arguments();
        args.addArgument("path", "http://localhost:8080/api/user/login");
        args.addArgument("body", "{\"username\" : \"kwei\", \"password\" : \"error_password\" }");
        responseBody = handleRequest(args, result);
        if (responseBody != null) {
            LoginValid.loginByErrorInfo(responseBody, result);
        }
        if (!result.isSuccessful()) {
            return result;
        }

        result.setSuccessful(true);
        return result;
    }
}
