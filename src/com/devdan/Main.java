package com.devdan;

import com.devdan.exhangeClasses.ExchangeContainer;

public class Main {


    public static void main(String[] args) {

        ExchangeContainer exchangeContainer = new ExchangeContainer("TRD2.csv");
        exchangeContainer.parseFile();

        //Sort exchange data if necessary
        //exchangeContainer.sortData();

        exchangeContainer.countTrades();
        exchangeContainer.showStats();
    }

}
