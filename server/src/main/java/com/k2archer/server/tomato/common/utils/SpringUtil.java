package com.k2archer.server.tomato.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

public class SpringUtil {

    private static ApplicationContext applicationContext;

    private static byte environmental = 0;

    public static void setApplicationContext(ApplicationContext context) {
        SpringUtil.applicationContext = context;
        Environment env = applicationContext.getEnvironment();
        String[] activeProFiles = env.getActiveProfiles();
        for (String profile : activeProFiles) {
            if(profile.equals("dev")) {
                environmental = 1;
            }
        }
    }

    public static boolean isDev() {
        return environmental == 1;
    }

}
