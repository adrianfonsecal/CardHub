package com.example.cardhubapp.model;

public class Cardholder {
    private Integer id;
    private CreditCardProduct[] creditCardProducts;

    public Cardholder() {
        //Do Nothing
    }

    public void addCardToWallet(String userEmail){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CreditCardProduct[] getCreditCardProducts() {
        return creditCardProducts;
    }

    public void setCreditCardProducts(CreditCardProduct[] creditCardProducts) {
        this.creditCardProducts = creditCardProducts;
    }

}
