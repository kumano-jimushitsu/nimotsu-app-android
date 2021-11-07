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

import java.util.List;
import java.util.Map;

public class Nimotsu_Uketori_Dialog extends DialogFragment {

    String owner_ryosei_name = "";
    String owner_ryosei_room = "";
    String owner_ryosei_id = "";
    String  release_staff_name = "";
    String  release_staff_room = "";
    String  release_staff_id = "";
    int nimotsu_count_sametime = 0;
    private DatabaseHelper _helper;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        owner_ryosei_name = getArguments().getString("owner_name","");
        owner_ryosei_room = getArguments().getString("owner_room","");
        owner_ryosei_id = getArguments().getString("owner_id","0");
         release_staff_name = getArguments().getString("release_staff_name","");
         release_staff_room = getArguments().getString("release_staff_room","");
         release_staff_id = getArguments().getString("release_staff_id","0");
        _helper = new com.example.top.DatabaseHelper(requireContext());
        SQLiteDatabase db = _helper.getWritableDatabase();

        List<Map<String,String>> choices = _helper.nimotsuCountOfRyosei(db,owner_ryosei_id);
        String[] rabellist = new String[choices.size()];
        int[] idlist = new int[choices.size()];
        boolean[] isCheckedList = new boolean[choices.size()];
        nimotsu_count_sametime = 0;
        for(int i =0;i < choices.size();i++){
            rabellist[i] = choices.get(i).get("rabel");
            idlist[i] = Integer.parseInt(choices.get(i).get("parcels_id"));
            isCheckedList[i] = false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(owner_ryosei_room+" "+
                owner_ryosei_name+" の荷物を引き渡します。")
                .setPositiveButton("引き渡し", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // このボタンを押した時の処理を書きます。
                        for(int i = 0; i < choices.size(); i++){
                            if(isCheckedList[i] == true){
                                _helper.receiveParcels(db,owner_ryosei_id,owner_ryosei_room,owner_ryosei_name,String.valueOf(idlist[i]),release_staff_id,
                                release_staff_room,release_staff_name);
                                nimotsu_count_sametime++;
                            }
                        }
                        if(nimotsu_count_sametime != 0){
                            Toast.makeText(getActivity(), owner_ryosei_room+ " " + owner_ryosei_name+
                                    "の荷物を"+ String.valueOf(nimotsu_count_sametime) +"個、引き渡しました", Toast.LENGTH_SHORT).show();
                            //荷物引き渡しページを閉じさせる。
                            //呼び出し元のフラグメントに結果を返す
                            Double_Buttoned_Uketori callingActivity = (Double_Buttoned_Uketori) getActivity();
                            callingActivity.closeActivity();
                        }
                    }
                })
                .setNegativeButton("キャンセル", null)
                .setMultiChoiceItems(rabellist, isCheckedList, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        isCheckedList[which] = isChecked;

                    }
                });
        return builder.create();
    }
}