package com.example.top;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class IdentifyDialog extends DialogFragment {

    public TouchSound touchsound;
    private DatabaseHelper _helper;

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {

        touchsound = new TouchSound(MainActivity.getMainActivityContext());
        boolean[] isCheckedList = {false};
        _helper = new com.example.top.DatabaseHelper(requireContext());
        String[] rabellist = {"本人確認済み"};
        String ryosei_id = getArguments().getString("id", "0");
        String ryosei_name = getArguments().getString("room", "0");
        String ryosei_room = getArguments().getString("name", "0");


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(ryosei_name + " " + ryosei_room + " の本人確認を行います。事務当番は身分証明証を提示してもらってください。").setPositiveButton("引き渡し", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // このボタンを押した時の処理を書きます。

                if (isCheckedList[0]) {
                    touchsound.playsoundone();
                    Toast.makeText(MainActivity.getMainActivityContext(), ryosei_room + " " + ryosei_name + "の本人確認を行いました", Toast.LENGTH_LONG).show();
                    SQLiteDatabase db = _helper.getWritableDatabase();
                    db.execSQL("UPDATE ryosei SET status = 1,sharing_status = 10  WHERE uid='" + ryosei_id + "';");
                    _helper.identify_event(db, ryosei_id);
                    touchsound.playsound555complete();
                    //呼び出し元のフラグメントに結果を返す
                } else {
                    touchsound.playsounderror();
                    Toast.makeText(MainActivity.getMainActivityContext(), "チェックがされていません。", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNeutralButton("キャンセル", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        })

                .setMultiChoiceItems(rabellist, isCheckedList, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        isCheckedList[which] = isChecked;

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        return alertDialog;
    }
}
