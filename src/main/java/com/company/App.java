package com.company;

import java.io.IOException;


public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
       Parser parser = new Parser();
       parser.getDataAndSave("diesel");
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("runtime in millis "+duration);
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory())+ "/ mb");

    }
}
