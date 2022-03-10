package com.k2archer.server.tomato;

import com.k2archer.server.tomato.common.utils.SpringUtil;
import org.apache.ibatis.mapping.Environment;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.k2archer.server.tomato.dao")
public class TomatoApplication {

    public static void main(String[] args) {
//        SpringApplication.run(TomatoApplication.class, args);

        ApplicationContext context = SpringApplication.run(TomatoApplication.class, args);
        SpringUtil.setApplicationContext(context);
    }
}
