package com.example.cardhubapp.connection;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;

public class RequestSender {
    private HttpURLConnection connection;
    private String requestParameters;
    public RequestSender(HttpURLConnection connection, String parameters){
        setConnection(connection);
        setrequestParameters(parameters);
    }
    public RequestSender(HttpURLConnection connection){
        setConnection(connection);
    }
    public JsonArray sendRequestToAPI(){
        JsonArray jsonResponseData = null;
        try {
            prepareConnection("POST");
            if(this.requestParameters != null){
                sendRequest(requestParameters);
            }else{
                sendRequest();
            }
            jsonResponseData = getResponseData();
            verifyResponseCode();
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
                response.append(responseLine.trim());
            }

            jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();

            System.out.println(jsonArray.size());
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }

    private void verifyResponseCode(){
        try {
            Integer responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
            } else {
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void sendRequest(String jsonData) throws IOException, IOException {
        OutputStream outputStream = connection.getOutputStream();
        writeToOutputStream(outputStream, jsonData);
        System.out.println("entro a send data" + connection);
    }

    private void sendRequest() throws IOException, IOException {
        OutputStream outputStream = connection.getOutputStream();
        System.out.println("entro a send data" + connection);
    }
    private void writeToOutputStream(OutputStream outputStream, String data) throws IOException {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
            outputStreamWriter.write(data);
            outputStreamWriter.flush();
        }
    }

    private void prepareConnection(String httpMethod) throws ProtocolException {
        connection.setRequestMethod(httpMethod);
        connection.setDoOutput(true);
    }

    public void setrequestParameters(String requestParameters) {
        this.requestParameters = requestParameters;
    }

    public String getrequestParameters() {
        return requestParameters;
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

}