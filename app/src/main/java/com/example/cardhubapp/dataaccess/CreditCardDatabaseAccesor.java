package com.example.cardhubapp.dataaccess;

import com.example.cardhubapp.connection.apirequest.CreditCardRequestProcessor;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Arrays;

public class CreditCardDatabaseAccesor{

    public CreditCardDatabaseAccesor(){}
    public JsonArray getAllCreditCardsFromUser(String email) {
        System.out.println("holaaa");
        ArrayList param = new ArrayList(Arrays.asList(email));
        CreditCardRequestProcessor creditCardRequestProcessor = new CreditCardRequestProcessor("http://10.0.2.2:8000/get_all_user_cards/", param);
        JsonArray jsonResponse = creditCardRequestProcessor.executeRequest();
        System.out.println("La respuesta Json en DB fue:" + jsonResponse);

        return jsonResponse;
    }

    public JsonArray getAllCreditCardProducts(){
        CreditCardRequestProcessor creditCardRequestProcessor = new CreditCardRequestProcessor("http://10.0.2.2:8000/get_all_cards/");
        JsonArray jsonResponse = creditCardRequestProcessor.executeRequest();
        System.out.println("La respuesta Json en DB fue:" + jsonResponse);

        return jsonResponse;
    }

    public JsonArray addCardToUserCardHolder(String userEmail, String cardId){
        ArrayList param = new ArrayList(Arrays.asList(userEmail, cardId));
        CreditCardRequestProcessor creditCardRequestProcessor = new CreditCardRequestProcessor("http://10.0.2.2:8000/add_card_to_user_cardholder/", param);
        JsonArray creditCardProducts = creditCardRequestProcessor.executeRequest();
        System.out.println("La respuesta Json en DB fue:" + creditCardProducts);

        return creditCardProducts;
    }

    public JsonArray deleteCardFromCardholder(String cardholderCardId){
        ArrayList param = new ArrayList(Arrays.asList(cardholderCardId));
        CreditCardRequestProcessor creditCardRequestProcessor = new CreditCardRequestProcessor("http://10.0.2.2:8000/remove_card_from_user_cardholder/", param);
        JsonArray creditCardProducts = creditCardRequestProcessor.executeRequest();
        System.out.println("La respuesta Json en DB fue:" + creditCardProducts);

        return creditCardProducts;
    }
}
