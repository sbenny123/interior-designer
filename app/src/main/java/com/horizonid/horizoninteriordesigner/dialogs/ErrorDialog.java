package com.horizonid.horizoninteriordesigner.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.horizonid.horizoninteriordesigner.R;

public class ErrorDialog {
    private Context context;
    private AlertDialog dialog;


    public ErrorDialog(Context context) {
        this.context = context;
    }

    public void createDialog(int icon, @Nullable String title, @Nullable String message) {

        int alertIcon = icon != 0 ? icon : R.drawable.ic_error;
        String alertTitle = title != null ? title : "Error";
        String alertMsg = message != null ? message :
                "The application was unable to perform the action.";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setIcon(alertIcon);
        builder.setTitle(alertTitle);
        builder.setMessage(alertMsg);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
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
