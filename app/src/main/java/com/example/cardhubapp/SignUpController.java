package com.example.cardhubapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.model.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class SignUpController extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        Button button1 = findViewById(R.id.signUpBtn);
        button1.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signUpBtn) {

            EditText nameField = findViewById(R.id.nameField);
            String userName =  nameField.getText().toString();

            EditText emailField = findViewById(R.id.emailField);
            String userEmail =  emailField.getText().toString();

            EditText passwordField = findViewById(R.id.passwordField);
            String userPassword =  passwordField.getText().toString();

            createUser(userName, userEmail, userPassword);


    }


}
    private void createUser(String name, String email, String password) {
        User newUser = new User(name, email, password);
        AsyncTaskSignUp asyncTask= new AsyncTaskSignUp();
        asyncTask.execute(newUser);

    }


}
