package com.example.cardhubapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.dataaccess.CreditCardDatabaseAccesor;
import com.example.cardhubapp.model.CreditCardProduct;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class HomeController extends AppCompatActivity implements View.OnClickListener{
    Intent intent = getIntent();

//    final String USER_EMAIL = getUserEmailFromPreviousIntent();
//    final String USER_PASSWORD = getUserPasswordFromPreviousIntent();
    private JsonArray response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        allowSyncronousOperations();
        setOnClickListeners();

        String userEmail = getUserEmailFromPreviousIntent();
        JsonArray creditCards = getAllUserCards(userEmail);
        inflateElements(creditCards);
    }

    private void setOnClickListeners() {
        ImageButton addCardBtn = findViewById(R.id.addCardBtn);
        addCardBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addCardBtn) {
            startAddCardView(getUserEmailFromPreviousIntent());
        }
    }

    private void inflateElements(JsonArray creditCards){
        LinearLayout containerLayout = findViewById(R.id.containerLayout);
        List<CreditCardProduct> creditCardsList = new ArrayList<>();
        for (int i = 0; i < creditCards.size(); i++) {
            JsonObject creditCardJsonObject = creditCards.get(i).getAsJsonObject();
            JsonObject creditCardJsonInfo = creditCardJsonObject.getAsJsonObject("card");
            CreditCardProduct creditCardProduct = createCreditCardProduct(creditCardJsonInfo);
            creditCardsList.add(creditCardProduct);

            View creditCardView = LayoutInflater.from(this).inflate(R.layout.card_element, containerLayout, false);

            TextView cardNameTextView = creditCardView.findViewById(R.id.tipoTarjetaTextView);
            TextView bankNameTextView = creditCardView.findViewById(R.id.tituloTextView);

            cardNameTextView.setText(creditCardProduct.getName());
            bankNameTextView.setText(creditCardProduct.getBankName());

            containerLayout.addView(creditCardView);
        }
    }

    private CreditCardProduct createCreditCardProduct(JsonObject creditCardJsonInfo) {
        Integer cardId = creditCardJsonInfo.get("card_id").getAsInt();
        String cardName = creditCardJsonInfo.get("card_name").getAsString();
        String bankName = creditCardJsonInfo.get("bank_name").getAsString();
        float annuity = creditCardJsonInfo.get("annuity").getAsFloat();
        float interestRate = creditCardJsonInfo.get("interest_rate").getAsFloat();
        CreditCardProduct creditCardProduct = new CreditCardProduct(cardId, cardName, bankName, interestRate, annuity);
        return creditCardProduct;
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

    private void startAddCardView(String userEmail) {
        Intent intent = new Intent(this, AddCardController.class);
        intent.putExtra("USER_EMAIL", userEmail);
        startActivity(intent);
    }

}

