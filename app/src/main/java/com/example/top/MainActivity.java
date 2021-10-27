package com.example.top;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int JIMUTOCHANGE_ACTIVITY = 1001;
    String jimuto_room = "";
    String jimuto_name = "";
    String jimuto_id = null;

    private DatabaseHelper _helper;

    private String [] from ={"id","text"};
    private int[] to = {android.R.id.text2,android.R.id.text1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageButton image_button_touroku = findViewById(R.id.image_button_touroku);
        DoubleTourokuListener listener3 = new DoubleTourokuListener();
        image_button_touroku.setOnClickListener(listener3);


        Button jimutou_change = findViewById(R.id.jimuto_change_button);
        JimutoChangeListener listener4 = new JimutoChangeListener();
        jimutou_change.setOnClickListener(listener4);

        ImageButton image_button_uketori = findViewById(R.id.image_button_uketori);
        UketoriListener listener5 = new UketoriListener();
        image_button_uketori.setOnClickListener(listener5);

        eventLogshow();
        ListView eventLogshower = findViewById(R.id.event_show);
        EventShowListener showListener=new EventShowListener();
        eventLogshower.setOnItemClickListener(showListener);

        Button b_ryosei = findViewById(R.id.button_select);
        DBselect_Listener_ryosei ryosei_listener = new DBselect_Listener_ryosei();
        b_ryosei.setOnClickListener(ryosei_listener);

        Button b_parcels = findViewById(R.id.button_select2);
        DBselect_Listener_parcels parcels_listener = new DBselect_Listener_parcels();
        b_parcels.setOnClickListener(parcels_listener);

        Button b_event = findViewById(R.id.button_select3);
        DBselect_Listener_event event_listener = new DBselect_Listener_event();
        b_event.setOnClickListener(event_listener);

        Button duty_night = findViewById(R.id.duty_night_button);
        duty_night_listener listener6 = new duty_night_listener();
        duty_night.setOnClickListener(listener6);

        refleshEventLog reflasher = new refleshEventLog();
        reflasher.onAppStart();
    }


    public void eventLogshow(){
        List<Map<String, String>> show_eventlist = new ArrayList<>();
        _helper = new com.example.top.DatabaseHelper(MainActivity.this);
        SQLiteDatabase db = _helper.getWritableDatabase();
        String sql = "SELECT _id, created_at, event_type, parcel_uid, room_name, ryosei_name, target_event_uid,is_deleted FROM parcel_event where is_deleted = 0 order by _id desc limit 100" ;
        Cursor cursor = db.rawQuery(sql,null);
        show_eventlist.clear();
        while(cursor.moveToNext()) {
            if(cursor.getString(cursor.getColumnIndex("is_deleted")).equals("1")){
                continue;
            }
            Map<String, String> event_raw = new HashMap<>();
            String text = "";
            int index = cursor.getColumnIndex("_id");
            String event_id = String.valueOf(cursor.getInt(index));
            index = cursor.getColumnIndex("created_at");
            text += cursor.getString(index);
            index = cursor.getColumnIndex("event_type");
            int event_type_int = cursor.getInt(index);
            switch (event_type_int) {
                case 1://荷物登録
                    index = cursor.getColumnIndex("room_name");
                    text += cursor.getString(index);
                    index = cursor.getColumnIndex("ryosei_name");
                    text += cursor.getString(index) + " に荷物登録";
                    break;
                case 2://荷物受取
                    index = cursor.getColumnIndex("room_name");
                    text += cursor.getString(index);
                    index = cursor.getColumnIndex("ryosei_name");
                    text += cursor.getString(index) + " が荷物受取";
                    break;
                case 3://イベント削除：表示しなくてもいいかもね
                    text+="イベントが削除されました";
                    break;
            }
            event_raw.put("id", event_id);
            event_raw.put("text", text);
            show_eventlist.add(event_raw);
        }
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                show_eventlist,
                android.R.layout.simple_list_item_1,
                from,
                to
        );
        ListView listView = (ListView)findViewById(R.id.event_show);
        listView.setAdapter(adapter);
        ListView listListener = findViewById(R.id.event_show);
        listListener.setOnItemClickListener(new EventShowListener());
    }


    public void onReturnJimutoValue(String value, String id) {
        jimuto_id = id;

        String[] newStr = value.split("\\s+");
        jimuto_room = newStr[0];
        jimuto_name = newStr[1];
        TextView jimuto_show = findViewById(R.id.main_jimutou_show);
        jimuto_show.setText(jimuto_room + " " + jimuto_name);
    }


    private class DoubleTourokuListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (jimuto_id == null) {
                String show = "先に事務当番を設定してください。";
                Toast.makeText(MainActivity.this, show ,Toast.LENGTH_LONG).show();
            } else{
                Intent intent = new Intent(MainActivity.this, Double_Buttoned_Touroku.class);
                intent.putExtra("Jimuto_id", jimuto_id);
                intent.putExtra("Jimuto_room", jimuto_room);
                intent.putExtra("Jimuto_name", jimuto_name);
                startActivity(intent);
            }
        }
    }


    private class TourokuListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (jimuto_id == null) {
                String show = "先に事務当番を設定してください。";
                Toast.makeText(MainActivity.this, show ,Toast.LENGTH_LONG).show();
            } else{
                Intent intent = new Intent(MainActivity.this, Buttoned_Touroku.class);
                intent.putExtra("Jimuto_id", jimuto_id);
                intent.putExtra("Jimuto_room", jimuto_room);
                intent.putExtra("Jimuto_name", jimuto_name);
                startActivity(intent);
        }
        }
    }



    private class UketoriListener implements  View.OnClickListener {
        @Override
        public void onClick(View view){
            if (jimuto_id == null) {
                String show = "先に事務当番を設定してください。";
                Toast.makeText(MainActivity.this, show ,Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(MainActivity.this, Buttoned_Uketori.class);
                intent.putExtra("Jimuto_id", jimuto_id);
                intent.putExtra("Jimuto_room", jimuto_room);
                intent.putExtra("Jimuto_name", jimuto_name);
                startActivity(intent);
            }
        }

    }


    private class JimutoChangeListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent jimuto_intent = new Intent(MainActivity.this, Jimuto_Change.class);
            jimuto_intent.putExtra("Jimuto_name",jimuto_room + " " + jimuto_name);
            jimuto_intent.putExtra("Jimuto_id",jimuto_id);
            startActivityForResult(jimuto_intent,JIMUTOCHANGE_ACTIVITY);
        }
    }


    private class duty_night_listener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            if (jimuto_id == null) {
                String show = "先に事務当番を設定してください。";
                Toast.makeText(MainActivity.this, show ,Toast.LENGTH_LONG).show();
            } else {

                DialogFragment dialogFragment = new Duty_Night_Dialog();
                Bundle args = new Bundle();
                args.putString("register_staff_room",jimuto_room);
                args.putString("register_staff_name",jimuto_name);
                args.putString("register_staff_id",jimuto_id);

                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "Duty_Night_Dialog");
            }
        }
    }


    private class EventShowListener implements AdapterView.OnItemClickListener{
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            String event_id="";
            String ryosei_uid="";
            String created_at="";
            String event_type = null;
            String parcel_uid="";
            String room_name="";
            String ryosei_name="";
            String target_event_uid="";
            String is_finished="";
            Map<String ,String> item = (Map)parent.getItemAtPosition(position);
            //TextView configshow = findViewById(R.id.showText);
            //configshow.setText(item.get("id"));
            item.get("id");
            _helper = new com.example.top.DatabaseHelper(MainActivity.this);
            SQLiteDatabase db = _helper.getWritableDatabase();
            String sql = "SELECT _id, created_at, event_type,ryosei_uid, parcel_uid, room_name, ryosei_name, target_event_uid FROM parcel_event WHERE _id = "+
                    item.get("id");
            Cursor cursor = db.rawQuery(sql,null);
            while(cursor.moveToNext()) {
                int index = cursor.getColumnIndex("_id");
                event_id = String.valueOf(cursor.getInt(index));
                index = cursor.getColumnIndex("created_at");
                created_at = cursor.getString(index);
                index = cursor.getColumnIndex("event_type");
                event_type = String.valueOf(cursor.getInt(index));
                index = cursor.getColumnIndex("parcel_uid");
                parcel_uid = String.valueOf(cursor.getInt(index));
                index = cursor.getColumnIndex("room_name");
                room_name = cursor.getString(index);
                index = cursor.getColumnIndex("ryosei_name");
                ryosei_name = cursor.getString(index);
                index = cursor.getColumnIndex("target_event_uid");
                target_event_uid = String.valueOf(cursor.getInt(index));
                index = cursor.getColumnIndex("ryosei_uid");
                ryosei_uid = String.valueOf(cursor.getInt(index));
            }

            if(event_id==""){
                return;
            }

            DialogFragment dialogFragment = new Delete_Event_Dialog();
            Bundle args = new Bundle();
            args.putString("event_id",event_id);
            args.putString("parcel_id",parcel_uid);
            args.putString("ryosei_id",ryosei_uid);
            args.putString("event_type",event_type);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Delete_Event_Dialog");
        }

    }




    private class A101KumanoTourokuListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
            SQLiteDatabase db = _helper.getWritableDatabase();

            // 日時情報を指定フォーマットの文字列で取得
            Date dateObj = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String date = format.format(dateObj);
            String ryousei = "A101KumanoAjiri";
            String mada = "MadaUketottenai";
            // インサート用SQL文字列の用意。
            //String sqlInsert = "INSERT INTO nimotsu (time, ryosei, done) VALUES (?, ?, ?)";
            String sqlInsert = "INSERT INTO nimotsu VALUES (?, ?, ?)";
            // SQL文字列を元にプリペアドステートメントを取得。
            SQLiteStatement stmt = db.compileStatement(sqlInsert);
            // 変数のバイド。
            stmt.bindString(1, date);
            stmt.bindString(2, ryousei);
            stmt.bindString(3, mada);
            // インサートSQLの実行。
            stmt.executeInsert();

            // 主キーによる検索SQL文字列の用意。
            String sql = "SELECT * FROM nimotsu ";
            // SQLの実行。
            Cursor cursor = db.rawQuery(sql, null);
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            // SQL実行の戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得。
            while (cursor.moveToNext()) {
                // カラムのインデックス値を取得。
                int dateNote = cursor.getColumnIndex("time");
                // カラムのインデックス値を元に実際のデータを取得。
                note += cursor.getString(dateNote);
                int ryouseiNote = cursor.getColumnIndex("ryosei");
                note += cursor.getString(ryouseiNote);
                int ryouseiStatus = cursor.getColumnIndex("done");
                note += cursor.getString(ryouseiStatus);
                note += "\n";


            }
        }
    }
    private class DBselect_Listener_ryosei implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            _helper = new com.example.top.DatabaseHelper(MainActivity.this);
            SQLiteDatabase db = _helper.getWritableDatabase();
            String test;
            test=_helper.select_ryosei_show_json(db);
            TextView test_txtbox = findViewById(R.id.textView5);
            test_txtbox.setText(test);
        }
    }
    private class DBselect_Listener_parcels implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            _helper = new com.example.top.DatabaseHelper(MainActivity.this);
            SQLiteDatabase db = _helper.getWritableDatabase();
            String test;
            test=_helper.select_parcels_show_json(db);
            TextView test_txtbox = findViewById(R.id.textView5);
            test_txtbox.setText(test);
        }
    }

    private class DBselect_Listener_event implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            _helper = new com.example.top.DatabaseHelper(MainActivity.this);
            SQLiteDatabase db = _helper.getWritableDatabase();
            String test;
            test=_helper.select_event_show_json(db);
            TextView test_txtbox = findViewById(R.id.textView5);
            test_txtbox.setText(test);
        }
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
            switch (requestCode) {
                case JIMUTOCHANGE_ACTIVITY:
                    jimuto_id = intent.getStringExtra("Jimuto_id");
                    String[] newStr = intent.getStringExtra("Jimuto_room_name").split("\\s+");
                    jimuto_room = newStr[0];
                    jimuto_name = newStr[1];
                    TextView jimuto_show = findViewById(R.id.main_jimutou_show);
                    jimuto_show.setText(jimuto_room + " " + jimuto_name);
                default:
            }
        }
    public class refleshEventLog implements LifecycleObserver {

        private final Handler handler = new Handler();
        private Runnable runnable;

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onAppStart() {
            runnable = new Runnable() {
                @Override
                public void run() {
                    // do something.
                    eventLogshow();
                    // 1秒ごとに実行
                    handler.postDelayed(this, 1000);
                }
            };
            handler.post(runnable);

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onAppStop() {
            handler.removeCallbacks(runnable);
        }
    }


    }

