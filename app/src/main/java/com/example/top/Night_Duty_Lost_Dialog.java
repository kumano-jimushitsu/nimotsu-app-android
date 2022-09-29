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

public class Night_Duty_Lost_Dialog extends DialogFragment {

    String  roomName;
   String ownerName;
   String parcels_id;
   Boolean lost_status;
    private DatabaseHelper _helper;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        roomName = getArguments().getString("room_name","");
        ownerName = getArguments().getString("owner_name","");
        parcels_id = getArguments().getString("parcels_id","");
        lost_status = getArguments().getBoolean("lost_status",true);
        _helper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = _helper.getWritableDatabase();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(lost_status){
            //_helper.is_lost_updater(db,parcels_id);
            db.close();

            builder.setTitle("荷物紛失")
                    .setMessage(roomName +" " + ownerName + "の荷物を紛失中扱いにしました。")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // このボタンを押した時の処理を書きます。
                        }
                    });
        } else{
            //_helper.is_lost_updater(db,parcels_id);
            db.close();
            builder.setTitle("荷物発見")
                    .setMessage(roomName +" " + ownerName + "の荷物を未紛失扱いにしました。")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
        }

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
}