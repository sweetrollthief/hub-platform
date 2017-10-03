package com.sweetrollthief.hub;

import org.springframework.context.annotation.*;

import com.sweetrollthief.hub.gate.Gate;

@Configuration
public class HubConfig {
    @Bean(destroyMethod = "close")
    public Gate gate() {
        return new Gate();
    }
}
