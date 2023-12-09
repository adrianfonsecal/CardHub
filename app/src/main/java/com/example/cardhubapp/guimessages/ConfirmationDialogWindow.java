package com.example.cardhubapp.guimessages;

import android.app.AlertDialog;
import android.content.Context;

public class ConfirmationDialogWindow {

    public static void showConfirmationDialog(Context context, String message, Runnable positiveAction) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("Sí", (dialog, which) -> positiveAction.run())
                .setNegativeButton("No", null)
                .show();
    }
}