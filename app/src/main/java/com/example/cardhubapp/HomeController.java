package com.example.cardhubapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.connection.asyncronous.AsyncTaskOperator;
import com.example.cardhubapp.connection.asyncronous.InterfaceAsyncResponse;
import com.example.cardhubapp.dataaccess.CreditCardDatabaseAccesor;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class HomeController extends AppCompatActivity implements View.OnClickListener, InterfaceAsyncResponse {

    private JsonArray response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        ImageButton addCardBtn = findViewById(R.id.addCardBtn);
        addCardBtn.setOnClickListener(this);
        inflateElements();

    }

    private JsonArray getJsonResponse(){
        AsyncTaskOperator asyncTaskOperator = new AsyncTaskOperator("http://10.0.2.2:8000/get-all-user-cards/", this);
        //asyncTaskOperator.delegate = this;
        String email = "admin";
        asyncTaskOperator.execute(email);
        JsonArray jsonResponse = asyncTaskOperator.getJsonResponse();
        System.out.println("Controlador metodo getJsonResponse es: " + jsonResponse);
        return jsonResponse;
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addCardBtn) {
            Intent intent = new Intent(this, SignUpController.class);
            startActivity(intent);
        }
    }

    private void inflateElements(){
        LinearLayout containerLayout = findViewById(R.id.containerLayout);
        //CreditCardDatabaseAccesor creditCardDatabaseAccesor = new CreditCardDatabaseAccesor();

        try{
            JsonArray jsonArray = getJsonResponse();
            System.out.println("La respuesta en el controlador es: " + jsonArray);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Array en el metodo inflate" + this.response);
            // Ahora inflar los elementos
            for (int i = 0; i < 8; i++) {
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout inflatedLayout = (LinearLayout) inflater.inflate(R.layout.card_element, containerLayout, false);
                containerLayout.addView(inflatedLayout);
            }

    }

    @Override
    public void processFinish(JsonArray output) {

        this.response = output;
        System.out.println("El array en controller es " + this.response);

    }

}

