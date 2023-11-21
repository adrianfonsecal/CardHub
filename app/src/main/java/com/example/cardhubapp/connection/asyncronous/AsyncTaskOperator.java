package com.example.cardhubapp.connection.asyncronous;

import android.os.AsyncTask;
import android.util.JsonReader;

import com.example.cardhubapp.connection.ApiClient;
import com.example.cardhubapp.connection.HttpConnector;
import com.example.cardhubapp.dataaccess.CreditCardDatabaseAccesor;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsyncTaskOperator implements Runnable {

    private String endpointUrl;


    private JsonArray jsonResponse;

    private ArrayList queryParameters;

    public AsyncTaskOperator(String endpointUrl, ArrayList parameters) {
        setEndpointUrl(endpointUrl);
        setQueryParameters(parameters);
    }


    @Override
    public void run() {
        int responseCode = 0;
        JsonArray jsonArray = null;
        HttpURLConnection connection = connectToAPI();
        if (endpointUrl.contains("login")) {
            List<String> keys = Arrays.asList("email", "password");
            Map parameters = buildMap(keys, queryParameters);
            System.out.println("Los query parameters en login son: " + queryParameters);
            jsonArray = createRequestToAPI(connection, parameters);
            System.out.println("json array " + jsonArray);
            this.jsonResponse = jsonArray;

        } else if (endpointUrl.contains("signup")) {
            List<String> keys = Arrays.asList("name", "email", "password");
            Map parameters = buildMap(keys, queryParameters);
            System.out.println("Los query parameters en signup son: " + queryParameters);
            jsonArray = createRequestToAPI(connection, parameters);
            System.out.println("json array " + jsonArray);
            this.jsonResponse = jsonArray;
        } else if (endpointUrl.contains("get-all-user-cards")) {
            System.out.println("Entro a get all user cards");
            List<String> keys = Arrays.asList("email");
            Map parameters = buildMap(keys, queryParameters);
            System.out.println("Los query parameters en login son: " + queryParameters);
            jsonArray = createRequestToAPI(connection, parameters);
            System.out.println("json array " + jsonArray);
            this.jsonResponse = jsonArray;


        } else {
            System.out.println("No contiene login");
        }

    }

    private JsonArray createRequestToAPI(HttpURLConnection connection, Map parameters) {
        ApiClient apiClient = new ApiClient(connection, parameters);
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

    private void setEndpointUrl(String url) {
        this.endpointUrl = url;
    }

    public ArrayList getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(ArrayList queryParameters) {
        this.queryParameters = queryParameters;
    }

    public JsonArray getJsonResponse() {
        return jsonResponse;
    }



}
