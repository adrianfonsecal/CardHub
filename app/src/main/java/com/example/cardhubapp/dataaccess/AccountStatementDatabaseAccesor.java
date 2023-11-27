package com.example.cardhubapp.dataaccess;

import com.example.cardhubapp.connection.apirequest.AccountStatementRequestProcessor;
import com.example.cardhubapp.model.AccountStatement;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Arrays;

public class AccountStatementDatabaseAccesor {
    public AccountStatementDatabaseAccesor(){}

    public JsonArray saveAccountStatement(AccountStatement accountStatement, String accountStatementCreationDate, Integer cardholderCardId){
        ArrayList param = new ArrayList(Arrays.asList(accountStatement.getCutOffDate(), accountStatement.getPaymentDate(), accountStatement.getCurrentDebt().toString(), accountStatement.getPaymentForNoInterest().toString(), accountStatementCreationDate, cardholderCardId.toString()));
        System.out.println("El cardholder cardd id en save CCOUNT es: " + cardholderCardId);
        AccountStatementRequestProcessor accountStatementRequestProcessor = new AccountStatementRequestProcessor("http://10.0.2.2:8000/generate_card_statement/", param);
        System.out.println("Entro a SaveAccountStatement en DBAccesor");
        JsonArray creditCardProducts = accountStatementRequestProcessor.executeRequest();
        System.out.println("La respuesta Json en DB fue:" + creditCardProducts);
        return creditCardProducts;
    }

    public JsonArray getLastAccountStatementOfCreditCard(Integer cardholderCardId){
        ArrayList param = new ArrayList(Arrays.asList(cardholderCardId.toString()));
        AccountStatementRequestProcessor accountStatementRequestProcessor = new AccountStatementRequestProcessor("http://10.0.2.2:8000/get_last_statement/", param);
        System.out.println("Entro a Get Last statement en DBAccesor");
        JsonArray lastAccountStatement = accountStatementRequestProcessor.executeRequest();
        System.out.println("La respuesta Json en DB fue:" + lastAccountStatement);
        return lastAccountStatement;
    }

    public JsonArray getAllAccountStatementsOfCreditCard(Integer cardholderCardId){
        ArrayList param = new ArrayList(Arrays.asList(cardholderCardId.toString()));
        AccountStatementRequestProcessor accountStatementRequestProcessor = new AccountStatementRequestProcessor("http://10.0.2.2:8000/get_all_statements_from_card/", param);
        System.out.println("Entro a Get Last statement en DBAccesor");
        JsonArray lastAccountStatement = accountStatementRequestProcessor.executeRequest();
        System.out.println("La respuesta Json en DB fue:" + lastAccountStatement);
        return lastAccountStatement;
    }
}
