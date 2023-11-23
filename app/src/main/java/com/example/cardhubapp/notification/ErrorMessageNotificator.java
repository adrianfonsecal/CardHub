package com.example.cardhubapp.notification;

import android.content.Context;
import android.widget.Toast;

public class ErrorMessageNotificator {
    public static void showShortMessage(Context context, String message) {
        showMessage(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLongMessage(Context context, String message) {
        showMessage(context, message, Toast.LENGTH_LONG);
    }

    private static void showMessage(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }
}
