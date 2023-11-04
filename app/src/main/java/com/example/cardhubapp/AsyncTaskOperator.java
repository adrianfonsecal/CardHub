package com.example.cardhubapp;

import android.os.AsyncTask;
import com.example.cardhubapp.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
        if (endpointUrl.contains("login")) {
            List<String> keys = Arrays.asList("email", "password");
            Map parameters = createHashMap(keys, params);
            HttpURLConnection connection = connectToAPI();
            ApiClient apiClient = new ApiClient(connection, parameters);
            apiClient.createRequest();

        } else if (endpointUrl.contains("signup")) {
            List<String> keys = Arrays.asList("name", "email", "password");
            Map parameters = createHashMap(keys, params);
            HttpURLConnection connection = connectToAPI();
            ApiClient apiClient = new ApiClient(connection, parameters);
            apiClient.createRequest();

        } else {
            System.out.println("No contiene login");
        }

        return responseCode;
    }

    @Override
    protected void onPostExecute(Integer responseCode) {
        if (responseCode == HttpURLConnection.HTTP_OK) {
        } else {
        }
    }

    private Map createHashMap(List keys, Object[] values){
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