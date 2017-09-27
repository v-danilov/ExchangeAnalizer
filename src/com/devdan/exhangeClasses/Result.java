package com.devdan.exhangeClasses;


import java.util.Date;

public class Result {
    private Date start_window_time;
    private Date end_window_time;
    private int trades_counter;

    public Result() {
        start_window_time = null;
        end_window_time = null;
        trades_counter = -1;
    }

    public Result(Date start_window_time, Date end_window_time, int trades_counter) {
        this.start_window_time = start_window_time;
        this.end_window_time = end_window_time;
        this.trades_counter = trades_counter;
    }

    public Date getStart_window_time() {
        return start_window_time;
    }

    public void setStart_window_time(Date start_window_time) {
        this.start_window_time = start_window_time;
    }

    public Date getEnd_window_time() {
        return end_window_time;
    }

    public void setEnd_window_time(Date end_window_time) {
        this.end_window_time = end_window_time;
    }

    public int getTrades_counter() {
        return trades_counter;
    }

    public void setTrades_counter(int trades_counter) {
        this.trades_counter = trades_counter;
    }
}
