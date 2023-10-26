package com.example.cardhubapp;
import android.os.AsyncTask;
import com.example.cardhubapp.model.User;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class AsyncTaskLogIn extends AsyncTask<String, String, Integer> {
    private String email;
    private String password;

    public AsyncTaskLogIn(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        int responseCode = 0;
        try {
            String endpointURL = "http://10.0.2.2:8000/login/";

            URL url = new URL(endpointURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setDoOutput(true);

            Map<String, String> data = Map.of("email", email, "password", password);
            String jsonData = new Gson().toJson(data);
            System.out.println(jsonData);

            OutputStream os = connection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);

            osw.write(jsonData);
            osw.flush();
            osw.close();

            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // La solicitud se completó correctamente, puedes hacer algo si es necesario
            } else {
                System.out.println("Error en la solicitud. Código de respuesta: " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseCode;
    }

    @Override
    protected void onPostExecute(Integer responseCode) {
        // Esta función se ejecuta en el hilo principal después de doInBackground
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // La solicitud se completó correctamente, puedes actualizar la interfaz de usuario si es necesario
        } else {
            // La solicitud no se completó correctamente, maneja los errores en la interfaz de usuario si es necesario
        }
    }
}
