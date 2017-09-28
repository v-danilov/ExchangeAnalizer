package com.devdan;

import com.devdan.exhangeClasses.ExchangeContainer;

public class Main {


    public static void main(String[] args) {

        ExchangeContainer exchangeContainer = new ExchangeContainer("TRD2.csv");

        long startTime = System.currentTimeMillis();

        exchangeContainer.parseFile();

        //Sort exchange data if necessary
        //exchangeContainer.sortData();

        exchangeContainer.countTrades();
        try {
            exchangeContainer.showStats();
        } catch (InterruptedException ie) {
            System.err.println(ie.getStackTrace());
        }

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(estimatedTime);
    }

}
