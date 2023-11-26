package com.example.cardhubapp.model;

public class AccountStatement {

    private Integer id;
    private String cutOffDate;
    private String paymentDate;
    private Float currentDebt;
    private Float paymentForNoInterest;

    public AccountStatement(String cutOffDate, String paymentDate, Float currentDebt, Float paymentForNoInterest) {
        setCutOffDate(cutOffDate);
        setPaymentDate(paymentDate);
        setCurrentDebt(currentDebt);
        setPaymentForNoInterest(paymentForNoInterest);
    }

    public AccountStatement(Integer id, String cutOffDate, String paymentDate, Float currentDebt, Float paymentForNoInterest) {
        setId(id);
        setCutOffDate(cutOffDate);
        setPaymentDate(paymentDate);
        setCurrentDebt(currentDebt);
        setPaymentForNoInterest(paymentForNoInterest);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCutOffDate() {
        return cutOffDate;
    }

    public void setCutOffDate(String cutOffDate) {
        this.cutOffDate = cutOffDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Float getCurrentDebt() {
        return currentDebt;
    }

    public void setCurrentDebt(Float currentDebt) {
        this.currentDebt = currentDebt;
    }

    public Float getPaymentForNoInterest() {
        return paymentForNoInterest;
    }

    public void setPaymentForNoInterest(Float paymentForNoInterest) {
        this.paymentForNoInterest = paymentForNoInterest;
    }

}
