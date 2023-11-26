package com.example.cardhubapp.dataaccess;

import static android.content.Intent.getIntent;

import android.content.Intent;

import com.example.cardhubapp.connection.asyncronous.AsyncTaskOperator;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Arrays;

public class CreditCardDatabaseAccesor{

    public CreditCardDatabaseAccesor(){}
    public JsonArray getAllCreditCardsFromUser(String email) {
        ArrayList param = new ArrayList(Arrays.asList(email));
        AsyncTaskOperator asyncTaskOperator = new AsyncTaskOperator("http://10.0.2.2:8000/get_all_user_cards/", param);
        asyncTaskOperator.run();
        JsonArray jsonResponse = asyncTaskOperator.getJsonResponse();
        System.out.println("La respuesta Json en DB fue:" + jsonResponse);
        return jsonResponse;
    }

    public JsonArray getAllCreditCardProducts(){
        AsyncTaskOperator asyncTaskOperator = new AsyncTaskOperator("http://10.0.2.2:8000/get_all_cards/");
        asyncTaskOperator.run();
        JsonArray creditCardProducts = asyncTaskOperator.getJsonResponse();
        System.out.println("La respuesta Json en DB fue:" + creditCardProducts);
        return creditCardProducts;
    }

    public JsonArray addCardToUserCardHolder(String userEmail, String cardId){
        ArrayList param = new ArrayList(Arrays.asList(userEmail, cardId));
        AsyncTaskOperator asyncTaskOperator = new AsyncTaskOperator("http://10.0.2.2:8000/add_card_to_user_cardholder/", param);
        asyncTaskOperator.run();
        JsonArray creditCardProducts = asyncTaskOperator.getJsonResponse();
        System.out.println("La respuesta Json en DB fue:" + creditCardProducts);
        return creditCardProducts;
    }

    public JsonArray deleteCardFromCardholder(String cardholderCardId){
        ArrayList param = new ArrayList(Arrays.asList(cardholderCardId));
        AsyncTaskOperator asyncTaskOperator = new AsyncTaskOperator("http://10.0.2.2:8000/remove_card_from_user_cardholder/", param);
        asyncTaskOperator.run();
        JsonArray creditCardProducts = asyncTaskOperator.getJsonResponse();
        System.out.println("La respuesta Json en DB fue:" + creditCardProducts);
        return creditCardProducts;
    }



//

//

}
