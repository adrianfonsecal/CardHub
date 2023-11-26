package com.example.cardhubapp.dataaccess;

import com.example.cardhubapp.connection.asyncronous.AsyncTaskOperator;
import com.example.cardhubapp.model.AccountStatement;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Arrays;

public class AccountStatementDatabaseAccesor {
    public AccountStatementDatabaseAccesor(){}

    public JsonArray saveAccountStatement(AccountStatement accountStatement, String accountStatementCreationDate, Integer cardholderCardId){
        ArrayList param = new ArrayList(Arrays.asList(accountStatement.getCutOffDate(), accountStatement.getPaymentDate(), accountStatement.getCurrentDebt().toString(), accountStatement.getPaymentForNoInterest().toString(), accountStatementCreationDate, cardholderCardId.toString()));
        AsyncTaskOperator asyncTaskOperator = new AsyncTaskOperator("http://10.0.2.2:8000/generate_card_statement/", param);
        System.out.println("Entro a SaveAccountStatement en DBAccesor");
        asyncTaskOperator.run();
        JsonArray creditCardProducts = asyncTaskOperator.getJsonResponse();
        System.out.println("La respuesta Json en DB fue:" + creditCardProducts);
        return creditCardProducts;
    }

    public JsonArray getLastAccountStatementOfCreditCard(Integer cardholderCardId){
        ArrayList param = new ArrayList(Arrays.asList(cardholderCardId.toString()));
        AsyncTaskOperator asyncTaskOperator = new AsyncTaskOperator("http://10.0.2.2:8000/get_last_statement/", param);
        System.out.println("Entro a Get Last statement en DBAccesor");
        asyncTaskOperator.run();
        JsonArray lastAccountStatement = asyncTaskOperator.getJsonResponse();
        System.out.println("La respuesta Json en DB fue:" + lastAccountStatement);
        return lastAccountStatement;
    }
}
