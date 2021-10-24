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

public class Delete_Event_Dialog extends DialogFragment {
    String event_id = "";
    String ryosei_id = "";
    String parcel_id = "";
    String event_type = "";
    private DatabaseHelper _helper;
    int placement = 0;
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //値を受け取る
        ryosei_id = getArguments().getString("ryosei_id","0");
        parcel_id = getArguments().getString("parcel_id","0");
        event_id = getArguments().getString("event_id","0");
        event_type = getArguments().getString("event_type","0");

        builder.setTitle("荷物情報確認")
                .setMessage("工事中");

        if(event_type.equals("1")||event_type.equals("2")){
            builder.setMessage("工事中です。")
                    .setPositiveButton("イベント削除", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    _helper = new com.example.top.DatabaseHelper(requireContext());
                    SQLiteDatabase db = _helper.getWritableDatabase();
                    _helper.delete_event(db,event_id,ryosei_id,parcel_id,event_type);
                }
            })
                    .setNegativeButton("キャンセル", null);
        }
        return builder.create();
    }

}