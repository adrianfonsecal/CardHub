package com.example.cardhubapp.connection.requesters.creditcardrequester;

import com.example.cardhubapp.connection.requesters.Requester;
import com.google.gson.JsonArray;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GetAllCardsRequester extends Requester {
   private final String endpointUrl = "http://10.0.2.2:8000/get_all_cards/";

   public GetAllCardsRequester(){super();}

    @Override
    public JsonArray executeRequest() {
        HttpURLConnection connection = connectToAPI(this.endpointUrl);
        return createRequestToAPI(connection);
    }
}
