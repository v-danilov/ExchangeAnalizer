package com.devdan.exhangeClasses;

import java.util.Date;

/**
 * Created by Bounc on 26.09.2017.
 */
public class ExchangeData {
    private Date date;
    private double price;
    private int size;

    public ExchangeData() {}

    public ExchangeData(Date date, double price, int size) {
        this.date = date;
        this.price = price;
        this.size = size;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExchangeData that = (ExchangeData) o;

        if (Double.compare(that.price, price) != 0) return false;
        if (size != that.size) return false;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = date.hashCode();
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + size;
        return result;
    }

    @Override
    public String toString() {
        return "ExchangeData{" +
                "date=" + date +
                ", price=" + price +
                ", size=" + size +
                '}';
    }

}
