package com.example.cardhubapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cardhubapp.connection.asyncronous.AsyncTaskOperator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Arrays;


public class LogInController extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        allowSyncronousOperations();

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

            if(logIn(userEmail, userPassword) == true){
                startHomeView(userEmail, userPassword);
            }else{
                String errorMessage = "Usuario o contraseña incorrectos";
                showErrorMessage(this, errorMessage);
            }
        }

    }

    

    private boolean logIn(String email, String password){
        ArrayList userLoginData = new ArrayList<>(Arrays.asList(email, password));
        AsyncTaskOperator asyncTaskLogIn = new AsyncTaskOperator("http://10.0.2.2:8000/login/", userLoginData);
        asyncTaskLogIn.run();
        JsonArray isLoggedResponse = asyncTaskLogIn.getJsonResponse();
        System.out.println("La Jsonresponse de login fue: " + isLoggedResponse);
        JsonElement firstElement = isLoggedResponse.get(0);

        if (firstElement.toString().equals("\"True\"")) {
            System.out.println("Bienvenido");
            return true;
        }else{
            System.out.println("Incorecto");
            System.out.println("\"False\"");
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

    private void showErrorMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}