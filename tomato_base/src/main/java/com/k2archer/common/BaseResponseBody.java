package com.k2archer.common;

/**
 * @api {RESPONSE} ResponseBody 请求响应包装类
 * @apiGroup 数据结构
 * @apiDescription HTTP 请求返回的 Body 包装结构
 * @apiParam {ResponseStateCode} code <a href="/#api-数据结构-ResponseResponsestatecode">响应状态码</a>
 * @apiParam {String} message 提示消息
 * @apiParam {Object} data 数据
 * @apiSuccessExample {json} Success-Response:
 * {
 * "code" : 0,
 * "message" : "",
 * "data": {
 * }
 * }
 */
public class BaseResponseBody<T> {
    private int code;
    private String message;
    private T data;

    public BaseResponseBody() {
    }

    public BaseResponseBody(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(ResponseStateCode code) {
        this.code = code.getCode();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
