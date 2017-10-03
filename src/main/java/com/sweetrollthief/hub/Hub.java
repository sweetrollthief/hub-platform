package com.sweetrollthief.hub;

import org.springframework.context.annotation.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import com.sweetrollthief.hub.gate.Gate;

/**
* Main class, program entry point
*
**/
public class Hub {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(HubConfig.class);
        System.out.println("Hello world " + ctx.getBean(Gate.class));

        ((ConfigurableApplicationContext) ctx).close();
    }
}
