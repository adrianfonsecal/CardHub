package com.example.cardhubapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardhubapp.connection.apirequest.UserAccessApiRequestProcessor;
import com.example.cardhubapp.notification.ErrorMessageNotificator;
import com.example.cardhubapp.validator.EmailValidator;
import com.example.cardhubapp.validator.PasswordValidator;
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
        setOnClickListenersToButtons();
    }

    private void setOnClickListenersToButtons() {
        Button signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (userClickedSignUpButton(view)) {
            signUpUser();
        }
    }

    private void signUpUser() {
        String userName =  getUserNameFromTextField();
        String userEmail =  getUserEmailFromTextField();
        String userPassword = getUserPasswordFromTextField();

        if(allFieldsAreValid(userName, userEmail, userPassword)){
            JsonArray signUpResponse = signUp(userName, userEmail, userPassword);
            boolean isSigned = isUserSignedUp(signUpResponse);
            if(isSigned == true){
                startLogInView();
            }else{
                String errorMessage = "Algo ocurrió en el proceso, lo sentimos";
                showErrorMessage(this, errorMessage);
            }
        }else{
            String errorMessage = "Por favor llene todos los campos";
            showErrorMessage(this, errorMessage);
        }

    }

    private boolean allFieldsAreValid(String userName, String userEmail, String userPassword) {
        boolean fieldsAreNotEmpty = fieldsAreNotEmpty(userName, userEmail, userPassword);
        boolean emailIsValid = emailIsValid(userEmail);
        boolean passwordIsValid = passwordIsValid(userPassword);
        return (fieldsAreNotEmpty && emailIsValid && passwordIsValid);
    }

    private boolean passwordIsValid(String userPassword) {
        if(PasswordValidator.isValidPassword(userPassword)){
            return true;
        }else{
            String message = "La contraseña debe tener al menos 6 caracteres y debe contener números";
            ErrorMessageNotificator.showShortMessage(this, message);
            return false;
        }
    }

    private boolean emailIsValid(String userEmail) {
        if(EmailValidator.isValidEmail(userEmail)){
            return true;
        }else{
            String message = "Por favor ingrese un email válido";
            ErrorMessageNotificator.showShortMessage(this, message);
            return false;
        }
    }


    private boolean fieldsAreNotEmpty(String userName, String userEmail, String userPassword) {
        boolean isUserNameEmpty = userName.equals("");
        boolean isUserEmailEmpty = userEmail.equals("");
        boolean isUserPasswordEmpty = userPassword.equals("");
        return !(isUserNameEmpty || isUserEmailEmpty || isUserPasswordEmpty);
    }

    private void startLogInView() {
        Intent intent = new Intent(this, LogInController.class);
        startActivity(intent);
    }

    private boolean userClickedSignUpButton(View view) {
        return view.getId() == R.id.signUpBtn;
    }

    private String getUserNameFromTextField() {
        EditText nameField = findViewById(R.id.nameField);
        String userName =  nameField.getText().toString();
        return userName;
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

    private JsonArray signUp(String name, String email, String password) {
        ArrayList userSignupData = new ArrayList<>(Arrays.asList(name, email, password));
        UserAccessApiRequestProcessor userAccessApiRequestProcessor = new UserAccessApiRequestProcessor("http://10.0.2.2:8000/signup/", userSignupData);
        JsonArray isSignedResponse = userAccessApiRequestProcessor.executeRequest();
        return isSignedResponse;
    }

    private boolean isUserSignedUp(JsonArray signUpResponse) {
        JsonElement firstElement = signUpResponse.get(0);
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


    private void showErrorMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
