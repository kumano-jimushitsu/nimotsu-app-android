package com.example.top;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class Proxy_Change_Dialog extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //値を受け取る
        String proxy_ryosei_room = getArguments().getString("room_ryosei","");
        String proxy_ryosei_id = getArguments().getString("id","0");
        builder.setTitle("代理受取人選択")
                .setMessage(proxy_ryosei_room+"を代理受取人として設定します。")
                .setPositiveButton("設定する", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // このボタンを押した時の処理を書きます。
                        Toast.makeText(getActivity(), proxy_ryosei_room+"を代理受取人として設定しました。", Toast.LENGTH_SHORT).show();
                        //呼び出し元のフラグメントに結果を返す
                        Double_Proxy_Change callingActivity = (Double_Proxy_Change) getActivity();
                        callingActivity.onReturnValue(proxy_ryosei_room,proxy_ryosei_id);

                    }
                })
                .setNegativeButton("キャンセル", null);

        insert_parcels_shearingstatus();
        update_ryosei_shearingstatus();
        insert_event_shearingstatus();
        return builder.create();
    }
    public void insert_parcels_shearingstatus (){

    }
    public void update_ryosei_shearingstatus (){

    }
    public void insert_event_shearingstatus (){

    }

}