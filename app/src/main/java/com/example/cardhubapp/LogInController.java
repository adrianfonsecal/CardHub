package com.example.cardhubapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cardhubapp.connection.asyncronous.AsyncTaskOperator;
import com.example.cardhubapp.notification.ErrorMessageNotificator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Arrays;


public class LogInController extends AppCompatActivity implements View.OnClickListener {

    private Button signUpButton;
    private Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        allowSyncronousOperations();
        setButtonsOnClickListeners();
    }

    @Override
    public void onClick(View view) {
        if (userClickedSignUpButton(view)) {
            startSignUpView();
        }
        if (userClickedLogInButton(view)) {
            logInUser();
        }

    }

    private void logInUser() {
        String userEmail = getUserEmailFromTextField();
        String userPassword = getUserPasswordFromTextField();
        JsonArray logInResponse = logIn(userEmail, userPassword);
        boolean isUserAuthenticated = isUserAuthenticated(logInResponse);
        if(isUserAuthenticated){
            startHomeView(userEmail, userPassword);
        }else{
            String errorMessage = "Usuario o contraseña incorrectos";
            ErrorMessageNotificator.showShortMessage(this, errorMessage);
        }
    }

    private boolean userClickedLogInButton(View view) {
        return view.getId() == R.id.logInBtn;
    }

    private boolean userClickedSignUpButton(View view) {
        return view.getId() == R.id.signUpBtn;
    }

    private String getUserPasswordFromTextField() {
        EditText passwordFieldObtained = findViewById(R.id.passwordField);
        String userPassword = passwordFieldObtained.getText().toString();
        return userPassword;
    }

    private String getUserEmailFromTextField() {
        EditText emailFieldObtained = findViewById(R.id.emailField);
        String userEmail = emailFieldObtained.getText().toString();
        return userEmail;
    }

    private void startSignUpView() {
        Intent intent = new Intent(this, SignUpController.class);
        startActivity(intent);
    }


    private JsonArray logIn(String email, String password){
        ArrayList userLoginParameters = new ArrayList<>(Arrays.asList(email, password));
        AsyncTaskOperator asyncTaskLogIn = new AsyncTaskOperator("http://10.0.2.2:8000/login/", userLoginParameters);
        asyncTaskLogIn.run();
        JsonArray isLoggedResponse = asyncTaskLogIn.getJsonResponse();
        return isLoggedResponse;
    }

    private boolean isUserAuthenticated(JsonArray loginResponse) {
        JsonElement firstElement = loginResponse.get(0);
        if (firstElement.toString().equals("\"True\"")) {
            return true;
        }else{
            return  false;
        }
    }

    private void allowSyncronousOperations() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void startHomeView(String userEmail, String userPassword) {
        Intent intent = new Intent(this, HomeController.class);
        intent.putExtra("USER_EMAIL", userEmail);
        intent.putExtra("USER_PASSWORD", userPassword);
        startActivity(intent);
    }

    private void setLogInBtn(Button logInBtn) {
        this.logInButton = logInBtn;
    }

    private void setSignUpButton(Button signUpButton) {
        this.signUpButton = signUpButton;
    }

    private void setButtonsOnClickListeners() {
        Button signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(this);
        setSignUpButton(signUpButton);

        Button logInBtn = findViewById(R.id.logInBtn);
        logInBtn.setOnClickListener(this);
        setLogInBtn(logInBtn);
    }



}