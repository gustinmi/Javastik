package com.javastik.jdbc.utils;

public class StringUtils {

    public static int countMatches(String hayStack, String needle) {
        if (hayStack == null || hayStack.isEmpty()) return 0;
        if (needle == null || needle.isEmpty()) return 0;
        
        int count = hayStack.length() - hayStack.replace(needle, "").length();
        return count;

    }

    
    
}
