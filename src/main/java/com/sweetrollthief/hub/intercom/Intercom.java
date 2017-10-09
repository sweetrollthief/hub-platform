package com.sweetrollthief.hub.intercom;

import java.util.Scanner;

import com.sweetrollthief.hub.Console;

/**
* I/O Handling bean
*
**/
public class Intercom implements Console {
    private Scanner in = new Scanner(System.in);

    @Override
    public void watch() {
        try {
            while (true) {
                switch (getInput()) {
                    case 100:
                        break;
                    case 200:
                        return;
                    case 500:
                        throw new Exception("Error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getInput() throws Exception {
        while (!in.hasNextLine()) {
            Thread.sleep(100);
        }

        String temp = in.nextLine();

        if (temp == null) {
            return 500;
        } else if ("exit".equals(temp)) {
            return 200;
        }

        return 100;
    }
}
