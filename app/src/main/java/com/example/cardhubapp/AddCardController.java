package com.example.cardhubapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.dataaccess.AccountStatementDatabaseAccesor;
import com.example.cardhubapp.dataaccess.CreditCardDatabaseAccesor;
import com.example.cardhubapp.model.AccountStatement;
import com.example.cardhubapp.model.Date;
import com.example.cardhubapp.uielements.CreditCardAdapter;
import com.example.cardhubapp.model.CreditCardProduct;
import com.example.cardhubapp.notification.ErrorMessageNotificator;
import com.example.cardhubapp.uielements.DateSelectionListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;

public class AddCardController extends AppCompatActivity implements View.OnClickListener, DateSelectionListener {

    private String selectedCutOffDate;
    private String selectedPaymentDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_card);
        allowSyncronousOperations();
        setCreditCardProductsForCreditCardSpinner();
        setOnClickListenersToButtons();
        setSpinnersOnClickListeners();

    }

    private void showCalendarDialog(View view, String dateType) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddCardController.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                String selectedDate = year + "-" + month + "-" + dayOfMonth;
                notifyDateSelected(selectedDate, dateType);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void notifyDateSelected(String selectedDate, String dateType) {
        if (this instanceof DateSelectionListener) {
            ((DateSelectionListener) this).onDateSelected(selectedDate, dateType);
        }
    }


    @Override
    public void onClick(View view) {
        Spinner creditCardProductsSpinner = findViewById(R.id.creditCardProductSpinner);
        EditText currentDebtDecimalField = findViewById(R.id.currentDebtDecimalField);
        EditText paymentForNoInterestDecimalField = findViewById(R.id.paymentForNoInterestDecimalField);

        if(view.getId() == R.id.saveCreditCardBtn) {
            saveCreditCardAndAccountStatement(creditCardProductsSpinner, currentDebtDecimalField, paymentForNoInterestDecimalField);
        }
        if(view.getId() == R.id.paymenDateCalendarOpenerBtn) {
            showCalendarDialog(view, "paymentDate");
        }
        if(view.getId() == R.id.cutOffDateCalendarOpenerBtn) {
            showCalendarDialog(view, "cutOffDate");
        }

    }

    private void saveCreditCardAndAccountStatement(Spinner creditCardProductsSpinner, EditText currentDebtDecimalField, EditText paymentForNoInterestDecimalField) {
        Float currentDebt = getFloatFromEditText(currentDebtDecimalField);
        Float paymentForNoInterest = getFloatFromEditText(paymentForNoInterestDecimalField);
        
        if(allFieldsAreValid(currentDebt, paymentForNoInterest)){
            Integer selectedCreditCardProduct = getSelectedCreditCardProductInSpinner(creditCardProductsSpinner);
            String userEmail = getUserEmailFromPreviousIntent();
            JsonArray savedCreditCardResponse = saveCreditCard(userEmail, selectedCreditCardProduct);
            System.out.println("El SAVEDCREDITCARDRESPONSE ES: " + savedCreditCardResponse);
            String todayDate = Date.getCurrentDate();
            AccountStatement firstAccountStatementOfUser = new AccountStatement(this.selectedCutOffDate, this.selectedPaymentDate, currentDebt, paymentForNoInterest);
            Integer cardholderCardId = getCardholderCardIdFromResponse(savedCreditCardResponse);
            System.out.println("El cardholderID es: " + cardholderCardId);
            saveFirstAccountStatement(firstAccountStatementOfUser, todayDate, cardholderCardId);
            startHomeView(userEmail);
        }else{
            String message = "Por favor llene todos los campos";
            ErrorMessageNotificator.showShortMessage(this, message);
        }
    }

    private boolean allFieldsAreValid(Float currentDebt, Float paymentForNoInterest) {
        boolean isCurrentDebtValid = currentDebt != null;
        boolean isPaymentForNoInterestValid = paymentForNoInterest != null;
        boolean isCutOffDateValid = this.selectedCutOffDate != null && !this.selectedCutOffDate.equals("");
        boolean isPaymentDateValid = this.selectedPaymentDate != null && !this.selectedPaymentDate.equals("");

        return (isCurrentDebtValid && isPaymentForNoInterestValid && isCutOffDateValid && isPaymentDateValid);
    }

    private Float getFloatFromEditText(EditText editText) {
        String editTextText = editText.getText().toString();
        if (editTextText.isEmpty()) {
            return 0.0f;
        }
        return Float.valueOf(editTextText);
    }

    private Integer getCardholderCardIdFromResponse(JsonArray response) {
        JsonObject jsonObject = response.get(0).getAsJsonObject();
        System.out.println("EL OBJETO JSON ES: " + jsonObject);
        Integer cardholderCardId = jsonObject.get("cardholder_card_id").getAsInt();
        System.out.println("el cardholder cardid en controller es: " + cardholderCardId);
        return cardholderCardId;
    }

    private Integer getCardholderCardIdFromPreviousIntent() {
        Intent intent = getIntent();
        Integer cardholderCardId = null;
        if (intent != null) {
            cardholderCardId = intent.getIntExtra("cardholderCardId", 0);
            return cardholderCardId;
        }else{
            return null;
        }
    }

    private void saveFirstAccountStatement(AccountStatement firstAccountStatementOfUser, String todayDate, Integer cardholderCardId) {
        AccountStatementDatabaseAccesor accountStatementDatabaseAccesor = new AccountStatementDatabaseAccesor();
        accountStatementDatabaseAccesor.saveAccountStatement(firstAccountStatementOfUser, todayDate, cardholderCardId);

    }

    private boolean isCalendarButtonClicked(View view){
        return view.getId() == R.id.cutOffDateCalendarOpenerBtn || view.getId() == R.id.paymenDateCalendarOpenerBtn;
    }

    private Integer getSelectedCreditCardProductInSpinner(Spinner spinner) {
        CreditCardProduct selectedItem = (CreditCardProduct) spinner.getSelectedItem();
        if (selectedItem != null) {
            Integer selectedCardId = selectedItem.getCardId();
            System.out.println("Seleccionado elemento: " + selectedItem);
            return selectedCardId;

        }else{
            return null;
        }
    }


    private JsonArray saveCreditCard(String userEmail, Integer cardId) {
        CreditCardDatabaseAccesor creditCardDatabaseAccesor = new CreditCardDatabaseAccesor();
        JsonArray response = creditCardDatabaseAccesor.addCardToUserCardHolder(userEmail, cardId.toString());
        System.out.println("Se salvo la tarjeta: " + cardId);
        return response;
    }

    private void setListenerForCreditCardProductSpinner(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                handleItemSelected(parentView, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                handleNothingSelected();
            }
        });
    }

    private void handleItemSelected(AdapterView<?> parentView, int position) {
        CreditCardProduct selectedCreditCard = (CreditCardProduct) parentView.getItemAtPosition(position);
        int selectedCardId = selectedCreditCard.getCardId();
        logSelectedCardId(selectedCardId);
    }

    private void handleNothingSelected() {
        ErrorMessageNotificator.showShortMessage(AddCardController.this, "Nada seleccionado");
    }

    private void logSelectedCardId(int selectedCardId) {
        Log.d("AddCardController", "El elemento seleccionado fue: " + selectedCardId);
    }


    private void setCreditCardProductsForCreditCardSpinner() {
        CreditCardDatabaseAccesor creditCardDatabaseAccesor = new CreditCardDatabaseAccesor();
        JsonArray creditCardProducts = creditCardDatabaseAccesor.getAllCreditCardProducts();

        try {
            ArrayList<CreditCardProduct> cardNames = new ArrayList<>();

            for (int i = 0; i < creditCardProducts.size(); i++) {
                JsonObject jsonObject = creditCardProducts.get(i).getAsJsonObject();
                Integer cardId = jsonObject.get("card_id").getAsInt();
                String cardName = jsonObject.get("card_name").getAsString();
                String bankName = jsonObject.get("bank_name").getAsString();
                float interestRate = jsonObject.get("interest_rate").getAsFloat();
                float annuity = jsonObject.get("annuity").getAsFloat();
                CreditCardProduct creditCardProduct = new CreditCardProduct(cardId, cardName, bankName, interestRate, annuity);
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

    private void setSpinnersOnClickListeners() {
        Spinner creditCardProductsSpinner = findViewById(R.id.creditCardProductSpinner);
        setListenerForCreditCardProductSpinner(creditCardProductsSpinner);
    }

    private void setOnClickListenersToButtons() {
        Button addCardBtn = findViewById(R.id.saveCreditCardBtn);
        addCardBtn.setOnClickListener(this);
        Button cutOffDateCalendarOpenerBtn = findViewById(R.id.cutOffDateCalendarOpenerBtn);
        cutOffDateCalendarOpenerBtn.setOnClickListener(this);
        Button paymenDateCalendarOpenerBtn = findViewById(R.id.paymenDateCalendarOpenerBtn);
        paymenDateCalendarOpenerBtn.setOnClickListener(this);
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

    @Override
    public void onDateSelected(String selectedDate, String dateType) {
        if (dateType.equals("cutOffDate")) {
            this.selectedCutOffDate = selectedDate;
            TextView selectedCutOffDateTextView = findViewById(R.id.selectedCutOffDateTextView);
            selectedCutOffDateTextView.setText(this.selectedCutOffDate);
        } else if (dateType.equals("paymentDate")) {
            this.selectedPaymentDate = selectedDate;
            TextView selectedPaymentDateTextView = findViewById(R.id.selectedPaymentDateTextView);
            selectedPaymentDateTextView.setText(this.selectedPaymentDate);
        }else{
            System.out.println("Error, only cut off and payment date are allowed");
        }

    }

}
