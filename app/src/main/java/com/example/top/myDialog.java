package com.example.top;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


/*
呼び出しサンプルコード

public void showDialog(View view,String title,String mainText,String positiveButton,String neutralButton) {
            DialogFragment dialogFragment = new myDialog();
            Bundle args = new Bundle();
            args.putString("positivebutton",positiveButton);
            args.putString("neutralbutton",neutralButton);
            args.putString("title",title);
            args.putString("maintext",mainText);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "myDialog");
        }



*/
public class myDialog extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //値を受け取る
        String title = getArguments().getString("title", "");
        String mainText = getArguments().getString("maintext", "");
        String positiveButton = getArguments().getString("positivebutton", "");
        String neutralButton = getArguments().getString("neutralbutton", "");
        if (neutralButton == "") {
            builder.setTitle(title).setMessage(mainText).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // このボタンを押した時の処理を書きます。
                }
            });
        } else {
            builder.setTitle(title).setMessage(mainText).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // このボタンを押した時の処理を書きます。
                }
            }).setNeutralButton(neutralButton, null);

        }
        return builder.create();
    }
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.data1D));
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(30);
        }

    }

}