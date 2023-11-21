package com.example.cardhubapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.dataaccess.CreditCardDatabaseAccesor;
import com.google.gson.JsonArray;

public class HomeController extends AppCompatActivity implements View.OnClickListener{
    Intent intent = getIntent();

//    final String USER_EMAIL = getUserEmailFromPreviousIntent();
//    final String USER_PASSWORD = getUserPasswordFromPreviousIntent();
    private JsonArray response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allowSyncronousOperations();
        setContentView(R.layout.home);
        ImageButton addCardBtn = findViewById(R.id.addCardBtn);
        addCardBtn.setOnClickListener(this);
        inflateElements();


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
        String userEmail = getUserEmailFromPreviousIntent();
        JsonArray creditCards = getAllUserCards(userEmail);

        for (int i = 0; i < creditCards.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout inflatedLayout = (LinearLayout) inflater.inflate(R.layout.card_element, containerLayout, false);
            containerLayout.addView(inflatedLayout);
        }

    }

    private void allowSyncronousOperations() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    private JsonArray getAllUserCards(String userEmail){
        CreditCardDatabaseAccesor creditCardDatabaseAccesor = new CreditCardDatabaseAccesor();
        JsonArray creditCards = creditCardDatabaseAccesor.getAllCreditCardsFromUser(userEmail);
        return creditCards;
    }

    private String getUserEmailFromPreviousIntent() {
        Intent intent = getIntent();
        String userEmail = null;
        if (intent != null) {
            userEmail = intent.getStringExtra("USER_EMAIL");
            return userEmail;
        }else{
            return null;
        }
    }
    private String getUserPasswordFromPreviousIntent() {
        Intent intent = getIntent();
        String userPassword = null;
        if (intent != null) {
            userPassword = intent.getStringExtra("USER_PASSWORD");
            return userPassword;
        }else{
            return null;
        }
    }


}

