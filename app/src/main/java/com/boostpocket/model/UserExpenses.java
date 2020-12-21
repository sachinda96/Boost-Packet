package com.boostpocket.model;

public class UserExpenses {

    private String user;
    private String categoryId;
    private String date;
    private String amount;


    public UserExpenses(String user, String categoryId, String date, String amount) {
        this.user = user;
        this.categoryId = categoryId;
        this.date = date;
        this.amount = amount;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
