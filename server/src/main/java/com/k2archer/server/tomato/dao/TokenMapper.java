package com.k2archer.server.tomato.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TokenMapper {


    @Insert("INSERT INTO token(token,user_id,expires) values(#{token}, #{user_id}, now());")
    int addToken(@Param("token")String token, @Param("user_id") Long user_id);
}
