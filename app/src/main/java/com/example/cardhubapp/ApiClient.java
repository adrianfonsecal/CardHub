package com.example.cardhubapp;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Map;

public class ApiClient {
    private HttpURLConnection connection;
    private Map parameters;
    public ApiClient(HttpURLConnection connection, Map parameters){
        setConnection(connection);
        setParameters(parameters);
    }
    public Integer createRequest(){
        int responseCode = 0;
        try {
            prepareConnection();
            String jsonLoginData = convertMapToJson(parameters);
            sendData(jsonLoginData);
            responseCode = connection.getResponseCode();
            verifyResponseCode(responseCode);
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseCode;
    }
    private void verifyResponseCode(int responseCode){
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Connection was made succesfully");
        } else {
            System.out.println("Request error, code: " + responseCode);
        }
    }
    private void sendData(String jsonData) throws IOException, IOException {
        OutputStream outputStream = connection.getOutputStream();
        writeToOutputStream(outputStream, jsonData);
    }
    private void writeToOutputStream(OutputStream outputStream, String data) throws IOException {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
            outputStreamWriter.write(data);
            outputStreamWriter.flush();
        }
    }
    private String convertMapToJson(Map<String, String> loginData){
        String jsonLoginData = new Gson().toJson(loginData);
        System.out.println(jsonLoginData);
        return jsonLoginData;
    }
    private void prepareConnection() throws ProtocolException {
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
    }

    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    public Map getParameters() {
        return parameters;
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

}
