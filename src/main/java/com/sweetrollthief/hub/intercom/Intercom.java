package com.sweetrollthief.hub.intercom;

import java.util.Scanner;

import com.sweetrollthief.hub.HubBean;
import com.sweetrollthief.hub.Console;

/**
* I/O Handling bean
*
**/
public class Intercom extends HubBean implements Console {
    private Scanner in = new Scanner(System.in);

    @Override
    public String watch() {
        try {
            while (!in.hasNextLine()) {
                Thread.sleep(100);
            }

            System.out.println(1231223);

            return in.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
