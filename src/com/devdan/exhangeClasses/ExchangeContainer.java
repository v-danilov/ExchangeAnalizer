package com.devdan.exhangeClasses;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExchangeContainer {
    private String filePath;
    private Map<String, Queue<ExchangeData>> dataMap;
    private Map<String, Result> resultMap;
    private List<Thread> threadList;

    public ExchangeContainer(String filePath) {
        this.filePath = filePath;
        dataMap = new HashMap<String, Queue<ExchangeData>>();
        resultMap = new ConcurrentHashMap<String, Result>();
        threadList = new ArrayList<Thread>();
    }

    public void parseFile() {
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                parseString(line);
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("File not found");
        } catch (IOException ioe) {
            System.out.println(ioe.getStackTrace());
        } catch (ParseException pe) {
            System.out.println(pe.getStackTrace());
        }

       /* for(String key : dataMap.keySet()){
            System.out.println(key);
            System.out.println(dataMap.get(key));
        }*/
    }

    public void sortData() {
        Set<String> keySet = dataMap.keySet();
        for (String key : keySet) {
            List<ExchangeData> sortedList = new ArrayList<ExchangeData>(dataMap.get(key));
            Collections.sort(sortedList, new Comparator<ExchangeData>() {
                @Override
                public int compare(ExchangeData o1, ExchangeData o2) {
                    return o1.getTime().compareTo(o2.getTime());
                }
            });
            Queue<ExchangeData> sortedQueue = new LinkedList<ExchangeData>(sortedList);
            dataMap.put(key, sortedQueue);
        }
    }


    public void countTrades() {
        Set<String> keySet = dataMap.keySet();

        for (final String key : keySet) {

            final Queue<ExchangeData> data_queue = dataMap.get(key);

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    findMax(key, data_queue);
                }
            });
            threadList.add(thread);
            thread.start();

        }
    }

    private void findMax(String key, Queue<ExchangeData> data_queue) {

        Queue<ExchangeData> window_queue = new LinkedList<ExchangeData>();

        //Take first element
        while (!data_queue.isEmpty()) {

            Result result_for_exchange = new Result();

            //Set up window start time
            ExchangeData start_element = data_queue.poll();

            //Add element to one second window
            window_queue.add(start_element);

            //Remember start time
            result_for_exchange.setStart_window_time(start_element.getTime());

            boolean go_next = true;

            //Search all trades in 1s interval
            while (go_next && !data_queue.isEmpty()) {

                ExchangeData end_element = data_queue.peek();
                long end_window = end_element.getTime().getTime() - 1000;

                //Check current time with window head
                //If in range
                if (window_queue.peek().getTime().getTime() >= end_window) {

                    //Save end time
                    result_for_exchange.setEnd_window_time(end_element.getTime());

                    //Add new element from common list (queue)
                    window_queue.add(data_queue.poll());

                    go_next = true;
                }
                //Not in range
                else {

                    //Save current window size
                    result_for_exchange.setTrades_counter(window_queue.size());

                    //Check for max and save it
                    saveMax(key, result_for_exchange);

                    go_next = false;

                    //Delete head of the window to scale it
                    window_queue.poll();
                }

            }

        }
    }

    private void saveMax(String key, Result res) {
        //If we already have data for the current exchange
        if (resultMap.containsKey(key)) {

            //If old data have a lower value
            if (resultMap.get(key).getTrades_counter() < res.getTrades_counter()) {
                //Save new value
                resultMap.put(key, res);
            }
        }
        //If data is new
        else {
            //Save data
            resultMap.put(key, res);
        }
    }

    public void showStats() throws InterruptedException {

        for (Thread th : threadList) {
            th.join();
        }

        Set<String> keySet = resultMap.keySet();

        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.SSS");

        System.out.println("------------------------------");
        for (String key : keySet) {

            Result res = resultMap.get(key);
            System.out.println("Биржа: " + key);
            System.out.println("максимальное количество сделок в течение одной секунды было между "
                    + dateFormat.format(res.getStart_window_time()) + " и "
                    + dateFormat.format(res.getEnd_window_time()) + ". В этот интервал прошло "
                    + res.getTrades_counter() + " сделок.");
            System.out.println("------------------------------");
        }
    }

    private void parseString(String input_string) throws ParseException {
        String string_data[] = input_string.split(",");
        String key = string_data[3];

        //Create element
        ExchangeData element = buildObject(string_data);

        if (dataMap.containsKey(key)) {

            //Update list
            Queue<ExchangeData> updatedQueue = dataMap.get(key);
            updatedQueue.add(element);

            //Update map
            dataMap.put(key, updatedQueue);

        } else {

            //Create list
            Queue<ExchangeData> dataQueue = new LinkedList<ExchangeData>();

            //Add element to queue
            dataQueue.add(element);


            //Update map
            dataMap.put(key, dataQueue);
        }
    }

    private ExchangeData buildObject(String arr[]) throws ParseException {
        //Date (Time)
        DateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
        Date date = sdf.parse(arr[0]);

        //Price
        double price = Double.parseDouble(arr[1]);

        //Size
        int size = Integer.parseInt(arr[2]);

        return new ExchangeData(date, price, size);
    }

}
