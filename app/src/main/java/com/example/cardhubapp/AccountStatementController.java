package com.example.cardhubapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.dataaccess.AccountStatementDatabaseAccesor;
import com.example.cardhubapp.dataaccess.CreditCardDatabaseAccesor;
import com.example.cardhubapp.model.AccountStatement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class AccountStatementController extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_statement);
        allowSyncronousOperations();
        setValuesToCreditCardProductFields();
        setValuesToAccountStatementFields();
        setButtonsOnClickListeners();

    }

    private void setValuesToAccountStatementFields() {
        AccountStatementDatabaseAccesor accountStatementDatabaseAccesor = new AccountStatementDatabaseAccesor();
        String cardholderCardId = getDataFromPreviousIntent("cardholderCardId");

        Integer cardholderCardIdInteger = Integer.valueOf(cardholderCardId);
        JsonArray accountStatementJsonArray = accountStatementDatabaseAccesor.getLastAccountStatementOfCreditCard(cardholderCardIdInteger);

        AccountStatement accountStatement = createAccountStatementFromJsonArray(accountStatementJsonArray);


        TextView cutOffDateTextView = findViewById(R.id.accountStatementCutOffDateTextView);
        cutOffDateTextView.setText(accountStatement.getCutOffDate());

        TextView paymentDateTextView = findViewById(R.id.accountStatementPaymentDateTextView);
        paymentDateTextView.setText(accountStatement.getPaymentDate());

        TextView currentDebtTextView = findViewById(R.id.accountStatementCurrentDebtTextView);
        currentDebtTextView.setText(accountStatement.getCurrentDebt().toString());

        TextView paymentForNoInterestTextView = findViewById(R.id.accountStatementPaymentForNoInterestTextView);
        paymentForNoInterestTextView.setText(accountStatement.getPaymentForNoInterest().toString());
    }

    private AccountStatement createAccountStatementFromJsonArray(JsonArray accountStatementJsonArray) {
        AccountStatement accountStatement = null;
        System.out.println("El accountstatement json array en controller es: " + accountStatementJsonArray);
        for (int i = 0; i < accountStatementJsonArray.size(); i++) {
            JsonObject jsonObject = accountStatementJsonArray.get(i).getAsJsonObject();

            int statementId = jsonObject.get("statement_id").getAsInt();
            String date = jsonObject.get("date").getAsString();
            String cutOffDate = jsonObject.get("cut_off_date").getAsString();
            String paymentDate = jsonObject.get("payment_date").getAsString();
            float currentDebt = jsonObject.get("current_debt").getAsFloat();
            float paymentForNoInterest = jsonObject.get("payment_for_no_interest").getAsFloat();
            int cardFromCardholder = jsonObject.get("card_from_cardholder").getAsInt();

            accountStatement = new AccountStatement(statementId, cutOffDate, paymentDate, currentDebt, paymentForNoInterest);
            System.out.println(accountStatement.getId());
            System.out.println(accountStatement.getCutOffDate());
            System.out.println(accountStatement.getPaymentForNoInterest());
            System.out.println(accountStatement.getCurrentDebt());
            System.out.println(accountStatement.getPaymentDate());

        }
        return accountStatement;
    }

    private void setButtonsOnClickListeners() {
        ImageButton deleteCardButton = findViewById(R.id.deleteCardBtn);
        deleteCardButton.setOnClickListener(this);

        Button addMonthlyPaymentButton = findViewById(R.id.addMonthlyPatmentBtn);
        addMonthlyPaymentButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(userClickedDeleteCardButton(view)){
            deleteSelectedCreditCard();
            startHomeView();
        }
        if(userClickedAddMonthlyPaymentButton(view)) {
            EditText addPaymentTextField = findViewById(R.id.addPaymentTextField);
            if(textFieldIsNotEmpty(addPaymentTextField)){
                Float periodPayment = Float.valueOf(addPaymentTextField.getText().toString());
                //InterestCalculator
                //TODO
            }
        }

    }

    private void startHomeView() {
        Intent intent = new Intent(this, HomeController.class);
        intent.putExtra("USER_EMAIL", getDataFromPreviousIntent("userEmail"));
        startActivity(intent);
    }

    private void deleteSelectedCreditCard() {
        String cardholderCardId = getDataFromPreviousIntent("cardholderCardId");
        CreditCardDatabaseAccesor creditCardDatabaseAccesor = new CreditCardDatabaseAccesor();
        JsonArray response = creditCardDatabaseAccesor.deleteCardFromCardholder(cardholderCardId);
        System.out.println(response);

    }

    private boolean textFieldIsNotEmpty(EditText addPaymentTextField) {
        return !addPaymentTextField.getText().equals("");
    }


    private boolean userClickedAddMonthlyPaymentButton(View view) {
        return view.getId() == R.id.addMonthlyPatmentBtn;
    }

    private boolean userClickedDeleteCardButton(View view) {
        return view.getId() == R.id.deleteCardBtn;
    }

    private void setValuesToCreditCardProductFields() {
        TextView cardNameTextView = findViewById(R.id.cardNameTextView);
        cardNameTextView.setText(getDataFromPreviousIntent("cardName"));

        TextView bankNameTextView = findViewById(R.id.bankTextView);
        bankNameTextView.setText(getDataFromPreviousIntent("bankName"));

        TextView interestRateTextView = findViewById(R.id.interestRateEditTextView);
        interestRateTextView.setText(String.valueOf(getDataFromPreviousIntent("interestRate")));
    }

    private String getDataFromPreviousIntent(String element){
        Intent intent = getIntent();
        String foundElement = null;
        if (intent != null) {
            foundElement = intent.getStringExtra(element);
            return foundElement;
        }else{
            return null;
        }
    }





    private void allowSyncronousOperations() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
