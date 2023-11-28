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
import com.example.cardhubapp.connection.requesters.accountstatementrequester.GetLastStatementRequester;
import com.example.cardhubapp.connection.requesters.creditcardrequester.RemoveCardFromCardholderRequester;
import com.example.cardhubapp.model.AccountStatement;
import com.example.cardhubapp.model.date.CurrentDateProvider;
import com.example.cardhubapp.model.date.DatesComparator;
import com.example.cardhubapp.notification.ConfirmationDialogWindow;
import com.example.cardhubapp.notification.ErrorMessageNotificator;
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
        if(userClickedDeleteCardButton(view)){
            deleteSelectedCreditCard();
        }
        if(userClickedAddMonthlyPaymentButton(view)) {
            EditText addPaymentTextField = findViewById(R.id.addPaymentTextField);
            if(textFieldIsNotEmpty(addPaymentTextField)){
                Float periodPayment = Float.valueOf(addPaymentTextField.getText().toString());
                JsonArray lastAccountStatementJson = getLastAccountStatement();
                AccountStatement lastAccountStatement = createAccountStatementFromJsonArray(lastAccountStatementJson);
                LocalDate currentDate = CurrentDateProvider.getCurrentDate();
                boolean isPaymentDateExpired = DatesComparator.compareDates(currentDate.toString(), lastAccountStatement.getPaymentDate());
                if(!isPaymentDateExpired){
                    updateLastAccountStatement(periodPayment, lastAccountStatement);
                }
            }else{
                String message = "Por favor ingrese una cantidad";
                ErrorMessageNotificator.showShortMessage(this, message);
            }
        }
        if(userClickedShowHistoryButton(view)){
            startHistoryView();
        }
    }

    private static void updateLastAccountStatement(Float periodPayment, AccountStatement lastAccountStatement) {
        Float newCurrentDebt = lastAccountStatement.getCurrentDebt() - periodPayment;
        lastAccountStatement.setCurrentDebt(newCurrentDebt);

        Float newPaymentForNoInterest = lastAccountStatement.getPaymentForNoInterest() - periodPayment;
        lastAccountStatement.setPaymentForNoInterest(newPaymentForNoInterest);

    }

    private void setValuesToAccountStatementFields() {
        JsonArray accountStatementJsonArray = getLastAccountStatement();

        AccountStatement accountStatement = createAccountStatementFromJsonArray(accountStatementJsonArray);
        setTextsInView(accountStatement);

    }

    private JsonArray getLastAccountStatement() {
        String cardholderCardId = getDataFromPreviousIntent("cardholderCardId");
        ArrayList queryParameters = new ArrayList(Arrays.asList(cardholderCardId));
        Requester getLastStatementRequester = new GetLastStatementRequester(queryParameters);
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

        Button addMonthlyPaymentButton = findViewById(R.id.addMonthlyPatmentBtn);
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
        ArrayList queryParameters = new ArrayList(Arrays.asList(userEmail, creditCardProductId));
        Requester removeCardFromCardholderRequester = new RemoveCardFromCardholderRequester(queryParameters);
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

    private boolean textFieldIsNotEmpty(EditText addPaymentTextField) {
        return !addPaymentTextField.getText().toString().equals("");
    }


    private boolean userClickedAddMonthlyPaymentButton(View view) {
        return view.getId() == R.id.addMonthlyPatmentBtn;
    }

    private boolean userClickedShowHistoryButton(View view) {
        return view.getId() == R.id.showHistoryBtn;
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
