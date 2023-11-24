package com.example.cardhubapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class AccountStatementController extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allowSyncronousOperations();
        setContentView(R.layout.account_statement);
        setValuesToFields();
    }

    private void setValuesToFields() {
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


    @Override
    public void onClick(View view) {

    }


    private void allowSyncronousOperations() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
