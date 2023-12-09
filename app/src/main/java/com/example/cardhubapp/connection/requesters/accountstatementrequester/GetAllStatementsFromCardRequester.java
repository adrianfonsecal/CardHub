package com.example.cardhubapp.connection.requesters.accountstatementrequester;

import com.example.cardhubapp.connection.requesters.Requester;
import com.google.gson.JsonArray;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GetAllStatementsFromCardRequester extends Requester {
    private final String endpointUrl = "http://10.0.2.2:8000/get_all_statements_from_card/";

    public GetAllStatementsFromCardRequester(ArrayList requestParameters) {
        super(requestParameters);
    }

    @Override
    public JsonArray executeRequest() {
        HttpURLConnection connection = connectToAPI(this.endpointUrl);
        List<String> keys = Arrays.asList("cardholder_card_id");
        Map<String, String> parameters = buildMap(keys, getrequestParameters());
        String parametersToSend = convertMapToJsonString(parameters);
        return createRequestToAPI(connection, parametersToSend);
    }
}
