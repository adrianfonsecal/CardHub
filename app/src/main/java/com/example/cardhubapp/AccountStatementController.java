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
import com.example.cardhubapp.model.TransactionType;
import com.example.cardhubapp.model.date.DateService;
import com.example.cardhubapp.guimessages.ConfirmationDialogWindow;
import com.example.cardhubapp.guimessages.ErrorMessageNotificator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
        if(userClickedAddExpenseButton(view)){
            addPeriodExpense();
            startHomeView();
        }
        if(userClickedAddPaymentButton(view)) {
            addPeriodPayment();
            startHomeView();
        }
        if(userClickedShowHistoryButton(view)){
            startHistoryView();
        }
        if(userClickedDeleteCardButton(view)){
            deleteSelectedCreditCard();
        }
    }

    private void addPeriodPayment() {
        EditText addPaymentTextField = findViewById(R.id.addPaymentNumberField);
        if(textFieldIsNotEmpty(addPaymentTextField)){
            Float periodPayment = Float.valueOf(addPaymentTextField.getText().toString());
            addPaymentToAccountStatement(periodPayment);
        }else{
            String message = "Por favor ingrese una cantidad";
            ErrorMessageNotificator.showShortMessage(this, message);
        }
    }

    private void addPeriodExpense() {
        EditText addExpenseTextField = findViewById(R.id.addExpenseNumberField);
        if(textFieldIsNotEmpty(addExpenseTextField)){
            Float periodExpense = Float.valueOf(addExpenseTextField.getText().toString());
            addExpenseToAccountStatement(periodExpense);
        }else{
            String message = "Por favor ingrese una cantidad";
            ErrorMessageNotificator.showShortMessage(this, message);
        }
    }

    private void generateAccountStatement(Float periodPayment, AccountStatement lastAccountStatement, TransactionType payment, String currentDate) {
        Float interestRate = Float.valueOf(getDataFromPreviousIntent("interestRate"));
        String cardFromCardHolder = getDataFromPreviousIntent("cardholderCardId").toString();
        AccountStatement newAccountStatement = createNewAccountStatement(periodPayment, lastAccountStatement, interestRate, payment);
        sendGenerateAccountStatementRequest(newAccountStatement, currentDate, cardFromCardHolder);
    }

    private void addPaymentToAccountStatement(Float periodPayment) {
        JsonArray lastAccountStatementJson = getLastAccountStatement();
        AccountStatement lastAccountStatement = createAccountStatementFromJsonArray(lastAccountStatementJson);
        String currentDate = DateService.getCurrentDate().toString();
        boolean isPaymentDateExpired = DateService.compareDates(currentDate.toString(), lastAccountStatement.getPaymentDate());
        if(paymentDateIsNotExpired(isPaymentDateExpired)){
            updateLastAccountStatementToAddPayment(periodPayment, lastAccountStatement);
        }else{
            generateAccountStatement(periodPayment, lastAccountStatement, TransactionType.PAYMENT, currentDate);
        }
    }

    private void addExpenseToAccountStatement(Float periodPayment) {
        JsonArray lastAccountStatementJson = getLastAccountStatement();
        AccountStatement lastAccountStatement = createAccountStatementFromJsonArray(lastAccountStatementJson);
        String currentDate = DateService.getCurrentDate().toString();
        boolean isPaymentDateExpired = DateService.compareDates(currentDate.toString(), lastAccountStatement.getPaymentDate());
        if(paymentDateIsNotExpired(isPaymentDateExpired)){
            updateLastAccountStatementToAddExpense(periodPayment, lastAccountStatement);
        }else{
            generateAccountStatement(periodPayment, lastAccountStatement, TransactionType.EXPENSE, currentDate);
        }
    }

    private AccountStatement createNewAccountStatement(Float periodPayment, AccountStatement lastAccountStatement, Float interestRate, TransactionType transactionType) {
        Float currentDebt = lastAccountStatement.getCurrentDebt();
        Float paymentForNoInterest = lastAccountStatement.getPaymentForNoInterest();
        Float newCurrentDebt = AccountStatementCalculator.calculateExpiredCurrentDebt(periodPayment, currentDebt, paymentForNoInterest, interestRate, transactionType);
        Float newPaymentForNoInterestDebt = AccountStatementCalculator.calculateExpiredPaymentForNoInterest(periodPayment, paymentForNoInterest, interestRate, transactionType);

        String newCutOffDate = DateService.addOneMonthToFormattedDate(lastAccountStatement.getCutOffDate());
        String newPaymentDate = DateService.addOneMonthToFormattedDate(lastAccountStatement.getPaymentDate());

        AccountStatement newAccountStatement = new AccountStatement(newCutOffDate, newPaymentDate, newCurrentDebt, newPaymentForNoInterestDebt);
        return newAccountStatement;
    }
    private JsonArray sendGenerateAccountStatementRequest(AccountStatement accountStatementToSave, String currentDate, String cardholderCardId) {
        ArrayList requestParameters = new ArrayList(Arrays.asList(accountStatementToSave.getCutOffDate().toString(), accountStatementToSave.getPaymentDate().toString(), accountStatementToSave.getCurrentDebt().toString(), accountStatementToSave.getPaymentForNoInterest().toString(), currentDate, cardholderCardId));
        GenerateCardStatementRequester cardStatementGenerator = new GenerateCardStatementRequester(requestParameters);
        JsonArray createdCardStatementResponse = cardStatementGenerator.executeRequest();
        return createdCardStatementResponse;
    }

    private JsonArray updateLastAccountStatementToAddPayment(Float periodPayment, AccountStatement lastAccountStatement) {
        String updatedCurrentDebt = AccountStatementCalculator.calculateCurrentDebt(periodPayment, lastAccountStatement.getCurrentDebt(), TransactionType.PAYMENT).toString();
        String updatedPaymentForNoInterestDebt = AccountStatementCalculator.calculatePaymentForNoInterest(periodPayment, lastAccountStatement.getPaymentForNoInterest(), TransactionType.PAYMENT).toString();
        String statementId = lastAccountStatement.getId().toString();
        JsonArray updatedAccountStatementResponse = sendUpdateAccountStatementRequest(updatedCurrentDebt, updatedPaymentForNoInterestDebt, statementId);
        return updatedAccountStatementResponse;
    }

    private JsonArray updateLastAccountStatementToAddExpense(Float periodPayment, AccountStatement lastAccountStatement) {
        String updatedCurrentDebt = AccountStatementCalculator.calculateCurrentDebt(periodPayment, lastAccountStatement.getCurrentDebt(), TransactionType.EXPENSE).toString();
        String updatedPaymentForNoInterestDebt = AccountStatementCalculator.calculatePaymentForNoInterest(periodPayment, lastAccountStatement.getPaymentForNoInterest(), TransactionType.EXPENSE).toString();
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
        System.out.println("EL last accountstatement es: " + accountStatementJsonArray);
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
        final int FIRST_ACCOUNT_STATEMENT = 0;
        AccountStatement accountStatement = null;
        System.out.println("El accountstatement json array en controller es: " + accountStatementJsonArray);
        JsonObject accountStatementJsonObject = accountStatementJsonArray.get(FIRST_ACCOUNT_STATEMENT).getAsJsonObject();

        int statementId = accountStatementJsonObject.get("statement_id").getAsInt();
        String cutOffDate = accountStatementJsonObject.get("cut_off_date").getAsString();
        String paymentDate = accountStatementJsonObject.get("payment_date").getAsString();
        float currentDebt = accountStatementJsonObject.get("current_debt").getAsFloat();
        float paymentForNoInterest = accountStatementJsonObject.get("payment_for_no_interest").getAsFloat();

        accountStatement = new AccountStatement(statementId, cutOffDate, paymentDate, currentDebt, paymentForNoInterest);
        return accountStatement;
    }

    private void setOnClickListenersToButtons() {
        ImageButton deleteCardButton = findViewById(R.id.deleteCardBtn);
        deleteCardButton.setOnClickListener(this);

        ImageButton showHistoryButton = findViewById(R.id.showHistoryBtn);
        showHistoryButton.setOnClickListener(this);

        Button addMonthlyPaymentButton = findViewById(R.id.addPeriodPaymentBtn);
        addMonthlyPaymentButton.setOnClickListener(this);

        Button addMonthlyExpenseButton = findViewById(R.id.addPeriodExpenseBtn);
        addMonthlyExpenseButton.setOnClickListener(this);

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
    private boolean paymentDateIsNotExpired(boolean isPaymentDateExpired) {
        return !isPaymentDateExpired;
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


    private boolean userClickedAddPaymentButton(View view) {
        return view.getId() == R.id.addPeriodPaymentBtn;
    }

    private boolean userClickedAddExpenseButton(View view) {
        return view.getId() == R.id.addPeriodExpenseBtn;
    }

    private boolean userClickedShowHistoryButton(View view) {
        return view.getId() == R.id.showHistoryBtn;
    }

    private boolean userClickedDeleteCardButton(View view) {
        return view.getId() == R.id.deleteCardBtn;
    }

}
