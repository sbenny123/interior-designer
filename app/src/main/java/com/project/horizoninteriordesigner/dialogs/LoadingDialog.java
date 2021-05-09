package com.project.horizoninteriordesigner.dialogs;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.project.horizoninteriordesigner.R;


public class LoadingDialog {

    private Context context;
    private AlertDialog dialog;


    public LoadingDialog(Context context) {
        this.context = context;
    }


    public void createDialog(@Nullable String message) {

        String alertMsg = message != null ? message : "Loading";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(alertMsg);
        builder.setView(R.layout.dialog_loading);

        dialog = builder.create();
    }

    public void showDialog() {
        dialog.show();
    }


    public void dismissDialog() {
        dialog.dismiss();
    }
}
