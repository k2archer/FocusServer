package com.k2archer.server.tomato.service.impl;

import com.k2archer.server.tomato.bean.dto.User;
import com.k2archer.server.tomato.dao.TokenMapper;
import com.k2archer.server.tomato.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TokenServiceImpl implements TokenService {


    @Autowired
    TokenMapper tokenMapper;

    @Override
    public String generateToken(Long user_id) {
        // todo ... 简单生成 Token
        String token = "token-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(10000000);

        if (tokenMapper.addToken(token, user_id) > 0) {
            return token;
        }

        return null;
    }

}
