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

import com.example.cardhubapp.connection.requesters.creditcardrequester.GetAllUserCardsRequester;
import com.example.cardhubapp.model.CreditCardProduct;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeController extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        allowSyncronousOperations();
        setOnClickListenersToButtons();
        String userEmail = getUserEmailFromPreviousIntent();
        JsonArray creditCards = getAllUserCards(userEmail);
        setUserCreditCardsInView(creditCards);
    }

    @Override
    public void onClick(View view) {
        if (userClickedAddCardButton(view)) {
            startAddCardView(getUserEmailFromPreviousIntent());
        }
    }

    private void setOnClickListenersToButtons() {
        ImageButton addCardBtn = findViewById(R.id.addCardBtn);
        addCardBtn.setOnClickListener(this);
    }
    private void setUserCreditCardsInView(JsonArray creditCards) {
        LinearLayout containerLayout = findViewById(R.id.containerLayout);
        List<CreditCardProduct> creditCardsList = new ArrayList<>();
        System.out.println("Los credit cards en inflate es: " + creditCards);

        for (int i = 0; i < creditCards.size(); i++) {
            JsonObject creditCardJsonObject = creditCards.get(i).getAsJsonObject();
            JsonObject creditCardJsonInfo = creditCardJsonObject.getAsJsonObject("card");

            CreditCardProduct creditCardProduct = createCreditCardProduct(creditCardJsonInfo);
            creditCardsList.add(creditCardProduct);

            View creditCardView = createCreditCardView(creditCardProduct, creditCardJsonObject);
            containerLayout.addView(creditCardView);
        }
    }

    private View createCreditCardView(CreditCardProduct creditCardProduct, JsonObject creditCardJsonObject) {
        View creditCardView = LayoutInflater.from(this).inflate(R.layout.card_element, null);

        TextView cardNameTextView = creditCardView.findViewById(R.id.cardNameTextView);
        TextView bankNameTextView = creditCardView.findViewById(R.id.bankNameTextView);

        cardNameTextView.setText(creditCardProduct.getName());
        bankNameTextView.setText(creditCardProduct.getBankName());

        creditCardView.setOnClickListener(view -> startAccountStatementView(creditCardProduct, creditCardJsonObject));

        return creditCardView;
    }

    private void startAccountStatementView(CreditCardProduct creditCardProduct, JsonObject creditCardJsonObject) {
        Intent intent = new Intent(HomeController.this, AccountStatementController.class);
        intent.putExtra("cardId", creditCardProduct.getCardId().toString());
        intent.putExtra("cardName", creditCardProduct.getName());
        intent.putExtra("bankName", creditCardProduct.getBankName());
        intent.putExtra("interestRate", String.valueOf(creditCardProduct.getInterestRate()));
        intent.putExtra("cardholderCardId", getCardholderIdFromCreditCard(creditCardJsonObject).toString());
        intent.putExtra("userEmail", getUserEmailFromPreviousIntent());
        startActivity(intent);
    }

    private Integer getCardholderIdFromCreditCard(JsonObject creditCardJsonObject) {
        JsonObject cardHolderCardObject = creditCardJsonObject.getAsJsonObject("card_holder_card");
        Integer cardHolderCardsId = cardHolderCardObject.get("card_holder_cards_id").getAsInt();
        System.out.println("El cardholder_cards_id es: " + cardHolderCardsId);
        return cardHolderCardsId;
    }

    private CreditCardProduct createCreditCardProduct(JsonObject creditCardJsonInfo) {
        System.out.println("El credit cardJSON info es: " + creditCardJsonInfo);
        Integer cardId = creditCardJsonInfo.get("card_id").getAsInt();
        String cardName = creditCardJsonInfo.get("card_name").getAsString();
        String bankName = creditCardJsonInfo.get("bank_name").getAsString();
        float annuity = creditCardJsonInfo.get("annuity").getAsFloat();
        float interestRate = creditCardJsonInfo.get("interest_rate").getAsFloat();

        CreditCardProduct creditCardProduct = new CreditCardProduct(cardId, cardName, bankName, interestRate, annuity);
        return creditCardProduct;
    }
    private JsonArray getAllUserCards(String userEmail){
        ArrayList queryParameters = new ArrayList(Arrays.asList(userEmail));
        GetAllUserCardsRequester getAllUserCardsRequester = new GetAllUserCardsRequester(queryParameters);
        JsonArray creditCards = getAllUserCardsRequester.executeRequest();
        System.out.println("El credit cards en getAllUserCards es: " + creditCards);
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

    private void startAddCardView(String userEmail) {
        Intent intent = new Intent(this, AddCardController.class);
        intent.putExtra("USER_EMAIL", userEmail);
        startActivity(intent);
    }
    private void allowSyncronousOperations() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private boolean userClickedAddCardButton(View view) {
        return view.getId() == R.id.addCardBtn;
    }

}

