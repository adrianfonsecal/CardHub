package com.example.cardhubapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.connection.requesters.Requester;
import com.example.cardhubapp.connection.requesters.accountstatementrequester.GenerateCardStatementRequester;
import com.example.cardhubapp.connection.requesters.accountstatementrequester.GetLastStatementRequester;
import com.example.cardhubapp.connection.requesters.accountstatementrequester.UpdateLastStatementRequest;
import com.example.cardhubapp.connection.requesters.creditcardrequester.RemoveCardFromCardholderRequester;
import com.example.cardhubapp.model.AccountStatement;
import com.example.cardhubapp.model.AccountStatementCalculator;
import com.example.cardhubapp.model.date.DateService;
import com.example.cardhubapp.guimessages.ConfirmationDialogWindow;
import com.example.cardhubapp.guimessages.ErrorMessageNotificator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class AccountStatementController extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_statement);
        allowSyncronousOperations();
        setValuesToCreditCardProductFields();
        setValuesToAccountStatementFields();
        setOnClickListenersToButtons();

    }
    @Override
    public void onClick(View view) {
        if(userClickedAddMonthlyPaymentButton(view)) {
            EditText addPaymentTextField = findViewById(R.id.addPaymentTextField);
            addPeriodPayment(addPaymentTextField);
        }
        if(userClickedShowHistoryButton(view)){
            startHistoryView();
        }
        if(userClickedDeleteCardButton(view)){
            deleteSelectedCreditCard();
        }
    }

    private void addPeriodPayment(EditText addPaymentTextField) {
        if(textFieldIsNotEmpty(addPaymentTextField)){
            Float periodPayment = Float.valueOf(addPaymentTextField.getText().toString());
            addPaymentToAccountStatement(periodPayment);
        }else{
            String message = "Por favor ingrese una cantidad";
            ErrorMessageNotificator.showShortMessage(this, message);
        }
    }

    private void addPaymentToAccountStatement(Float periodPayment) {
        JsonArray lastAccountStatementJson = getLastAccountStatement();
        AccountStatement lastAccountStatement = createAccountStatementFromJsonArray(lastAccountStatementJson);
        LocalDate currentDate = DateService.getCurrentDate();
        boolean isPaymentDateExpired = DateService.compareDates(currentDate.toString(), lastAccountStatement.getPaymentDate());
        if(!isPaymentDateExpired){
            updateLastAccountStatement(periodPayment, lastAccountStatement);
        }else{
            Float interestRate = Float.valueOf(getDataFromPreviousIntent("interestRate"));
            createNewAccountStatement(periodPayment, lastAccountStatement, interestRate);
        }
    }

    private void createNewAccountStatement(Float periodPayment, AccountStatement lastAccountStatement, Float interestRate) {
        Float currentDebt = lastAccountStatement.getCurrentDebt();
        Float paymentForNoInterest = lastAccountStatement.getPaymentForNoInterest();
        String newCurrentDebt = AccountStatementCalculator.calculateExpiredCurrentDebt(periodPayment, currentDebt, interestRate).toString();
        String newPaymentForNoInterestDebt = AccountStatementCalculator.calculateExpiredPaymentForNoInterest(periodPayment, paymentForNoInterest, interestRate).toString();

        String currentDate = DateService.getCurrentDate().toString();
        String newCutOffDate = DateService.addOneMonthToFormattedDate(lastAccountStatement.getCutOffDate());
        String newPaymentDate = DateService.addOneMonthToFormattedDate(lastAccountStatement.getPaymentDate());
        String cardFromCardHolder = getDataFromPreviousIntent("cardholderCardId").toString();

        saveAccountStatement(newCutOffDate, newPaymentDate, newCurrentDebt, newPaymentForNoInterestDebt, currentDate, cardFromCardHolder);
    }
    private JsonArray saveAccountStatement(String newCutOffDate, String newPaymentDate, String currentDebt, String paymentForNoInterest, String currentDate, String cardholderCardId) {
        ArrayList requestParameters = new ArrayList(Arrays.asList(newCutOffDate, newPaymentDate, currentDebt, paymentForNoInterest, currentDate, cardholderCardId));
        GenerateCardStatementRequester cardStatementGenerator = new GenerateCardStatementRequester(requestParameters);
        JsonArray createdCardStatementResponse = cardStatementGenerator.executeRequest();
        return createdCardStatementResponse;
    }

    private JsonArray updateLastAccountStatement(Float periodPayment, AccountStatement lastAccountStatement) {
        String updatedCurrentDebt = AccountStatementCalculator.calculateCurrentDebt(periodPayment, lastAccountStatement.getCurrentDebt()).toString();
        String updatedPaymentForNoInterestDebt = AccountStatementCalculator.calculatePaymentForNoInterest(periodPayment, lastAccountStatement.getPaymentForNoInterest()).toString();
        String statementId = lastAccountStatement.getId().toString();
        JsonArray updatedAccountStatementResponse = sendUpdateAccountStatementRequest(updatedCurrentDebt, updatedPaymentForNoInterestDebt, statementId);
        return updatedAccountStatementResponse;
    }

    private JsonArray sendUpdateAccountStatementRequest(String updatedCurrentDebt, String updatedPaymentForNoInterestDebt, String statementId) {
        ArrayList requestParameters = new ArrayList(Arrays.asList(statementId, updatedCurrentDebt, updatedPaymentForNoInterestDebt));
        Requester updateLastStatementRequest = new UpdateLastStatementRequest(requestParameters);
        JsonArray updatedAccountStatementResponse = updateLastStatementRequest.executeRequest();
        return updatedAccountStatementResponse;
    }

    private void setValuesToAccountStatementFields() {
        JsonArray accountStatementJsonArray = getLastAccountStatement();
        AccountStatement accountStatement = createAccountStatementFromJsonArray(accountStatementJsonArray);
        setTextsInView(accountStatement);

    }
    private JsonArray getLastAccountStatement() {
        String cardholderCardId = getDataFromPreviousIntent("cardholderCardId");
        ArrayList requestParameters = new ArrayList(Arrays.asList(cardholderCardId));
        Requester getLastStatementRequester = new GetLastStatementRequester(requestParameters);
        JsonArray accountStatementJsonArray = getLastStatementRequester.executeRequest();
        return accountStatementJsonArray;
    }

    private AccountStatement createAccountStatementFromJsonArray(JsonArray accountStatementJsonArray) {
        AccountStatement accountStatement = null;
        System.out.println("El accountstatement json array en controller es: " + accountStatementJsonArray);
        for (int i = 0; i < accountStatementJsonArray.size(); i++) {
            JsonObject jsonObject = accountStatementJsonArray.get(i).getAsJsonObject();

            int statementId = jsonObject.get("statement_id").getAsInt();
            String cutOffDate = jsonObject.get("cut_off_date").getAsString();
            String paymentDate = jsonObject.get("payment_date").getAsString();
            float currentDebt = jsonObject.get("current_debt").getAsFloat();
            float paymentForNoInterest = jsonObject.get("payment_for_no_interest").getAsFloat();

            accountStatement = new AccountStatement(statementId, cutOffDate, paymentDate, currentDebt, paymentForNoInterest);
        }
        return accountStatement;
    }

    private void setOnClickListenersToButtons() {
        ImageButton deleteCardButton = findViewById(R.id.deleteCardBtn);
        deleteCardButton.setOnClickListener(this);

        ImageButton showHistoryButton = findViewById(R.id.showHistoryBtn);
        showHistoryButton.setOnClickListener(this);

        Button addMonthlyPaymentButton = findViewById(R.id.addPeriodPaymentBtn);
        addMonthlyPaymentButton.setOnClickListener(this);

    }

    private void startHomeView() {
        Intent intent = new Intent(this, HomeController.class);
        intent.putExtra("USER_EMAIL", getDataFromPreviousIntent("userEmail"));
        startActivity(intent);
    }

    private void startHistoryView() {
        Intent intent = new Intent(this, AccountStatementHistoryController.class);
        intent.putExtra("cardholderCardId", getDataFromPreviousIntent("cardholderCardId"));
        intent.putExtra("cardName", getDataFromPreviousIntent("cardName"));
        startActivity(intent);
    }

    private void deleteSelectedCreditCard() {
        String windowMessage = "¿Seguro que quieres eliminar la tarjeta?";
        ConfirmationDialogWindow.showConfirmationDialog(this, windowMessage, () -> {
            deleteCreditCard();
            startHomeView();
        });
    }

    private void deleteCreditCard() {
        String userEmail = getDataFromPreviousIntent("userEmail");
        String creditCardProductId = getDataFromPreviousIntent("cardId");
        ArrayList requestParameters = new ArrayList(Arrays.asList(userEmail, creditCardProductId));
        Requester removeCardFromCardholderRequester = new RemoveCardFromCardholderRequester(requestParameters);
        removeCardFromCardholderRequester.executeRequest();
    }

    private void setTextsInView(AccountStatement accountStatement) {
        TextView cutOffDateTextView = findViewById(R.id.accountStatementCutOffDateTextView);
        cutOffDateTextView.setText(accountStatement.getCutOffDate());

        TextView paymentDateTextView = findViewById(R.id.accountStatementPaymentDateTextView);
        paymentDateTextView.setText(accountStatement.getPaymentDate());

        TextView currentDebtTextView = findViewById(R.id.accountStatementCurrentDebtTextView);
        currentDebtTextView.setText(accountStatement.getCurrentDebt().toString());

        TextView paymentForNoInterestTextView = findViewById(R.id.accountStatementPaymentForNoInterestTextView);
        paymentForNoInterestTextView.setText(accountStatement.getPaymentForNoInterest().toString());
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


    private boolean textFieldIsNotEmpty(EditText addPaymentTextField) {
        return !addPaymentTextField.getText().toString().equals("");
    }


    private boolean userClickedAddMonthlyPaymentButton(View view) {
        return view.getId() == R.id.addPeriodPaymentBtn;
    }

    private boolean userClickedShowHistoryButton(View view) {
        return view.getId() == R.id.showHistoryBtn;
    }

    private boolean userClickedDeleteCardButton(View view) {
        return view.getId() == R.id.deleteCardBtn;
    }


}
