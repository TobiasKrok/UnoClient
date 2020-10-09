package com.tobias.utils;

import java.util.Arrays;

public class IPValidator {

    public static boolean isDecimal(String string) {
        // Check whether string has a leading zero but is not "0"
        if (string.startsWith("0")) {
            return string.length() == 1;
        }
        for(char c : string.toCharArray()) {
            if(c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static boolean isIpv4(String ip) {
        // while localhost is not an ipv4 address, we want the program to accept it when hosting local games
        if(ip.equals("localhost")) return true;

        String[] parts = ip.split("\\.", -1);
        return parts.length == 4 // 4 parts
                && Arrays.stream(parts)
                .filter(IPValidator::isDecimal) // Only decimal numbers
                .map(Integer::parseInt)
                .filter(i -> i <= 255 && i >= 0) // Must be inside [0, 255]
                .count() == 4; // 4 numerical parts inside [0, 255]
    }

    public static boolean isValidPort(String portStr) {
        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            return false;
        }
        return port > 1 && port <= 65535;
    }
}
