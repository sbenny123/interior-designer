package com.horizonid.horizoninteriordesigner.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class ConfirmationDialog {
    private Context context;
    private AlertDialog dialog;


    public ConfirmationDialog(Context context) {
        this.context = context;
    }

    public void createDialog(@Nullable String title, @Nullable String message) {

        String alertTitle = title != null ? title : "Confirmation";
        String alertMsg = message != null ? message :
                "Are you sure?";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(alertMsg);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog = builder.create();
    }

    public void showDialog() {
        dialog.show();
    }


    public void dismissDialog() {
        dialog.dismiss();
    }
}
