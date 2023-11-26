package com.example.cardhubapp.connection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnector {
    private String endpointURLToConnect;
    public HttpConnector(String url){
        setEndpointUrl(url);
    }
    public HttpURLConnection openConnection() throws IOException {
        URL url = new URL(this.endpointURLToConnect);
        return (HttpURLConnection) url.openConnection();
    }
    private void setEndpointUrl(String url) {
        this.endpointURLToConnect = url;
    }
}
