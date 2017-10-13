package com.sweetrollthief.hub.intercom;

import java.util.Scanner;
import java.io.InputStream;

import com.sweetrollthief.hub.Console;

/**
* I/O Handling bean
*
**/
public class Intercom implements Console {
    private Scanner in;

    @Override
    public void setInputStream(InputStream is) {
        in = new Scanner(is);
    }

    @Override
    public String getInputLine() {
        try {
            while (!in.hasNextLine()) {
                Thread.sleep(100);
            }

            return in.nextLine();
        } catch (Exception e) {
            e.printStackTrace(); // TODO: handle output printing
        }

        return null;
    }
}
