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

public class Nimotsu_Touroku_Dialog extends DialogFragment {

    String owner_ryosei_name = "";
    String owner_ryosei_room = "";
    String owner_ryosei_id = "";
    String register_staff_name = "";
    String register_staff_room = "";
    String register_staff_id = "";
    private DatabaseHelper _helper;
    int placement = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        owner_ryosei_name = getArguments().getString("owner_name","");
        owner_ryosei_room = getArguments().getString("owner_room","");
        owner_ryosei_id = getArguments().getString("owner_id","0");
        register_staff_name = getArguments().getString("register_staff_name","");
        register_staff_room = getArguments().getString("register_staff_room","");
        register_staff_id = getArguments().getString("register_staff_id","0");

        String[] choices = {"普通", "冷蔵", "冷凍","大型","不在票"};
        boolean[] choicesChecked = {true, false, false, false, false};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(owner_ryosei_room+" "+
                owner_ryosei_name+" に荷物登録します。")
                .setPositiveButton("登録", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // このボタンを押した時の処理を書きます。

                        _helper = new com.example.top.DatabaseHelper(requireContext());
                        SQLiteDatabase db = _helper.getWritableDatabase();
                        _helper.addParcel(db,owner_ryosei_id,owner_ryosei_room,owner_ryosei_name,
                          register_staff_id,register_staff_room,register_staff_name,placement
                        );

                        String show = "事務当番" + register_staff_room + register_staff_name + "が"+owner_ryosei_room+" "+
                                owner_ryosei_name+ "に荷物を登録しました。";
                        Toast.makeText(getActivity(), show ,Toast.LENGTH_LONG).show();
                        //((MainActivity) getContext()).eventLogshow();

                    }
                })
                .setNegativeButton("キャンセル", null)
                .setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    placement = which;
                    }
                });

        return builder.create();
    }


}