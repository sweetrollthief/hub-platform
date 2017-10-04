package com.sweetrollthief.hub;

import org.springframework.context.annotation.*;

import com.sweetrollthief.hub.gate.DefaultGateImpl;
import com.sweetrollthief.hub.intercom.Intercom;

@Configuration
public class HubConfig {
    @Bean
    public Hub hub() {
        return new Hub();
    }
    @Bean
    public Console console() {
        return new Intercom();
    }
    @Bean
    public Gate gate() {
        return new DefaultGateImpl();
    }
}
