package com.k2archer.server.tomato.dao;


import com.k2archer.server.tomato.bean.dto.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {

    @Select("select * from t_user where user_name = #{user_name} and user_password = #{user_password}")
    @Results(id = "userMap", value = {
            @Result(column = "userID", property = "Id", javaType = Long.class),
            @Result(column = "user_name", property = "name", javaType = String.class)
    })
    User getUserByName(@Param("user_name") String user_name, @Param("user_password") String user_password);

    @Select("SELECT * FROM t_user WHERE userID = (SELECT user_id FROM token WHERE token = #{token})")
    @Results(id = "tokenUserMap", value = {
            @Result(column = "userID", property = "Id", javaType = Long.class),
            @Result(column = "user_name", property = "name", javaType = String.class)
    })
    User getUserByToken(@Param("token") String token);


}
