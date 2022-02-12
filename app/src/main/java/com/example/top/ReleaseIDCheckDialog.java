package com.example.top;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ReleaseIDCheckDialog extends DialogFragment {


    public TouchSound touchsound;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        touchsound = new TouchSound(ReleaseActivity.getReceiveActivityContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("本人確認").setMessage("身分証明証(学生証、免許証、橙食券等)を確認してください。").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                touchsound.playsound555pico();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        return alertDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.data1D));
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
        }

    }
}