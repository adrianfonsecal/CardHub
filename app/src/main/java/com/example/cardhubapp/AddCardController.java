package com.example.cardhubapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.connection.requesters.Requester;
import com.example.cardhubapp.connection.requesters.accountstatementrequester.GenerateCardStatementRequester;
import com.example.cardhubapp.connection.requesters.creditcardrequester.AddCardToUserCardholderRequester;
import com.example.cardhubapp.connection.requesters.creditcardrequester.GetAllCardsRequester;
import com.example.cardhubapp.model.AccountStatement;
import com.example.cardhubapp.model.date.DateService;
import com.example.cardhubapp.uielements.CreditCardAdapter;
import com.example.cardhubapp.model.CreditCardProduct;
import com.example.cardhubapp.notification.ErrorMessageNotificator;
import com.example.cardhubapp.uielements.DateSelectionListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public void onClick(View view) {
        Spinner creditCardProductsSpinner = findViewById(R.id.creditCardProductSpinner);
        EditText currentDebtDecimalField = findViewById(R.id.currentDebtDecimalField);
        EditText paymentForNoInterestDecimalField = findViewById(R.id.paymentForNoInterestDecimalField);

        if(userClickedSaveButton(view)) {
            saveCreditCardAndAccountStatement(creditCardProductsSpinner, currentDebtDecimalField, paymentForNoInterestDecimalField);
        }
        if(userClickedPaymentCalendarButton(view)) {
            showCalendarDialog(view, "paymentDate");
        }
        if(userClickedCutOffCalendarButton(view)) {
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

            createAccountStatement(currentDebt, paymentForNoInterest, savedCreditCardResponse);
            startHomeView(userEmail);
        }else{
            String message = "Por favor llene todos los campos";
            ErrorMessageNotificator.showShortMessage(this, message);
        }
    }

    private void createAccountStatement(Float currentDebt, Float paymentForNoInterest, JsonArray savedCreditCardResponse) {
        LocalDate todayDate = DateService.getCurrentDate();
        AccountStatement firstAccountStatementOfUser = new AccountStatement(this.selectedCutOffDate, this.selectedPaymentDate, currentDebt, paymentForNoInterest);
        String cardholderCardId = getCardholderCardIdFromResponse(savedCreditCardResponse).toString();
        saveAccountStatement(firstAccountStatementOfUser, todayDate.toString(), cardholderCardId);
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

    private String getCardholderCardIdFromResponse(JsonArray response) {
        JsonObject jsonObject = response.get(0).getAsJsonObject();
        String cardholderCardId = jsonObject.get("cardholder_card_id").getAsString();
        return cardholderCardId;
    }

    private void saveAccountStatement(AccountStatement firstAccountStatementOfUser, String todayDate, String cardholderCardId) {
        String accountStatementCutOffDate = firstAccountStatementOfUser.getCutOffDate();
        String accountStatementPaymentDate = firstAccountStatementOfUser.getPaymentDate();
        String accountStatementCurrentDebt = firstAccountStatementOfUser.getCurrentDebt().toString();
        String accountStatementPaymentForNoInterest = firstAccountStatementOfUser.getPaymentForNoInterest().toString();

        ArrayList queryParameters = new ArrayList(Arrays.asList(accountStatementCutOffDate, accountStatementPaymentDate, accountStatementCurrentDebt, accountStatementPaymentForNoInterest, todayDate, cardholderCardId));
        Requester cardStatementGenerator = new GenerateCardStatementRequester(queryParameters);
        cardStatementGenerator.executeRequest();
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
        String stringCardId = cardId.toString();
        ArrayList queryParameters = new ArrayList(Arrays.asList(userEmail, stringCardId));
        AddCardToUserCardholderRequester addCardToUserCardholderRequester = new AddCardToUserCardholderRequester(queryParameters);
        JsonArray response = addCardToUserCardholderRequester.executeRequest();
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
    }
    private void handleNothingSelected() {
        ErrorMessageNotificator.showShortMessage(AddCardController.this, "Nada seleccionado");
    }
    private void setCreditCardProductsForCreditCardSpinner() {
        GetAllCardsRequester getAllCardsRequester = new GetAllCardsRequester();
        JsonArray creditCardProducts = getAllCardsRequester.executeRequest();
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

    private static boolean userClickedCutOffCalendarButton(View view) {
        return view.getId() == R.id.cutOffDateCalendarOpenerBtn;
    }

    private static boolean userClickedPaymentCalendarButton(View view) {
        return view.getId() == R.id.paymenDateCalendarOpenerBtn;
    }

    private static boolean userClickedSaveButton(View view) {
        return view.getId() == R.id.saveCreditCardBtn;
    }

}
