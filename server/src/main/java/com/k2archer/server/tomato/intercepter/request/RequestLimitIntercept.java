package com.k2archer.server.tomato.intercepter.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//import top.lrshuai.limit.annotation.RequestLimit;
//import top.lrshuai.limit.common.ApiResultEnum;
//import top.lrshuai.limit.common.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求拦截
 */
@Slf4j
@Component
public class RequestLimitIntercept extends HandlerInterceptorAdapter {

//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * isAssignableFrom() 判定此 Class 对象所表示的类或接口与指定的 Class 参数所表示的类或接口是否相同，或是否是其超类或超接口
         * isAssignableFrom()方法是判断是否为某个类的父类
         * instanceof关键字是判断是否某个类的子类
         */
//        String body = request.getReader().readLine();
//        log.error(body);
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            //HandlerMethod 封装方法定义相关的信息,如类,方法,参数等
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 获取方法中是否包含注解
            RequestLimit methodAnnotation = method.getAnnotation(RequestLimit.class);
            //获取 类中是否包含注解，也就是controller 是否有注解
            RequestLimit classAnnotation = method.getDeclaringClass().getAnnotation(RequestLimit.class);
            // 如果 方法上有注解就优先选择方法上的参数，否则类上的参数
            RequestLimit requestLimit = methodAnnotation != null ? methodAnnotation : classAnnotation;
            if (requestLimit != null) {
                if (isLimit(request, requestLimit)) {
                    resonseOut(response);
//                    resonseOut(response, Result.error(ApiResultEnum.REQUST_LIMIT));
                    return false;
                }
            }
        }
        return super.preHandle(request, response, handler);
    }


    private Map<String, AtomicInteger> requestCountMap = new HashMap<>();


    //判断请求是否受限
    public boolean isLimit(HttpServletRequest request, RequestLimit requestLimit) {
        // 受限的redis 缓存key ,因为这里用浏览器做测试，我就用sessionid 来做唯一key,如果是app ,可以使用 用户ID 之类的唯一标识。
        String limitKey = request.getServletPath() + request.getSession().getId();
//        log.info(limitKey);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                requestCountMap.get(limitKey).set(0);
                timer.cancel();
            }
        }, 2000 , 2000);

        AtomicInteger frequency = requestCountMap.get(limitKey);
        if (frequency == null) {
            requestCountMap.put(limitKey, new AtomicInteger());
        } else {
            int i = frequency.get();
            log.info("onLimit " + i);
            if (i > requestLimit.maximum()) {
                log.error("onLimit " + i);
                return true;
            }
            requestCountMap.get(limitKey).incrementAndGet();
        }

        // 从缓存中获取，当前这个请求访问了几次
//        Integer redisCount = (Integer) redisTemplate.opsForValue().get(limitKey);
//        if (redisCount == null) {
//            //初始 次数
//            redisTemplate.opsForValue().set(limitKey, 1, requestLimit.second(), TimeUnit.SECONDS);
//        } else {
//            if (redisCount.intValue() >= requestLimit.maxCount()) {
//                return true;
//            }
//            // 次数自增
//            redisTemplate.opsForValue().increment(limitKey);
//        }
        return false;
    }

    //    /**
//     * 回写给客户端
//     *
//     * @param response
//     * @param result
//     * @throws IOException
//     */
//    private void resonseOut(HttpServletResponse response, Result result) throws IOException {
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json; charset=utf-8");
//        PrintWriter out = null;
//        String json = JSONObject.toJSON(result).toString();
//        out = response.getWriter();
//        out.append(json);
//    }
    private void resonseOut(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
//        String json = JSONObject.toJSON(result).toString();
        out = response.getWriter();
        out.append("onLimit");

    }
}