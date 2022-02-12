package com.example.top;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class Register_Dialog extends DialogFragment {

    String owner_ryosei_name = "";
    String owner_ryosei_room = "";
    String owner_ryosei_id = "";
    String register_staff_name = "";
    String register_staff_room = "";
    String register_staff_id = "";
    private DatabaseHelper _helper;
    int placement = 0;
    public TouchSound touchsound;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {//owner_idとjimuto_idさえあれば行ける
        _helper = new com.example.top.DatabaseHelper(requireContext());
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        Cursor cursor;
        String sql;
        String message;


        //owner_ryosei_name = getArguments().getString("owner_name","");
        //owner_ryosei_room = getArguments().getString("owner_room","");
        owner_ryosei_id = getArguments().getString("owner_id","0");
        //register_staff_name = getArguments().getString("register_staff_name","");
        //register_staff_room = getArguments().getString("register_staff_room","");
        register_staff_id = getArguments().getString("register_staff_id","0");

        sql = "select room_name, ryosei_name from ryosei where uid ='"+ owner_ryosei_id + "'";
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        owner_ryosei_room=cursor.getString(cursor.getColumnIndex("room_name"));
        owner_ryosei_name=cursor.getString(cursor.getColumnIndex("ryosei_name"));

        sql = "select room_name, ryosei_name from ryosei where uid ='"+ register_staff_id + "'";
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        register_staff_room=cursor.getString(cursor.getColumnIndex("room_name"));
        register_staff_name=cursor.getString(cursor.getColumnIndex("ryosei_name"));


        String[] choices = {"普通", "冷蔵", "冷凍","大型","不在票","その他"};
        boolean[] choicesChecked = {true, false, false, false, false,false};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(owner_ryosei_room+" "+
                owner_ryosei_name+" に荷物受け取りします。")
                .setPositiveButton("受け取り", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // このボタンを押した時の処理を書きます。
                        if(placement != 5){
                        _helper = new com.example.top.DatabaseHelper(requireContext());
                        SQLiteDatabase db = _helper.getWritableDatabase();
                        _helper.register(db,owner_ryosei_id,
                          register_staff_id,register_staff_room,register_staff_name,placement,"");
                        _helper.close();}
                        else{
                            Intent others_intent = new Intent(getActivity(), RegistarOthersActivity.class);
                            others_intent.putExtra("Jimuto_id", register_staff_id);
                            others_intent.putExtra("Jimuto_room", register_staff_room);
                            others_intent.putExtra("Jimuto_name", register_staff_name);
                            others_intent.putExtra("Owner_id", owner_ryosei_id);
                            others_intent.putExtra("Owner_room", owner_ryosei_room);
                            others_intent.putExtra("Owner_name", owner_ryosei_name);
                            others_intent.putExtra("placement", String.valueOf(placement));
                            startActivity(others_intent);
                        }


                        if(placement != 5) {
                            String show = "事務当番" + register_staff_room + register_staff_name + "が" + owner_ryosei_room + " " +
                                    owner_ryosei_name + "に荷物を受け取りしました。";
                            touchsound = new TouchSound(RegisterActivity.getRegisterActivityContext());
                            touchsound.playsoundOOOscan();
                            Toast.makeText(getActivity(), show, Toast.LENGTH_LONG).show();
                        }
//                        insert_parcels_shearingstatus();
//                        update_ryosei_shearingstatus();
//                        insert_event_shearingstatus();

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
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.data1D));
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.data1D));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
        }

    }
    public void insert_parcels_shearingstatus (){

    }
    public void update_ryosei_shearingstatus (){

    }
    public void insert_event_shearingstatus (){

    }


}