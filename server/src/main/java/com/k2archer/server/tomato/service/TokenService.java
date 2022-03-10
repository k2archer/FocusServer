package com.k2archer.server.tomato.service;

import com.k2archer.server.tomato.bean.dto.User;

public interface TokenService {

    String generateToken(Long user_id);
}
