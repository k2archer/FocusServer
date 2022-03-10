package com.k2archer.server.tomato.service.impl;

import com.k2archer.server.tomato.bean.dto.LoginInfo;
import com.k2archer.server.tomato.dao.UserMapper;
import com.k2archer.server.tomato.service.UserService;
import com.k2archer.server.tomato.bean.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User getUser(LoginInfo loginInfo) {
        // todo ...
        if (loginInfo.getUsername() != null) {
            return userMapper.getUserByName(loginInfo.getUsername(), loginInfo.getPassword());
        }

        return null;
    }

    @Override
    public User getUserByToken(String token) {
        return userMapper.getUserByToken(token);
    }
}
