package com.example.cardhubapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.cardhubapp.model.User;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

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
        AsyncTaskLogIn asyncTaskLogIn = new AsyncTaskLogIn(email, password);
        asyncTaskLogIn.execute(email, password);

    }



}
