package com.changgou.pay.pojo;

public class Amount {
    int total;

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "Amount{" +
                "total=" + total +
                ", currency='" + currency + '\'' +
                '}';
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    String currency;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
