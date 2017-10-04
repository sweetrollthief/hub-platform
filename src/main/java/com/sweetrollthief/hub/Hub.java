package com.sweetrollthief.hub;

import org.springframework.context.annotation.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
* Static program entry point
*
**/
public class Hub extends HubBean {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(HubConfig.class);

        try {
            context.getBean(Hub.class).watch();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ConfigurableApplicationContext) context).close();
    }

    public void watch() throws Exception {
        while (true) {
            switch (validateCommand(getBean(Console.class).watch())) {
                case 100:
                    break;
                case 200:
                    return;
                case 500:
                    throw new Exception("Error");
            }
        }
    }

    public int validateCommand(String str) {
        if (str == null) {
            return 500;
        } else if ("exit".equals(str)) {
            return 200;
        }

        return 100;
    }

    public void close() {

    }
}
