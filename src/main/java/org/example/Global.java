package org.example;

import org.springframework.context.ApplicationContext;

public class Global {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        Global.applicationContext = applicationContext;
    }
}
