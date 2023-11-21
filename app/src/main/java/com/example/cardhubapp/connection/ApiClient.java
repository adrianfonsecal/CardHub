package com.example.cardhubapp.connection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public JsonArray createRequest(){
        int responseCode = 0;
        JsonArray jsonResponseData = null;
        try {
            prepareConnection();
            String jsonLoginData = convertMapToJson(parameters);
            System.out.println("api client el json para envar es: " + jsonLoginData);
            sendData(jsonLoginData);
            jsonResponseData = getResponseData();

            responseCode = connection.getResponseCode();
            verifyResponseCode(responseCode);
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResponseData;
    }

    private JsonArray getResponseData() {
        JsonArray jsonArray = null;
        try  {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                System.out.println("responseLine: " + responseLine);
                response.append(responseLine.trim());
            }

            jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();

            System.out.println(jsonArray.size());
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Respuesta del servidor ApiClient: " + jsonArray);
        return jsonArray;
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
        System.out.println("entro a send data" + connection);
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