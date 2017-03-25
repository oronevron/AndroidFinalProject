package com.example.oron.androidfinalproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class MessagesAlertDialog extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int messageCode = getArguments().getInt("messageCode");
        String mystring = getResources().getString(messageCode);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mystring)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int resultCode = getArguments().getInt("resultCode");

                        Boolean isMessageOnly = getArguments().getBoolean("isMessageOnly");
                        if (!isMessageOnly) {
                            Boolean isTripIndexSent = getArguments().getBoolean("isTripIndexSent");
                            if (!isTripIndexSent) {
                                getActivity().setResult(resultCode);
                            } else {
                                int tripIndex = getArguments().getInt("tripIndex");
                                Intent intent = new Intent();
                                intent.putExtra("tripIndex", tripIndex);

                                getActivity().setResult(resultCode, intent);
                            }

                            getActivity().finish();
                        }
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
