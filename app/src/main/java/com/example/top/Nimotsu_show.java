package com.example.top;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Nimotsu_show extends AppCompatActivity {

    private DatabaseHelper _helper;
    private ArrayList<String> nimotsu_list = new ArrayList<String>();
    private int row = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outdated_activity_nimotsu_show);
        _helper = new com.example.top.DatabaseHelper(Nimotsu_show.this);

        SQLiteDatabase db = _helper.getWritableDatabase();

        this.show_ryosei(db);
    }

    public void show_ryosei(SQLiteDatabase db){
        nimotsu_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT uid, owner_uid,owner_room_name,owner_ryosei_name,register_datetime,register_staff_uid,register_staff_room_name,register_staff_parcels_name FROM parcels ;" ;
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while(cursor.moveToNext()) {
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "荷物ID：";
            String putter = "";
            row = cursor.getColumnIndex("uid");
            putter = String.valueOf(cursor.getInt(row));
            note += putter + " ";

            row = cursor.getColumnIndex("owner_uid");
            putter = String.valueOf(cursor.getInt(row));
            note += "荷物主" + putter + " ";

            row = cursor.getColumnIndex("owner_room_name");
            putter = String.valueOf(cursor.getString(row));
            note += putter + " ";

            row = cursor.getColumnIndex("owner_ryosei_name");
            putter = String.valueOf(cursor.getString(row));
            note += putter + " ";

            row = cursor.getColumnIndex("register_datetime");
            putter = String.valueOf(cursor.getString(row));
            note += "日付：" + putter + " ";

            row = cursor.getColumnIndex("register_staff_uid");
            putter = String.valueOf(cursor.getInt(row));
            note += "登録事務当" + putter + " ";

            row = cursor.getColumnIndex("register_staff_room_name");
            putter = String.valueOf(cursor.getString(row));
            note += putter + " ";

            row = cursor.getColumnIndex("register_staff_parcels_name");
            putter = String.valueOf(cursor.getString(row));
            note += putter ;
            nimotsu_list.add(note);
        }
        cursor.close();

        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nimotsu_list.toArray(new String[nimotsu_list.size()]));

        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.nimotsu_show);
        listView.setAdapter(adapter);
    }
}