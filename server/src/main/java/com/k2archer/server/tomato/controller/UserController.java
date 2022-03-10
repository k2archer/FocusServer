package com.k2archer.server.tomato.controller;

import com.google.gson.Gson;
import com.k2archer.common.ResponseStateCode;
import com.k2archer.server.tomato.bean.dto.UserInfo;
import com.k2archer.server.tomato.bean.response.ResponseBody;
import com.k2archer.server.tomato.bean.dto.LoginInfo;
import com.k2archer.server.tomato.intercepter.request.RequestLimit;
import com.k2archer.server.tomato.service.TokenService;
import com.k2archer.server.tomato.service.UserService;
import com.k2archer.server.tomato.bean.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * @api {POST} /api/user/login 登录
     * @apiGroup 用户
     * @apiDescription 用户登录接口，登录成功后，需要用 Token 连接 WebSocket 服务
     * @apiParam {String} userName 用户名称
     * @apiParam {String} password 密码
     * @apiParamExample{json} {JSON} Request-Body-Example:
     * {
     * "username" : "name",
     * "password" : "pw"
     * }
     * @apiSuccessExample {json} Success-Response-Example:
     * {
     * "code" : 0,
     * "message" : "",
     * "data": {
     * "token" : "T2O3K4E5N123"
     * }
     * }
     */
    @RequestMapping(value = "/api/user/login")
    @RequestLimit(second = 1, maximum = 1)
    public String login2(@RequestBody @Validated LoginInfo loginInfo) {
        ResponseBody<UserInfo> boy = new ResponseBody<>();

        User user = userService.getUser(loginInfo);

        if (user != null) {
            String token = tokenService.generateToken(user.getId());
            if (token != null) {
                boy.setCode(ResponseStateCode.SUCCESS);
                UserInfo userInfo = new UserInfo();
                userInfo.setToken(token);
                boy.setData(userInfo);
                boy.setMessage(new Gson().toJson(user));
            } else {
                boy.setCode(ResponseStateCode.FAILURE);
                boy.setMessage("生成 Token 失败");
            }
        } else {
            boy.setCode(ResponseStateCode.FAILURE);
            boy.setData(null);
            boy.setMessage("账号密码错误");
        }

        return new Gson().toJson(boy);
    }
}
