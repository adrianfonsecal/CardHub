package com.example.cardhubapp.model;

public class CreditCardProduct {

    private Integer cardId;
    private String name;
//    private String bankName;
//    private float interestRate;
//    private float annuity;
    public CreditCardProduct(int cardId, String name){
        setCardId(cardId);
        setName(name);
//        setBankName(bankName);
//        setInterestRate(interestRate);
//        setAnnuity(annuity);
    }

    public String toString(){
        return this.cardId.toString();
    }
    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getBankName() {
//        return bankName;
//    }
//
//    public void setBankName(String bankName) {
//        this.bankName = bankName;
//    }
//
//    public float getInterestRate() {
//        return interestRate;
//    }
//
//    public void setInterestRate(float interestRate) {
//        this.interestRate = interestRate;
//    }
//
//    public float getAnnuity() {
//        return annuity;
//    }
//
//    public void setAnnuity(float annuity) {
//        this.annuity = annuity;
//    }
}
