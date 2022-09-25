package com.example.top;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Night_Duty_Simple_Dialog extends DialogFragment {



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("泊事務当番")
                .setMessage("荷物確認を実行してもよろしいでしょうか？")
                .setPositiveButton("荷物確認をする", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // このボタンを押した時の処理を書きます。
                        Toast.makeText(getActivity(), "荷物確認しました。", Toast.LENGTH_SHORT).show();
                        //呼び出し元のフラグメントに結果を返す
                        TwoCheckBoxesNightDutyActivity callingActivity = (TwoCheckBoxesNightDutyActivity) getActivity();
                        callingActivity.onReturnValue(true);
                    }
                });
        update_parcels_shearingstatus();
        insert_event_shearingstatus();
        return builder.create();
    }
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
        }

    }
    public void update_parcels_shearingstatus (){

    }
    public void insert_event_shearingstatus (){

    }
}