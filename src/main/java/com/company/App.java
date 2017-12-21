package com.company;

import java.io.IOException;


public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {

       Parser parser = new Parser();
       parser.getDataAndSave("diesel");

    }
}
