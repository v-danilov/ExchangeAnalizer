package com.devdan.exhangeClasses;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExchangeContainer {
    private String filePath;
    private Map<String, List<ExchangeData>> dataMap;
    private Map<String, Result> resultMap;

    public ExchangeContainer(String filePath) {
        this.filePath = filePath;
        dataMap = new HashMap<String, List<ExchangeData>>();
        resultMap = new HashMap<String, Result>();
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
            List<ExchangeData> sortedList = dataMap.get(key);
            Collections.sort(sortedList, new Comparator<ExchangeData>() {
                @Override
                public int compare(ExchangeData o1, ExchangeData o2) {
                    return o1.getTime().compareTo(o2.getTime());
                }
            });
            dataMap.put(key, sortedList);
        }
    }

    public void countTrades() {
        Set<String> keySet = dataMap.keySet();

        for (String key : keySet) {

            List<ExchangeData> data_list = dataMap.get(key);
            List<Integer> transaction_counter = new ArrayList<Integer>();

            int list_size = data_list.size();

            //Take first element
            for (int i = 0; i < list_size; i++) {

                int counter = 1;
                Result result_for_exchange = new Result();

                //Set up window start time
                long start_window = data_list.get(i).getTime().getTime();

                //Remember start time
                result_for_exchange.setStart_window_time(data_list.get(i).getTime());

                //Search all trades in 1s interval
                for (int j = i + 1; j < list_size; j++) {

                    long end_window = data_list.get(j).getTime().getTime() - 1000;

                    //Trade belongs to 1s window
                    if (start_window >= end_window) {
                        counter++;
                        result_for_exchange.setEnd_window_time(data_list.get(j).getTime());
                    }

                    //Trade doesnt belong to 1s window -> window is closed
                    else
                    {
                        //If we already have data for the current exchange
                        if(resultMap.containsKey(key)){

                            //If old data have a lower value
                            if(resultMap.get(key).getTrades_counter() < counter){
                                //Save new value
                                result_for_exchange.setTrades_counter(counter);
                                resultMap.put(key,result_for_exchange);
                            }
                        }
                        //If data is new
                        else {

                            //Save data
                            result_for_exchange.setTrades_counter(counter);
                            resultMap.put(key,result_for_exchange);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void showStats(){
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
            List<ExchangeData> updatedList = dataMap.get(key);
            updatedList.add(element);

            //Update map
            dataMap.put(key, updatedList);

        } else {

            //Create list
            List<ExchangeData> dataList = new ArrayList<ExchangeData>();

            //Add element tot list
            dataList.add(element);

            //Update map
            dataMap.put(key, dataList);
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
