package com.k2archer.server.tomato.exception;

import com.k2archer.server.tomato.bean.response.ResponseBody;
import lombok.Getter;
import lombok.Setter;


/**
 * @api {RESPONSE} ExceptionResponseBody 请求异常响应包装类
 * @apiGroup 数据结构
 * @apiDescription
 * HTTP 请求异常后返回的 Body 包装结构, 继承自 <a href="/#api-数据结构-ResponseResponsebody">请求响应包装类</a>
 * @apiParam (新增) {String} url HTTP 请求路径
 */
public class ExceptionResponseBody<T> extends ResponseBody<T> {

    @Getter @Setter private String url;
}