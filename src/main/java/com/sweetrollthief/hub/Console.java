package com.sweetrollthief.hub;

import java.io.InputStream;

public interface Console {
    void setInputStream(InputStream in);
    String getInputLine();
}
