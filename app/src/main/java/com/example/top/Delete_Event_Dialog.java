package com.example.top;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class Delete_Event_Dialog extends DialogFragment {
    private static final int EVENT_REFRESH_ACTIVITY = 1002;
    private static final int EVENT_REFRESH_ACTIVITY_DELETE_FAILED = 1003;
    String event_id = "";
    String ryosei_id = "";
    String parcel_id = "";
    String jimuto_id = "";
    String event_type = "";
    private DatabaseHelper _helper;

    //int placement = 0;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //値を受け取る
        ryosei_id = getArguments().getString("ryosei_id", "0");
        parcel_id = getArguments().getString("parcel_id", "0");
        event_id = getArguments().getString("event_id", "0");
        jimuto_id = getArguments().getString("jimuto_id", "0");
        event_type = getArguments().getString("event_type", "0");//文字列として処理

        _helper = new com.example.top.DatabaseHelper(requireContext());
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        Cursor cursor;
        String sql;
        String message;
        switch (event_type) {
            case "1"://受取
                message = "寮に届いた荷物を受け取りました。";


                sql = "select * from parcels where uid ='" + parcel_id + "'";
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();

                message += "\r\n";
                message += "　・荷物の所有者：" + cursor.getString(cursor.getColumnIndex("owner_room_name")) + "　" + cursor.getString(cursor.getColumnIndex("owner_ryosei_name"));
                message += "\r\n";
                message += "　・引き渡し状況：";
                if (cursor.getInt(cursor.getColumnIndex("is_released")) == 0) {
                    message += "未引渡";
                    //ついでに、ここから引渡のダイアログを出せるようにする→したかった
                    /*
                    builder.setNeutralButton("受取",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DialogFragment dialogFragment = new Nimotsu_Touroku_Dialog();
                            Bundle args = new Bundle();
                            args.putString("owner_id",ryosei_id);
                            args.putString("register_staff_id",jimuto_id);
                            dialogFragment.setArguments(args);
                            dialogFragment.show(getFragmentManager(), "Nimotsu_Touroku_Dialog");
                        }
                    });*/
                } else {
                    message += "引渡済";
                }
                message += "\r\n";
                message += "　・寮到着時間　：" + cursor.getString(cursor.getColumnIndex("register_datetime")).replace('-', '/') + "";
                message += "\r\n";
                message += "　・受取時事務当：" + cursor.getString(cursor.getColumnIndex("register_staff_room_name")) + "　" + cursor.getString(cursor.getColumnIndex("register_staff_ryosei_name"));
                message += "\r\n";

                message += "　・荷物の種類　：";//{"普通", "冷蔵", "冷凍","大型","不在票","その他"};
                switch (cursor.getInt(cursor.getColumnIndex("placement"))) {
                    case 0:
                        message += "一般";
                        break;
                    case 1:
                        message += "冷蔵";
                        break;
                    case 2:
                        message += "冷凍";
                        break;
                    case 3:
                        message += "大型";
                        break;
                    case 4:
                        message += "不在票";
                        break;
                    case 5:
                        message += "その他 memo:" + cursor.getString(cursor.getColumnIndex("note"));
                        break;
                    case 6:
                        message += "新入寮生(面接番号:" + cursor.getString(cursor.getColumnIndex("note")) + ")";
                        break;
                    default:
                        message += "unknown";
                        break;
                }

                cursor.close();
                break;
            case "2"://引渡
                message = "寮生に荷物を引き渡しました。";
                sql = "select * from parcels where uid ='" + parcel_id + "'";
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();

                message += "\r\n";
                message += "　・荷物の所有者：" + cursor.getString(cursor.getColumnIndex("owner_room_name")) + "　" + cursor.getString(cursor.getColumnIndex("owner_ryosei_name"));
                message += "\r\n";
                message += "　・寮到着時間　：" + cursor.getString(cursor.getColumnIndex("register_datetime")).replace('-', '/') + "";
                message += "\r\n";
                message += "　・寮生引渡時間：" + cursor.getString(cursor.getColumnIndex("release_datetime")).replace('-', '/') + "";
                message += "\r\n";
                message += "　・引渡時事務当：" + cursor.getString(cursor.getColumnIndex("release_staff_room_name")) + "　" + cursor.getString(cursor.getColumnIndex("release_staff_ryosei_name"));
                message += "\r\n";

                message += "　・荷物の種類　：";//{"普通", "冷蔵", "冷凍","大型","不在票","その他"};
                switch (cursor.getInt(cursor.getColumnIndex("placement"))) {
                    case 0:
                        message += "一般";
                        break;
                    case 1:
                        message += "冷蔵";
                        break;
                    case 2:
                        message += "冷凍";
                        break;
                    case 3:
                        message += "大型";
                        break;
                    case 4:
                        message += "不在票";
                        break;
                    case 5:
                        message += "その他 memo:" + cursor.getString(cursor.getColumnIndex("note"));
                        break;
                    case 6:
                        message += "新入寮生(面接番号:" + cursor.getString(cursor.getColumnIndex("note")) + ")";
                        break;
                    default:
                        message += "unknown";
                        break;
                }
                if (cursor.getString(cursor.getColumnIndex("release_agent_uid")) != null) {
                    sql = "select room_name, ryosei_name from ryosei where uid ='" + cursor.getString(cursor.getColumnIndex("release_agent_uid")) + "';";
                    cursor = db.rawQuery(sql, null);
                    cursor.moveToFirst();
                    message += "\r\n　・代理　　：" + cursor.getString(cursor.getColumnIndex("room_name")) + "　" + cursor.getString(cursor.getColumnIndex("ryosei_name"));
                }

                cursor.close();
                break;
            case "10"://事務当交代
                message = "事務当番交代　";

                //一個前の事務当を取得
                sql = "SELECT uid, room_name, ryosei_name FROM parcel_event WHERE event_type=10 and  created_at<(select created_at from parcel_event where uid ='" + event_id + "') order by created_at desc limit 1;";
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();

                if (cursor.getCount() > 0) {
                    message += cursor.getString(cursor.getColumnIndex("room_name")) + "" + cursor.getString(cursor.getColumnIndex("ryosei_name"));
                    message += "→";
                }

                cursor.close();

                //指定されたイベントで交替した事務当を取得
                message += getJimutoAtEvent(db, event_id);
                break;
            case "11": // 泊まり事務当モード開始
                message = "泊まり事務当モード開始　" + getJimutoAtEvent(db, event_id);
                break;
            case "12": // 泊まり事務当モード終了
                message = "泊まり事務当モード終了　" + getJimutoAtEvent(db, event_id);
                break;
            case "20": // 本人確認完了
                sql = "select ryosei_name from ryosei where uid ='" + ryosei_id + "'";
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();
                message = "本人確認完了　" + cursor.getString(cursor.getColumnIndex("ryosei_name")) + "　5分後に登録完了通知が来ます。";
                break;
            default:
                message = "unknown event type";
                break;
        }


        builder.setTitle("操作履歴　詳細情報").setMessage(message);


        //event_typeが1or2(受取か引渡)で、is_finished=0ならば削除可能　削除ボタンを表示させる
        if (event_type.equals("1") || event_type.equals("2")) {
            sql = "SELECT is_finished FROM parcel_event where uid ='" + event_id + "';";
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex("is_finished")) == 0) {
                builder.setPositiveButton("操作取り消し", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity callingActivity = (MainActivity) getActivity();

                        SQLiteDatabase db = _helper.getWritableDatabase();
                        if (_helper.check_isFinished(db, event_id) == 0) {//is_finishedが0なら削除可能　ここで改めてチェック
                            _helper.delete_event(db, event_id, ryosei_id, parcel_id, jimuto_id, event_type);
                        } else {//削除ボタンが表示されていても、タイミングによってはis_finished=1になっているときもある。通知を出したい。
                            callingActivity.event_delete_failed_toast();
                        }


                        callingActivity.eventLogshow();
                        // update_parcels_shearingstatus();
                        // update_ryosei_shearingstatus();
                        // update_event_shearingstatus();
                        // _helper.close();
                    }
                });
            }
            //.setNeutralButton("確認終了　", null);

            cursor.close();
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

    /*
        public void showDialog(View view, String owner_room_name, String owner_id) {
            DialogFragment dialogFragment = new Nimotsu_Register_Dialog();
            String[] newStr = owner_room_name.split("\\s+");
            Bundle args = new Bundle();
            args.putString("owner_room",newStr[0]);
            args.putString("owner_name",newStr[1]);
            args.putString("owner_id",owner_id);
            //args.putString("release_staff_room",jimuto_room_Str);
            //args.putString("release_staff_name",jimuto_name_Str);
            args.putString("release_staff_id",jimuto_id_Str);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Nimotsu_Register_Dialog");
        }
        */

    private String getJimutoAtEvent(SQLiteDatabase db, String event_id) {
        String sql = "SELECT uid, room_name, ryosei_name FROM parcel_event where uid ='" + event_id + "';";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String getjimutoatevent = cursor.getString(cursor.getColumnIndex("room_name")) + "" + cursor.getString(cursor.getColumnIndex("ryosei_name"));
        cursor.close();
        return getjimutoatevent;
    }

}