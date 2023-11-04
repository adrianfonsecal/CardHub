package com.example.cardhubapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnector {
    private String endpointURL;
    public HttpConnector(String url){
        setEndpointUrl(url);
    }
    public HttpURLConnection openConnection() throws IOException {
        URL url = new URL(this.endpointURL);
        return (HttpURLConnection) url.openConnection();
    }
    private void setEndpointUrl(String url) {
        this.endpointURL = url;
    }
}
