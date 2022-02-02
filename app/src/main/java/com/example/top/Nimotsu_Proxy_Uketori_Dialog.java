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

public class Nimotsu_Proxy_Uketori_Dialog extends DialogFragment {

    String owner_ryosei_name = "";
    String owner_ryosei_room = "";
    String owner_ryosei_id = "";
    String  release_staff_name = "";
    String  release_staff_room = "";
    String  release_staff_id = "";
    String proxy_name ="";
    String proxy_room ="";
    String proxy_id ="";
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
        proxy_name =getArguments().getString("proxy_name","");
        proxy_room =getArguments().getString("proxy_room","");
        proxy_id =getArguments().getString("proxy_id","");
        String br = System.getProperty("line.separator");

        _helper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = _helper.getWritableDatabase();

        List<Map<String,String>> choices = _helper.nimotsuCountOfRyosei(db,owner_ryosei_id);
        String[] rabellist = new String[choices.size()];
        String[] idlist = new String[choices.size()];
        boolean[] isCheckedList = new boolean[choices.size()];
        nimotsu_count_sametime = 0;
        for(int i =0;i < choices.size();i++){
            rabellist[i] = choices.get(i).get("rabel");
            idlist[i] = choices.get(i).get("parcels_id");
            isCheckedList[i] = false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(owner_ryosei_room+" "+
                owner_ryosei_name+" の荷物を引き渡します。"+br+"注意" + proxy_room + " " + proxy_name + "が代理で受取します。")
                .setPositiveButton("引き渡し", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // このボタンを押した時の処理を書きます。
                        for(int i = 0; i < choices.size(); i++){
                            if(isCheckedList[i] == true){
                                _helper.release(db,owner_ryosei_id,owner_ryosei_room,owner_ryosei_name,String.valueOf(idlist[i]),release_staff_id,
                                release_staff_room,release_staff_name,proxy_id);
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
                        update_parcels_shearingstatus();
                        update_ryosei_shearingstatus();
                        insert_event_shearingstatus();
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
    public void update_ryosei_shearingstatus (){

    }
    public void insert_event_shearingstatus (){

    }
}