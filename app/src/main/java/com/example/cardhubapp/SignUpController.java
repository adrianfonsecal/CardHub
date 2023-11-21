package com.example.cardhubapp;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.connection.asyncronous.AsyncTaskOperator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Arrays;

public class SignUpController extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        allowSyncronousOperations();
        Button signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(this);


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
            boolean isSigned = signUp(userName, userEmail, userPassword);
            if(isSigned == true){
                //regresar a la pantalla login
            }else{
                String errorMessage = "Algo ocurrió en el proceso, lo sentimos";
                showErrorMessage(this, errorMessage);
            }
            signUp(userName, userEmail, userPassword);

    }
}
    private boolean signUp(String name, String email, String password) {

        ArrayList userSignupData = new ArrayList<>(Arrays.asList(name, email, password));
        AsyncTaskOperator asyncTaskLogIn = new AsyncTaskOperator("http://10.0.2.2:8000/signup/", userSignupData);
        asyncTaskLogIn.run();
        JsonArray isSignedResponse = asyncTaskLogIn.getJsonResponse();
        System.out.println("La Jsonresponse de Signup fue: " + isSignedResponse);
        JsonElement firstElement = isSignedResponse.get(0);

        if (firstElement.toString().equals("\"True\"")) {
            System.out.println("Singeado con éxito");
            return true;
        }else{
            System.out.println("Singeado sin exito");
            return  false;
        }

    }

    private void allowSyncronousOperations() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    private void showErrorMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
