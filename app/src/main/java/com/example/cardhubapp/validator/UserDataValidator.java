package com.example.cardhubapp.validator;

import com.example.cardhubapp.model.User;

public class UserDataValidator {

    public UserDataValidator(){}
    public User createUser(String username, String email, String password) {
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);
        User newUser = new User(username, email, password);
        return newUser;
    }

    private void validateEmail(String email) {
    }

    private void validateUsername(String username) {
        // Lógica de validación del nombre de usuario
    }

    private void validatePassword(String password) {
        // Lógica de validación de la contraseña
    }
}
