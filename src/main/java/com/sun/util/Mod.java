package com.sun.util;

public class Mod {
    public static void main(String[] args) {
        testMethod1();
        testMethod2();
    }

    public static void testMethod1(){
        System.out.println("serverCollector");
        System.out.println(getMod("serverCollector"));
    }

    public static void testMethod2(){
        String s = "test-group";
        System.out.println(s);
        System.out.println(getMod(s));
    }


    private static int getMod(String string){
        int a = string.hashCode();
        System.out.println(a);
        return a % 50;

    }

}
