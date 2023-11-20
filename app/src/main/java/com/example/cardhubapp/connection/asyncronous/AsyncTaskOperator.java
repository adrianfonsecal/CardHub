package com.example.cardhubapp.connection.asyncronous;

import android.os.AsyncTask;
import android.util.JsonReader;

import com.example.cardhubapp.connection.ApiClient;
import com.example.cardhubapp.connection.HttpConnector;
import com.example.cardhubapp.dataaccess.CreditCardDatabaseAccesor;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsyncTaskOperator extends AsyncTask<Object, Void, JsonArray> {

    public CreditCardDatabaseAccesor activity;
    private String endpointUrl;
    private JsonArray jsonResponse;
    public InterfaceAsyncResponse delegate = null;
    public AsyncTaskOperator(String endpointUrl, InterfaceAsyncResponse card) {
        setEndpointUrl(endpointUrl);
        this.delegate = card;
    }
    @Override
    protected JsonArray doInBackground(Object... params) {
        int responseCode = 0;
        JsonArray jsonArray = null;
        HttpURLConnection connection = connectToAPI();
        if (endpointUrl.contains("login")) {
            List<String> keys = Arrays.asList("email", "password");
            System.out.println("Entro a login");
            Map parameters = buildMap(keys, params);
            createRequestToAPI(connection, parameters);

        } else if (endpointUrl.contains("signup")) {
            List<String> keys = Arrays.asList("name", "email", "password");
            Map parameters = buildMap(keys, params);
            createRequestToAPI(connection, parameters);

        } else if (endpointUrl.contains("get-all-user-cards")) {
            System.out.println("Entro a get all user cards");
            List<String> keys = Arrays.asList("email");
            Map parameters = buildMap(keys, params);
            System.out.println(parameters);
            jsonArray = createRequestToAPI(connection, parameters);


        } else {
            System.out.println("No contiene login");
        }

        return jsonArray;
    }



    private JsonArray createRequestToAPI(HttpURLConnection connection, Map parameters) {
        ApiClient apiClient = new ApiClient(connection, parameters);
        JsonArray jsonResponse = apiClient.createRequest();

        return jsonResponse;
    }


    @Override

    protected void onPostExecute(JsonArray jsonResponse) {
        super.onPostExecute(this.jsonResponse);
        //this.jsonResponse = jsonResponse;
        delegate.processFinish(jsonResponse);

    }

    private Map buildMap(List keys, Object[] values){
        Map<String, String> userData = new HashMap<>();
        for(int i = 0; i < keys.size(); i++){
            userData.put(keys.get(i).toString(), (String)values[i]);
            System.out.println("Clave: " + keys.get(i).toString());
            System.out.println("Valor: " + (String)values[i]);
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

    public JsonArray getJsonResponse() {
        return jsonResponse;
    }
}
