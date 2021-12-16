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

public class Duty_Night_Dialog extends DialogFragment {


    String staffName = "";
    String staffRoom = "";
    String staffId = "";
    ArrayList<String> allData = null;
    int nimotsuCountSametime = 0;
    private DatabaseHelper _helper;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        staffName = getArguments().getString("jimuto_name","");
        staffRoom = getArguments().getString("jimuto_room","");
        staffId = getArguments().getString("jimuto_id","0");
        allData = getArguments().getStringArrayList("dataAll");
        _helper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = _helper.getWritableDatabase();

        List<Map<String,String>> choices = _helper.nightdutylist(db);
        String[] rabellist = new String[choices.size()];
        String[] idlist = new String[choices.size()];
        boolean[] isCheckedList = new boolean[choices.size()];
        nimotsuCountSametime = 0;
        for(int i =0;i < choices.size();i++){
            rabellist[i] = choices.get(i).get("rabel");
            idlist[i] = choices.get(i).get("parcels_id");
            isCheckedList[i] = false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("泊事務当番")
                .setMessage("チェックされていない荷物があります。荷物確認を実行してもよろしいでしょうか？")
                .setPositiveButton("荷物確認をする", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // このボタンを押した時の処理を書きます。
                        Toast.makeText(getActivity(), "荷物確認しました。", Toast.LENGTH_SHORT).show();
                        //呼び出し元のフラグメントに結果を返す

                        SQLiteDatabase db = _helper.getWritableDatabase();
                        for(int i = 0; i < allData.size(); i++) {
                            _helper.night_check_updater(db, allData.get(i));
                        }
                        db.close();
                        Night_Duty_NimotsuFuda callingActivity = (Night_Duty_NimotsuFuda) getActivity();
                        //callingActivity.onReturnValue(true);

                    }
                })
                .setNegativeButton("キャンセル", null);
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