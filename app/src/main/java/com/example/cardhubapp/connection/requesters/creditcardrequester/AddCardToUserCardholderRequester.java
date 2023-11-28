package com.example.cardhubapp.connection.requesters.creditcardrequester;

import com.example.cardhubapp.connection.requesters.Requester;
import com.google.gson.JsonArray;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddCardToUserCardholderRequester extends Requester {
    private final String endpointUrl = "http://10.0.2.2:8000/add_card_to_user_cardholder/";

    public AddCardToUserCardholderRequester(ArrayList queryParameters) {
        super(queryParameters);
    }

    @Override
    public JsonArray executeRequest() {
        HttpURLConnection connection = connectToAPI(this.endpointUrl);
        List<String> keys = Arrays.asList("email", "card_id");
        Map<String, String> parameters = buildMap(keys, getQueryParameters());
        String parametersToSend = convertMapToJson(parameters);
        return createRequestToAPI(connection, parametersToSend);
    }
}
