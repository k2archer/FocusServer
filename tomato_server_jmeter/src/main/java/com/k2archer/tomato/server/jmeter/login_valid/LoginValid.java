package com.k2archer.tomato.server.jmeter.login_valid;

import com.k2archer.common.BaseResponseBody;
import com.k2archer.common.ResponseStateCode;
import org.apache.jmeter.samplers.SampleResult;

public class LoginValid {

    public static SampleResult loginByAccurateInfo(BaseResponseBody responseBody, SampleResult result) {
        boolean isSuccess = responseBody.getCode() == ResponseStateCode.SUCCESS.getCode();
        result.setSuccessful(isSuccess);
        if (!isSuccess) {
            result.setResponseData(responseBody.getMessage());
            return result;
        }
        return null;
    }

    public static SampleResult loginByErrorInfo(BaseResponseBody responseBody, SampleResult result) {
        boolean isSuccess = responseBody.getCode() == ResponseStateCode.FAILURE.getCode();
        result.setSuccessful(isSuccess);
        if (!isSuccess) {
            result.setResponseData(responseBody.getMessage());
            return result;
        }
        return null;
    }
}
