package com.example.top;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;
import java.util.Map;

public class Duty_Night_Dialog extends DialogFragment {


    String  staff_name = "";
    String  staff_room = "";
    String  staff_id = "";
    int nimotsu_count_sametime = 0;
    private DatabaseHelper _helper;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        staff_name = getArguments().getString("jimuto_name","");
        staff_room = getArguments().getString("jimuto_room","");
        staff_id = getArguments().getString("jimuto_id","0");
        _helper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = _helper.getWritableDatabase();

        List<Map<String,String>> choices = _helper.nightdutylist(db);
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
        builder.setTitle("泊まり事務当用の画面です。確認できた荷物にチェックを入れてください。")
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // このボタンを押した時の処理を書きます。
                        for(int i = 0; i < choices.size(); i++){
                            if(isCheckedList[i]){
                                _helper.night_check_updater(db,String.valueOf(idlist[i]));
                            }
                        }
                        if(nimotsu_count_sametime != 0){
                            Toast.makeText(getActivity(), "荷物確認をしました。荷物札確認をしてください。", Toast.LENGTH_SHORT).show();

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
        /*
        Dialog d = builder.setView(new View(getActivity())).create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = (int)(getResources().getDisplayMetrics().widthPixels*0.60);
        lp.height = (int)(getResources().getDisplayMetrics().heightPixels*0.70);
        d.show();
        d.getWindow().setAttributes(lp); */
        return builder.create();
    }
}