package com.example.top;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ViewGroup;
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
                        _helper.addParcel(db,owner_ryosei_id,owner_ryosei_room,owner_ryosei_name,
                          register_staff_id,register_staff_room,register_staff_name,placement);
                        _helper.close();}
                        else{
                            Intent others_intent = new Intent(getActivity(), Touroku_Others.class);
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
    public void insert_parcels_shearingstatus (){

    }
    public void update_ryosei_shearingstatus (){

    }
    public void insert_event_shearingstatus (){

    }


}