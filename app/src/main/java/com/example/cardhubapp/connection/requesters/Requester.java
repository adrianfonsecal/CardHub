package com.example.cardhubapp.connection.requesters;

import com.example.cardhubapp.connection.HttpConnector;
import com.example.cardhubapp.connection.RequestSender;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Requester {

    private String endpointUrl;
    private ArrayList requestParameters;

    public Requester(ArrayList requestParameters){
        this.requestParameters = requestParameters;
    }
    public Requester(){}
    public abstract JsonArray executeRequest();

    protected HttpURLConnection connectToAPI(String endpointUrl){
        HttpConnector httpConnector = new HttpConnector(endpointUrl);
        HttpURLConnection connection;
        try {
            connection = httpConnector.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    protected JsonArray createRequestToAPI(HttpURLConnection connection, String requestParameters) {
        RequestSender apiClient = new RequestSender(connection, requestParameters);
        JsonArray jsonResponse = apiClient.sendRequestToAPI();

        return jsonResponse;
    }

    protected JsonArray createRequestToAPI(HttpURLConnection connection) {
        RequestSender apiClient = new RequestSender(connection);
        JsonArray jsonResponse = apiClient.sendRequestToAPI();

        return jsonResponse;
    }

    protected Map buildMap(List keys, ArrayList values){
        Map<String, String> userData = new HashMap<>();
        for(int i = 0; i < keys.size(); i++){
            userData.put(keys.get(i).toString(), (String)values.get(i));
        }
        return userData;
    }

    protected String convertMapToJsonString(Map<String, String> requestDataMap){
        String jsonLoginData = new Gson().toJson(requestDataMap);
        System.out.println(jsonLoginData);
        return jsonLoginData;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public ArrayList getrequestParameters() {
        return requestParameters;
    }

    public void setrequestParameters(ArrayList requestParameters) {
        this.requestParameters = requestParameters;
    }
}
