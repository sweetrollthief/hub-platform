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
    private static ApplicationContext context;
    private static ExecutorService service;

    public static void main(String[] args) {
        context = new AnnotationConfigApplicationContext(HubConfig.class);
        service = Executors.newSingleThreadExecutor();

        try {
            configureRouter();
            configureGate();

            service.submit(context.getBean(Gate.class));

            watch();
        } catch (Exception e) {
            e.printStackTrace();
        }

        service.shutdown();
        ((ConfigurableApplicationContext) context).close();
    }

    private static void watch() {
        Console console = context.getBean(Console.class);

        while (true) { // TODO: remove forever loop
            switch (console.getInputLine()) {
                case "exit":
                    return;
                default:
                    System.out.println("Unknown command");
                    continue;
            }
        }
    }
    
    private static void configureGate() throws Exception {
        Gate gate = context.getBean(Gate.class);
        gate.addListener(8080);
    }
    private static void configureRouter() throws Exception {
        Router router = context.getBean(Router.class);
        router.registerProtocol(8080, new com.sweetrollthief.hub.transfer.http.HttpProvider());
    }
}
