package com.example.cardhubapp.connection.asyncronous;

import android.os.AsyncTask;

import com.example.cardhubapp.connection.ApiClient;
import com.example.cardhubapp.connection.HttpConnector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsyncTaskOperator extends AsyncTask<Object, Void, Integer> {
    private String endpointUrl;
    public AsyncTaskOperator(String endpointUrl) {
        setEndpointUrl(endpointUrl);
    }
    @Override
    protected Integer doInBackground(Object... params) {
        int responseCode = 0;
        HttpURLConnection connection = connectToAPI();
        if (endpointUrl.contains("login")) {
            List<String> keys = Arrays.asList("email", "password");
            Map parameters = buildMap(keys, params);
            createRequestToAPI(connection, parameters);

        } else if (endpointUrl.contains("signup")) {
            List<String> keys = Arrays.asList("name", "email", "password");
            Map parameters = buildMap(keys, params);
            createRequestToAPI(connection, parameters);

        } else {
            System.out.println("No contiene login");
        }

        return responseCode;
    }

    private void createRequestToAPI(HttpURLConnection connection, Map parameters) {
        ApiClient apiClient = new ApiClient(connection, parameters);
        apiClient.createRequest();
    }


    @Override
    protected void onPostExecute(Integer responseCode) {
        if (responseCode == HttpURLConnection.HTTP_OK) {
        } else {
        }
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
}