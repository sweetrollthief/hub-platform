package com.sweetrollthief.hub;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
* Static program entry point
*
**/
public class Hub {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(HubConfig.class);
        ExecutorService service = Executors.newSingleThreadExecutor();

        try {
            configureRouter(context.getBean(Router.class));
            service.submit(configureGate(context.getBean(Gate.class)));
            context.getBean(Console.class).watch();
        } catch (Exception e) {
            e.printStackTrace();
        }

        service.shutdown();
        ((ConfigurableApplicationContext) context).close();
    }

    private static Runnable configureGate(Gate gate) throws Exception {
        gate.addListener(8080);

        return gate;
    }
    private static void configureRouter(Router router) throws Exception {
        router.registerProtocol(8080, new com.sweetrollthief.hub.transfer.http.HttpProvider());
    }
}
