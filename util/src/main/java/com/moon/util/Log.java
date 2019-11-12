package com.moon.util;


public class Log {
    public static void log(String log,String ... param){
        System.out.println(String.format(log,param));
    }

    public static void log(String log){
        System.out.println(log);
    }
}
