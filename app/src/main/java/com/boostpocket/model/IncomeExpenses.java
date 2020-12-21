package com.boostpocket.model;

public class IncomeExpenses {

    private String incomeId;
    private String expensesId;
    private String amount;
    private String date;

    public IncomeExpenses(String incomeId, String expensesId, String amount, String date) {
        this.incomeId = incomeId;
        this.expensesId = expensesId;
        this.amount = amount;
        this.date = date;
    }

    public String getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(String incomeId) {
        this.incomeId = incomeId;
    }

    public String getExpensesId() {
        return expensesId;
    }

    public void setExpensesId(String expensesId) {
        this.expensesId = expensesId;
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
}
