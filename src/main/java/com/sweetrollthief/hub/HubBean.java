package com.sweetrollthief.hub;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class HubBean implements ApplicationContextAware {
    protected ApplicationContext context;

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }
}
