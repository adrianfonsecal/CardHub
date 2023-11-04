package com.example.cardhubapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LogInController extends AppCompatActivity implements View.OnClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = findViewById(R.id.signUpBtn);
        button1.setOnClickListener(this);

        Button button2 = findViewById(R.id.logInBtn);
        button2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signUpBtn) {
            Intent intent = new Intent(this, SignUpController.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.logInBtn) {
            EditText emailFieldObtained = findViewById(R.id.emailField);
            String userEmail = emailFieldObtained.getText().toString();

            EditText passwordFieldObtained = findViewById(R.id.passwordField);
            String userPassword = passwordFieldObtained.getText().toString();
            logIn(userEmail, userPassword);
        }

    }

    private void logIn(String email, String password){
        AsyncTaskOperator asyncTaskLogIn = new AsyncTaskOperator("http://10.0.2.2:8000/login/");
        asyncTaskLogIn.execute(email, password);

    }



}