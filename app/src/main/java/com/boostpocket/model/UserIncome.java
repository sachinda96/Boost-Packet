package com.boostpocket.model;

import java.util.Date;

public class UserIncome {

    private String user;
    private String categoryId;
    private String amount;
    private String date;
    private String balance;

    public UserIncome() {
    }

    public UserIncome(String user, String categoryId, String amount, String date,String balance) {
        this.user = user;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.balance =balance;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
