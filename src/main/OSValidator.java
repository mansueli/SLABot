/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;



/**
 * From
 * http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
 */

public class OSValidator {

    private static String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Return OS ID
     * where Windows = 0
     * Mac & Linux & Unix = 1
     * everything else = 2 
     * @return 
     */
    public static int getOSID() {

        if (isWindows()) {
            return 0;
        } else if (isMac()) {
            return 1;
        } else if (isUnix()) {
            return 1;
        } else {
            //not supported
            return 2;
        }
    }

    public static boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        return (OS.indexOf("mac") >= 0);

    }

    public static boolean isUnix() {

        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);

    }

    public static boolean isSolaris() {

        return (OS.indexOf("sunos") >= 0);

    }

}