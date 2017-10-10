package com.sweetrollthief.hub;

import org.springframework.context.annotation.*;

import com.sweetrollthief.hub.intercom.Intercom;
import com.sweetrollthief.hub.gate.DefaultGateImpl;
import com.sweetrollthief.hub.router.DefaultRouterImpl;

@Configuration
public class HubConfig {
    @Bean
    public Console console() {
        return new Intercom();
    }
    @Bean
    public Gate gate() {
        return new DefaultGateImpl();
    }
    @Bean
    public Router router() {
        return new DefaultRouterImpl();
    }
}
