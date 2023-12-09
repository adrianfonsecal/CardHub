package com.example.cardhubapp.guimessages;

import android.content.Context;
import android.widget.Toast;

public class ErrorMessageNotificator {
    public static void showShortMessage(Context context, String message) {
        showMessage(context, message, Toast.LENGTH_SHORT);
    }

    private static void showMessage(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }
}
