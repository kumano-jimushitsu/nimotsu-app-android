package com.example.top;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class Jimuto_Change_Dialog extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //値を受け取る
        String ryosei_room = getArguments().getString("room_ryosei","");
        String ryosei_id = getArguments().getString("id","0");
        builder.setTitle("事務当番交代")
                .setMessage(ryosei_room+"に事務当番を交代します。")
                .setPositiveButton("交代する", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // このボタンを押した時の処理を書きます。
                        Toast.makeText(getActivity(), ryosei_room+"に事務当番を交代しました。", Toast.LENGTH_SHORT).show();
                        //呼び出し元のフラグメントに結果を返す
                        JimutoChangeActivity callingActivity = (JimutoChangeActivity) getActivity();
                        callingActivity.onReturnValue(ryosei_room,ryosei_id);

                    }
                })
                .setNegativeButton("キャンセル", null);
        return builder.create();
    }

}