package com.example.cardhubapp.connection.apirequest;

import com.example.cardhubapp.connection.RequestSender;
import com.example.cardhubapp.connection.HttpConnector;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountStatementRequestProcessor implements InterfaceApiRequest{
    private String endpointUrl;
    private ArrayList queryParameters;

    public AccountStatementRequestProcessor(String endpointUrl, ArrayList parameters) {
        setEndpointUrl(endpointUrl);
        setQueryParameters(parameters);
    }
    public AccountStatementRequestProcessor(String endpointUrl) {
        setEndpointUrl(endpointUrl);
    }
    @Override
    public JsonArray executeRequest() {
        JsonArray jsonArray = null;
        HttpURLConnection connection = connectToAPI();

        if (endpointUrl.contains("generate_card_statement")) {
            jsonArray = executeGenerateCardStatementRequest(connection);
        } else if (endpointUrl.contains("get_last_statement")) {
            jsonArray = executeGetLastStatementRequest(connection);
        } else if (endpointUrl.contains("get_all_statements_from_card")) {
            jsonArray = executeGetAllStatementsFromCardRequest(connection);
        }  else {
            System.out.println("No contiene login");
        }

        return jsonArray;
    }

    private JsonArray executeGetAllStatementsFromCardRequest(HttpURLConnection connection) {
        List<String> keys = Arrays.asList("cardholder_card_id");
        Map<String, String> parameters = buildMap(keys, queryParameters);
        return createRequestToAPI(connection, parameters);
    }

    private JsonArray executeGenerateCardStatementRequest(HttpURLConnection connection) {
        List<String> keys = Arrays.asList("cut_off_date", "payment_date", "current_debt", "pni", "date", "card_holder_cards_id");
        Map<String, String> parameters = buildMap(keys, queryParameters);
        return createRequestToAPI(connection, parameters);
    }

    private JsonArray executeGetLastStatementRequest(HttpURLConnection connection) {
        List<String> keys = Arrays.asList("cardholder_card_id");
        Map<String, String> parameters = buildMap(keys, queryParameters);
        return createRequestToAPI(connection, parameters);
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

}
