package com.project.horizoninteriordesigner.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class ErrorDialog {
    private Context context;
    private AlertDialog dialog;


    public ErrorDialog(Context context) {
        this.context = context;
    }

    public void createDialog(@Nullable String title, @Nullable String message) {

        String alertTitle = title != null ? title : "Error";
        String alertMsg = message != null ? message :
                "The application was unable to perform the action.";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(alertMsg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDialog();
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
