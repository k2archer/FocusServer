package com.k2archer.server.tomato.service;

import com.k2archer.server.tomato.bean.dto.LoginInfo;
import com.k2archer.server.tomato.bean.dto.User;

public interface UserService {

    public User getUser(LoginInfo loginInfo);

    User getUserByToken(String token);

}
