package com.example.cardhubapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.dataaccess.CreditCardDatabaseAccesor;
import com.example.cardhubapp.uielements.CreditCardAdapter;
import com.example.cardhubapp.model.CreditCardProduct;
import com.example.cardhubapp.notification.ErrorMessageNotificator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class AddCardController extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allowSyncronousOperations();
        setContentView(R.layout.add_card);
        setDaysForDaysSpinner();
        setCreditCardProductsForCreditCardSpinner();
        Button addCardBtn = findViewById(R.id.saveCreditCardBtn);
        addCardBtn.setOnClickListener(this);

        Spinner creditCardProductsSpinner = findViewById(R.id.creditCardProductSpinner);
        setListenerForCreditCardProductSpinner(creditCardProductsSpinner);

        Spinner cutOffDaysSpinner = findViewById(R.id.selectCutOffDaySpinner);
        setListenerDaysSpinner(cutOffDaysSpinner);

        Spinner paymentDaysSpinner = findViewById(R.id.selectPaymentDaySpinner);
        setListenerDaysSpinner(paymentDaysSpinner);
    }



    @Override
    public void onClick(View view) {
        Spinner creditCardProductsSpinner = findViewById(R.id.creditCardProductSpinner);
        Spinner paymentDaysSpinner = findViewById(R.id.selectPaymentDaySpinner);
        Spinner cutOffDaysSpinner = findViewById(R.id.selectCutOffDaySpinner);
        EditText currentDebtDecimalField = findViewById(R.id.currentDebtDecimalField);
        EditText paymentForNoInterestDecimalField = findViewById(R.id.paymentForNoInterestDecimalField);
        if (view.getId() == R.id.saveCreditCardBtn) {
            Integer selectedCreditCardProduct = getSelectedItemInSpinner(creditCardProductsSpinner);
            System.out.println("tarjeta es: " + selectedCreditCardProduct);
            String userEmail = getUserEmailFromPreviousIntent();
            saveCreditCard(userEmail, selectedCreditCardProduct);


//            String selectedPaymentDay = getSelectedItemInSpinner(paymentDaysSpinner);
//            String selectedCutOffDay = getSelectedItemInSpinner(cutOffDaysSpinner);

//            String currentDebtText = currentDebtDecimalField.getText().toString();
//            Integer currentDebt = Integer.valueOf(currentDebtText);
//
//            String paymentForNoInterestText = paymentForNoInterestDecimalField.getText().toString();
//            Integer paymentForNoInterest = Integer.valueOf(paymentForNoInterestText);

            //saveCreditCard(selectedCreditCardProduct);
            startHomeView(userEmail);
        }
    }

    private Integer getSelectedItemInSpinner(Spinner spinner) {
        CreditCardProduct selectedItem = (CreditCardProduct) spinner.getSelectedItem();
        if (selectedItem != null) {
            Integer selectedCardId = selectedItem.getCardId();
            System.out.println("Seleccionado elemento: " + selectedItem);
            return selectedCardId;

        }else{
            return null;
        }
    }


    private void saveCreditCard(String userEmail, Integer cardId) {
        CreditCardDatabaseAccesor creditCardDatabaseAccesor = new CreditCardDatabaseAccesor();
        creditCardDatabaseAccesor.addCardToUserCardHolder(userEmail, cardId.toString());
        System.out.println("Se salvo la tarjeta: " + cardId);
    }


    private void setListenerForCreditCardProductSpinner(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                CreditCardProduct selectedCreditCard = (CreditCardProduct) parentView.getItemAtPosition(position);
                int selectedCardId = selectedCreditCard.getCardId();
                System.out.println("El elemento seleccionado fue: " + selectedCardId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                ErrorMessageNotificator.showShortMessage(AddCardController.this, "a");
            }
        });
    }

    private void setListenerDaysSpinner(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Integer salectedDay = (Integer)parentView.getItemAtPosition(position);
                System.out.println("El elemento seleccionado fue: " + salectedDay);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                String errorMessage = "Por favor no deje vacío ningún elemento";
                ErrorMessageNotificator.showShortMessage(AddCardController.this, errorMessage);
            }
        });
    }

    private void setCreditCardProductsForCreditCardSpinner() {
        CreditCardDatabaseAccesor creditCardDatabaseAccesor = new CreditCardDatabaseAccesor();
        JsonArray creditCardProducts = creditCardDatabaseAccesor.getAllCreditCardProducts();

        try {
            ArrayList<CreditCardProduct> cardNames = new ArrayList<>();

            for (int i = 0; i < creditCardProducts.size(); i++) {
                JsonObject jsonObject = creditCardProducts.get(i).getAsJsonObject();
                String cardName = jsonObject.get("card_name").getAsString();
                Integer cardId = jsonObject.get("card_id").getAsInt();
                CreditCardProduct creditCardProduct = new CreditCardProduct(cardId, cardName);
                System.out.println("Agregando el cardId al Spinner: " + cardId);
                cardNames.add(creditCardProduct);
            }

            Spinner creditCardProductSpinner = findViewById(R.id.creditCardProductSpinner);
            CreditCardAdapter adapter = new CreditCardAdapter(this, cardNames);
            creditCardProductSpinner.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDaysForDaysSpinner() {
        Spinner cutOffDaysSpinner = findViewById(R.id.selectCutOffDaySpinner);
        Spinner paymentDaySpinner = findViewById(R.id.selectPaymentDaySpinner);

        Integer[] daysOfMonth = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};

        ArrayAdapter<Integer> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfMonth);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cutOffDaysSpinner.setAdapter(adaptador);
        paymentDaySpinner.setAdapter(adaptador);
    }


    private void allowSyncronousOperations() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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

    private void startHomeView(String userEmail) {
        Intent intent = new Intent(this, HomeController.class);
        intent.putExtra("USER_EMAIL", userEmail);
        startActivity(intent);
    }
}
