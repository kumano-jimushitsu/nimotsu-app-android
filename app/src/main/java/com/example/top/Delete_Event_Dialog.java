package com.example.top;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
    //int placement = 0;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //値を受け取る
        ryosei_id = getArguments().getString("ryosei_id","0");
        parcel_id = getArguments().getString("parcel_id","0");
        event_id = getArguments().getString("event_id","0");
        event_type = getArguments().getString("event_type","0");//文字列として処理

        _helper = new com.example.top.DatabaseHelper(requireContext());
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        Cursor cursor;
        String sql;
        String message;
        switch(event_type) {
            case "1"://受取
                message = "寮に届いた荷物を受け取りました。";


                sql = "select * from parcels where uid ='"+ parcel_id + "'";
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();
                message += "(" + cursor.getString(cursor.getColumnIndex("register_datetime")) +")";
                message +="\r\n";
                message +="　・荷物の所有者："+ cursor.getString(cursor.getColumnIndex("owner_room_name")) +"　"+ cursor.getString(cursor.getColumnIndex("owner_ryosei_name"));
                message +="\r\n";
                message +="　・受取事務当　："+ cursor.getString(cursor.getColumnIndex("register_staff_room_name")) +"　"+ cursor.getString(cursor.getColumnIndex("register_staff_ryosei_name"));
                message +="\r\n";
                message +="　・引き渡し状況：";
                if(cursor.getInt(cursor.getColumnIndex("is_released"))==0){
                    message+="未引渡";
                }else{
                    message+="引渡済";
                }
                message +="\r\n";
                message +="　・荷物の種類　：";//{"普通", "冷蔵", "冷凍","大型","不在票","その他"};
                if(cursor.getInt(cursor.getColumnIndex("placement"))==0)message+="一般";
                if(cursor.getInt(cursor.getColumnIndex("placement"))==1)message+="冷蔵";
                if(cursor.getInt(cursor.getColumnIndex("placement"))==2)message+="冷凍";
                if(cursor.getInt(cursor.getColumnIndex("placement"))==3)message+="大型";
                if(cursor.getInt(cursor.getColumnIndex("placement"))==4)message+="不在表";
                if(cursor.getInt(cursor.getColumnIndex("placement"))==5){
                    message+="その他 memo:"+cursor.getString(cursor.getColumnIndex("note")) ;
                }



                break;
            case "2"://引渡
                message = "";
                break;
            case "10"://事務当交代
                message = "事務当番交代　";

                //一個前の事務当を取得
                sql = "SELECT uid, room_name, ryosei_name FROM parcel_event WHERE event_type=10 and  created_at<(select created_at from parcel_event where uid ='" + event_id + "') order by created_at desc limit 1;";
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();
                message += cursor.getString(cursor.getColumnIndex("room_name")) +""+ cursor.getString(cursor.getColumnIndex("ryosei_name"));
                message+="→";
                //指定されたイベントで交替した事務当を取得
                sql = "SELECT uid, room_name, ryosei_name FROM parcel_event where uid ='" + event_id + "';";
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();
                message += cursor.getString(cursor.getColumnIndex("room_name")) +""+ cursor.getString(cursor.getColumnIndex("ryosei_name"));
                break;
            default:
                message = "unknown event type";
                break;
        }


        builder.setTitle("操作履歴　詳細情報")
                .setMessage(message);

        if(event_type.equals("1")||event_type.equals("2")){
            builder.setPositiveButton("操作取り消し", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SQLiteDatabase db = _helper.getWritableDatabase();
                    _helper.delete_event(db,event_id,ryosei_id,parcel_id,event_type);
//
//                    update_parcels_shearingstatus();
//                    update_ryosei_shearingstatus();
//                    update_event_shearingstatus();
//                    _helper.close();
                }
            })
                    .setNegativeButton("キャンセル", null);
        }
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
    public void update_parcels_shearingstatus (){

    }
    public void update_ryosei_shearingstatus (){

    }
    public void update_event_shearingstatus (){

    }



}