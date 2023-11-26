package com.example.cardhubapp.connection.apirequest;

import com.example.cardhubapp.connection.HttpConnector;
import com.example.cardhubapp.connection.RequestSender;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAccessApiRequestProcessor implements InterfaceApiRequest{

    private String endpointUrl;
    private ArrayList queryParameters;

    public UserAccessApiRequestProcessor(String endpointUrl, ArrayList parameters) {
        setEndpointUrl(endpointUrl);
        setQueryParameters(parameters);
    }
    public UserAccessApiRequestProcessor(String endpointUrl) {
        setEndpointUrl(endpointUrl);
    }
    @Override
    public JsonArray executeRequest() {
        JsonArray jsonArray = null;
        HttpURLConnection connection = connectToAPI();
        if (endpointUrl.contains("login")) {
            jsonArray = executeLoginRequest(connection);
        } else if (endpointUrl.contains("signup")) {
            jsonArray = executeSignupRequest(connection);
        }
        return jsonArray;
    }

    private JsonArray executeLoginRequest(HttpURLConnection connection) {
        List<String> keys = Arrays.asList("email", "password");
        Map<String, String> parameters = buildMap(keys, queryParameters);
        return createRequestToAPI(connection, parameters);
    }

    private JsonArray executeSignupRequest(HttpURLConnection connection) {
        List<String> keys = Arrays.asList("name", "email", "password");
        Map<String, String> parameters = buildMap(keys, queryParameters);
        return createRequestToAPI(connection, parameters);
    }

    private HttpURLConnection connectToAPI(){
        HttpConnector httpConnector = new HttpConnector(this.endpointUrl);
        HttpURLConnection connection;
        try {
            connection = httpConnector.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private JsonArray createRequestToAPI(HttpURLConnection connection, Map parameters) {
        RequestSender apiClient = new RequestSender(connection, parameters);
        JsonArray jsonResponse = apiClient.createRequest();
        System.out.println("La respuesta es: " + jsonResponse);

        return jsonResponse;
    }

    private Map buildMap(List keys, ArrayList values){
        Map<String, String> userData = new HashMap<>();
        for(int i = 0; i < keys.size(); i++){
            userData.put(keys.get(i).toString(), (String)values.get(i));
            System.out.println("Clave: " + keys.get(i).toString());
            System.out.println("Valor: " + (String)values.get(i));
        }
        return userData;
    }

    private void setEndpointUrl(String url) {
        this.endpointUrl = url;
    }

    public ArrayList getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(ArrayList queryParameters) {
        this.queryParameters = queryParameters;
    }
}
