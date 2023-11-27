package com.example.cardhubapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.dataaccess.AccountStatementDatabaseAccesor;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

public class AccountStatementHistoryController extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_statement_history);
        allowSyncronousOperations();
        setOnClickListenersToButtons();
        setCardNameInView();
        setAccountStatementsInView();
    }

    private void setAccountStatementsInView() {
        JsonArray accountStatements = getAccountStatements();
        LinearLayout linearLayoutContainer = findViewById(R.id.accountStatementContainerLayout);
        //ScrollView scrollView = findViewById(R.id.scrollView3);
        try {
            for (int i = 0; i < accountStatements.size(); i++) {
                JsonObject jsonObject = accountStatements.get(i).getAsJsonObject();

                LinearLayout newRow = (LinearLayout) getLayoutInflater().inflate(R.layout.account_statement_table_row, null);
                TextView accountStatementMonthTextView = newRow.findViewById(R.id.accountStatementMonthTextView);
                TextView accountStatementCutOffDateTextView = newRow.findViewById(R.id.accountStatementCutOffDateTextView);
                TextView accountStatementPaymentDateTextView = newRow.findViewById(R.id.accountStatementPaymentDateTextView);
                TextView accountStatementCurrentDebtTextView = newRow.findViewById(R.id.accountStatementCurrentDebtTextView);
                TextView accountStatementPaymentForNoInterestTextView = newRow.findViewById(R.id.accountStatementPaymentForNoInterestTextView);

                // Configura los valores de los elementos de la fila según sea necesario
                accountStatementMonthTextView.setText("MES");
                accountStatementCutOffDateTextView.setText(jsonObject.get("cut_off_date").getAsString());
                accountStatementPaymentDateTextView.setText(jsonObject.get("payment_date").getAsString());
                accountStatementCurrentDebtTextView.setText(jsonObject.get("current_debt").getAsString());
                accountStatementPaymentForNoInterestTextView.setText(jsonObject.get("payment_for_no_interest").getAsString());

                System.out.println(jsonObject.get("cut_off_date").getAsString() + i);

                linearLayoutContainer.addView(newRow);
            }
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
    }

    private JsonArray getAccountStatements() {
        AccountStatementDatabaseAccesor accountStatementDatabaseAccesor = new AccountStatementDatabaseAccesor();
        JsonArray accountStatements = accountStatementDatabaseAccesor.getAllAccountStatementsOfCreditCard(Integer.valueOf(getDataFromPreviousIntent("cardholderCardId")));
        return accountStatements;
    }

    private void setCardNameInView() {
//        TextView creditCardName = findViewById(R.id.historyCardNameTextView);
//        creditCardName.setText("s");

    }

    private void setOnClickListenersToButtons() {

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

    @Override
    public void onClick(View view) {

    }


    private void allowSyncronousOperations() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }



}
