package com.example.cardhubapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cardhubapp.connection.asyncronous.AsyncTaskOperator;
import com.example.cardhubapp.connection.asyncronous.InterfaceAsyncResponse;
import com.google.gson.JsonArray;


public class LogInController extends AppCompatActivity implements View.OnClickListener, InterfaceAsyncResponse {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(this);

        Button logInBtn = findViewById(R.id.logInBtn);
        logInBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signUpBtn) {
            Intent intent = new Intent(this, SignUpController.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.logInBtn) {
            EditText emailFieldObtained = findViewById(R.id.emailField);
            String userEmail = emailFieldObtained.getText().toString();

            EditText passwordFieldObtained = findViewById(R.id.passwordField);
            String userPassword = passwordFieldObtained.getText().toString();
            //Add logic for correct or incorrect login
            logIn(userEmail, userPassword);

            Intent intent = new Intent(this, HomeController.class);
            startActivity(intent);

        }

    }

    private void logIn(String email, String password){
//        AsyncTaskOperator asyncTaskLogIn = new AsyncTaskOperator("http://10.0.2.2:8000/login/");
//        asyncTaskLogIn.execute(email, password);

    }


    @Override
    public void processFinish(JsonArray output) {
        System.out.println("The output in login is: " + output);
    }
}