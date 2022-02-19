package com.example.top;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.top.ClickListener.OnOneClickListener;
import com.example.top.ClickListener.OnOneItemClickListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class ReleaseActivity extends AppCompatActivity {

    private static final int PROXYCHANGE_ACTIVITY = 2001;
    //ArrayListを用意
    private final ArrayList<String> ryosei_block = new ArrayList<>();
    private final String jimuto_name_Str = "";
    private final ArrayList<String> blocks_roomname_name = new ArrayList<>();
    private final ArrayList<String> blocks_ryosei_id = new ArrayList<>();
    private final ArrayList<Integer> ryosei_parcels_count = new ArrayList<>();
    private final List<Map<String, String>> show_list = new ArrayList<>();
    private final List<Map<String, String>> show_ryosei = new ArrayList<>();//表示する寮生
    private final List<String> show_block = new ArrayList<>();//全てのブロック
    private final List<String> show_room = new ArrayList<>();//全ての部屋or選択されたブロックの部屋
    private final String[] from = {"parcels_current_count", "room_name"};
    private final int[] to = {android.R.id.text2, android.R.id.text1};
    Cursor cursor;
    String proxy_room_Str = "";
    String proxy_name_Str = "";
    String proxy_id_Str = null;
    private DatabaseHelper _helper;
    private String jimuto_id_Str = "";
    private String jimuto_room_Str = "";
    private String selectedBlock = "";//選択されたブロック
    private String selectedRoom = "";//選択された部屋
    private TextView proxy_room_name_text;
    private TextView titleText;
    private Button proxy_change_button;
    private boolean proxy_check = false;
    public  TouchSound touchsound;
    private static Context context;
    public Cursor cursor_parcels_count;
    //private ConstraintLayout double_buttoned_register;

    public static Context getReceiveActivityContext() {
        return ReleaseActivity.context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);

        context = getApplicationContext();
        touchsound = new TouchSound(this);
        // double_buttoned_register = findViewById(R.id.fragment_hi);
        //事務当番の名前を受け取る
        Intent intent = getIntent();
        //jimuto_name_Str = intent.getStringExtra("Jimuto_name");
        jimuto_id_Str = intent.getStringExtra("Jimuto_id");
        jimuto_room_Str = intent.getStringExtra("Jimuto_room");

        proxy_name_Str = intent.getStringExtra("Proxy_name");
        proxy_id_Str = intent.getStringExtra("Proxy_id");
        proxy_room_Str = intent.getStringExtra("Proxy_room");
        //事務当番の名前を表示する
        TextView jimuto_name = findViewById(R.id.release_jimuto_show);
        jimuto_name.setText(jimuto_room_Str);
        ImageButton backbutton = findViewById(R.id.release_go_back_button);
        backbutton.setOnClickListener(this::onBackButtonClick);
        selectedBlock = null;
        // DBヘルパーオブジェクトを生成。
        _helper = new DatabaseHelper(ReleaseActivity.this);
        SQLiteDatabase db = _helper.getWritableDatabase();
        get_block();
        show_block();
        get_room(" ");
        Collections.sort(show_room);
        show_room();
        show_all_ryosei();//nullを渡すと全寮生を表示
        ListView blocklistListener = findViewById(R.id.release_block_list);
        blocklistListener.setOnItemClickListener(new ReleaseActivity.ListBlockClickListener());
        ListView roomlistListener = findViewById(R.id.release_room_list);
        roomlistListener.setOnItemClickListener(new ReleaseActivity.ListRoomClickListener());
        ImageButton ryosei_search_button = findViewById(R.id.release_search_ryosei_name_button);
        ryosei_search_button.setOnClickListener(new ReleaseActivity.RyoseiSearchListener());
        Switch proxySwitch = findViewById(R.id.proxy_switch);
        proxySwitch.setOnCheckedChangeListener(new ReleaseActivity.ProxySwitchListener());

        proxy_room_name_text = findViewById(R.id.proxy_textview);
        //proxy_change_button = findViewById(R.id.proxy_cahnge_button);
        proxy_room_name_text.setVisibility(View.GONE);
        //proxy_change_button.setVisibility(View.GONE);
        //proxy_change_button.setOnClickListener(new DoubleProxyChangeListener());
        if (proxy_name_Str == null && proxy_room_Str == null) {
            proxy_room_name_text.setText("代理人が設定されていません。");
        } else {
            proxy_room_name_text.setText("代理人: " + proxy_room_Str + " " + proxy_name_Str);
        }

        // システムナビゲーションバーの色を変更
        ActivityHelper.enableTransparentFooter(this);
    }

    /*
    public void show_ryosei(int block) {
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT uid, room_name, ryosei_name, parcels_current_count FROM ryosei WHERE parcels_current_count > 0 AND block_id = '" + block + "'order by room_name asc;";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while (cursor.moveToNext()) {
            Map<String, String> ryosei_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            String ryosei_id = "";
            // カラムのインデックス値を取得。
            int idNote = cursor.getColumnIndex("uid");
            // カラムのインデックス値を元に実際のデータを取得。
            ryosei_id = cursor.getString(idNote);
            ryosei_raw.put("id", cursor.getString(idNote));
            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            note += cursor.getString(roomNameNote);
            note += " ";
            int ryouseiNote = cursor.getColumnIndex("ryosei_name");
            note += cursor.getString(ryouseiNote);
            ryosei_raw.put("room_name", note);
            int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            int parcels_count = cursor.getInt(index_parcels_current_count);
            ryosei_raw.put("parcels_current_count", String.valueOf(parcels_count));
            blocks_roomname_name.add(note);
            blocks_ryosei_id.add(ryosei_id);
            ryosei_parcels_count.add(parcels_count);
            show_list.add(ryosei_raw);

        }
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter adapter = new SimpleAdapter
                (this,
                        show_list,
                        android.R.layout.simple_list_item_2,
                        from,
                        to);

        // ListViewにArrayAdapterを設定する
        ListView listView = findViewById(R.id.release_ryosei_list);
        listView.setAdapter(adapter);
        ListView listListener = findViewById(R.id.release_ryosei_list);
        listListener.setOnItemClickListener(new ListRyoseiClickListener());
        _helper.close();
    }

     */

    public void show_all_ryosei() {
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        String sql;
        // 主キーによる検索SQL文字列の用意。
        sql = "SELECT uid, room_name, ryosei_name FROM ryosei  order by room_name asc;";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while (cursor.moveToNext()) {
            Map<String, String> ryosei_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            String ryosei_id = "";
            // カラムのインデックス値を取得。
            int idNote = cursor.getColumnIndex("uid");
            // カラムのインデックス値を元に実際のデータを取得。
            ryosei_id = cursor.getString(idNote);
            ryosei_raw.put("id", cursor.getString(idNote));
            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            note += cursor.getString(roomNameNote);
            note += " ";
            int ryouseiNote = cursor.getColumnIndex("ryosei_name");
            note += cursor.getString(ryouseiNote);
            ryosei_raw.put("room_name", note);
            //int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            //int parcels_count = cursor.getInt(index_parcels_current_count);
            //ryosei_raw.put("parcels_current_count", String.valueOf(parcels_count));

            //parcelsテーブルからIDで荷物を検索
            //String sql_parcels_count = "select count(*) from parcels where owner_uid ='" + ryosei_id + "' AND is_released = 0;";
            //cursor_parcels_count = db.rawQuery(sql_parcels_count, null);
            //cursor_parcels_count.moveToFirst();
            //int parcels_count = cursor_parcels_count.getInt(0);
            //ryosei_raw.put("parcels_current_count", String.valueOf(parcels_count));
            //cursor_parcels_count.close();
            blocks_roomname_name.add(note);
            blocks_ryosei_id.add(ryosei_id);
            //ryosei_parcels_count.add(parcels_count);
            show_list.add(ryosei_raw);
        }
        cursor.close();
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter blocktoryoseiadapter = new SimpleAdapter
                (this,
                        show_list,
                        android.R.layout.simple_list_item_2,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        ListView listView = findViewById(R.id.release_ryosei_list);
        listView.setAdapter(blocktoryoseiadapter);
        ListView listListener = findViewById(R.id.release_ryosei_list);
        listListener.setOnItemClickListener(new ReleaseActivity.ListRyoseiClickListener());
        _helper.close();
    }

    public void show_block_ryosei(String block) {
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        String sql;
        // 主キーによる検索SQL文字列の用意。
        sql = "SELECT uid, room_name, ryosei_name FROM ryosei WHERE  block_id = '" + block_to_id(block) + "'order by room_name asc;";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while (cursor.moveToNext()) {
            Map<String, String> ryosei_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            String ryosei_id = "";
            // カラムのインデックス値を取得。
            int idNote = cursor.getColumnIndex("uid");
            // カラムのインデックス値を元に実際のデータを取得。
            ryosei_id = cursor.getString(idNote);
            ryosei_raw.put("id", cursor.getString(idNote));
            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            note += cursor.getString(roomNameNote);
            note += " ";
            int ryouseiNote = cursor.getColumnIndex("ryosei_name");
            note += cursor.getString(ryouseiNote);
            ryosei_raw.put("room_name", note);
            //int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            //int parcels_count = cursor.getInt(index_parcels_current_count);
            //ryosei_raw.put("parcels_current_count", String.valueOf(parcels_count));

            //parcelsテーブルからIDで荷物を検索
            String sql_parcels_count = "select count(*) from parcels where owner_uid ='" + ryosei_id + "' AND is_released = 0;";
            cursor_parcels_count = db.rawQuery(sql_parcels_count, null);
            cursor_parcels_count.moveToFirst();
            int parcels_count = cursor_parcels_count.getInt(0);
            ryosei_raw.put("parcels_current_count", String.valueOf(parcels_count));
            cursor_parcels_count.close();
            blocks_roomname_name.add(note);
            blocks_ryosei_id.add(ryosei_id);
            ryosei_parcels_count.add(parcels_count);
            show_list.add(ryosei_raw);
        }
        cursor.close();
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter blocktoryoseiadapter = new SimpleAdapter
                (this,
                        show_list,
                        android.R.layout.simple_list_item_2,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        ListView listView = findViewById(R.id.release_ryosei_list);
        listView.setAdapter(blocktoryoseiadapter);
        ListView listListener = findViewById(R.id.release_ryosei_list);
        listListener.setOnItemClickListener(new ReleaseActivity.ListRyoseiClickListener());
        _helper.close();
    }

    public void show_room_ryosei(String room) {
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT uid, room_name, ryosei_name FROM ryosei WHERE room_name = '" + room + "'order by room_name asc;";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while (cursor.moveToNext()) {
            Map<String, String> ryosei_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            String ryosei_id = "";
            // カラムのインデックス値を取得。
            int idNote = cursor.getColumnIndex("uid");
            // カラムのインデックス値を元に実際のデータを取得。
            ryosei_id = cursor.getString(idNote);
            ryosei_raw.put("id", cursor.getString(idNote));
            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            note += cursor.getString(roomNameNote);
            note += " ";
            int ryouseiNote = cursor.getColumnIndex("ryosei_name");
            note += cursor.getString(ryouseiNote);
            ryosei_raw.put("room_name", note);
            //従来の荷物カウントを削除
            //int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            //int parcels_count = cursor.getInt(index_parcels_current_count);
            //ryosei_raw.put("parcels_current_count", String.valueOf(parcels_count));

            //parcelsテーブルからIDで荷物を検索
            String sql_parcels_count = "select count(*) from parcels where owner_uid ='" + ryosei_id + "' AND is_released = 0;";
            cursor_parcels_count = db.rawQuery(sql_parcels_count, null);
            cursor_parcels_count.moveToFirst();
            int parcels_count = cursor_parcels_count.getInt(0);
            ryosei_raw.put("parcels_current_count", String.valueOf(parcels_count));
            cursor_parcels_count.close();

            blocks_roomname_name.add(note);
            blocks_ryosei_id.add(ryosei_id);
            //ryosei_parcels_count.add(parcels_count);
            show_list.add(ryosei_raw);
        }
        cursor.close();
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter roomtoryoseiadapter = new SimpleAdapter
                (this,
                        show_list,
                        android.R.layout.simple_list_item_2,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        // ListViewにArrayAdapterを設定する
        ListView listView = findViewById(R.id.release_ryosei_list);
        listView.setAdapter(roomtoryoseiadapter);
        ListView listListener = findViewById(R.id.release_ryosei_list);
        listListener.setOnItemClickListener(new ReleaseActivity.ListRyoseiClickListener());
        _helper.close();
    }

    public void show_block() {
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        ArrayAdapter blockadapter = new ArrayAdapter
                (this, android.R.layout.simple_list_item_1, show_block);
        // ListViewにArrayAdapterを設定する
        ListView blocklistView = findViewById(R.id.release_block_list);
        blocklistView.setAdapter(blockadapter);
        ListView blocklistListener = findViewById(R.id.release_block_list);
        blocklistListener.setOnItemClickListener(new ReleaseActivity.ListRyoseiClickListener());
        _helper.close();
    }

    public void get_block() {
        addblock("A1");
        addblock("A2");
        addblock("A3");
        addblock("A4");
        addblock("B12");
        addblock("B3");
        addblock("B4");
        addblock("C12");
        addblock("C34");
        addblock("臨キャパ");
    }

    public void show_room() {
        // ListViewにArrayAdapterを設定する
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        ArrayAdapter blockadapter = new ArrayAdapter
                (this, android.R.layout.simple_list_item_1, show_room);
        // ListViewにArrayAdapterを設定する
        ListView roomlistView = findViewById(R.id.release_room_list);
        roomlistView.setAdapter(blockadapter);
        ListView roomlistListener = findViewById(R.id.release_room_list);
        roomlistListener.setOnItemClickListener(new ReleaseActivity.ListRoomClickListener());

        _helper.close();
    }

    public void get_room(String block) {
        show_room.clear();
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql;
        if (block_to_id(block) == 0) {
            sql = "SELECT DISTINCT room_name FROM ryosei  order by room_name asc;";
        } else {
            sql = "SELECT DISTINCT room_name FROM ryosei  WHERE block_id = '" + block_to_id(block) + "'order by room_name asc;";
        }
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while (cursor.moveToNext()) {
            Map<String, String> room_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            room_raw.put("room_name", cursor.getString(roomNameNote));
            show_room.add(cursor.getString(roomNameNote));
            room_raw.clear();
        }

        cursor.close();
        _helper.close();
    }

    public int block_to_id(String block) {
        int id = 0;
        switch (block) {
            case "A1":
                id = 1;
                break;
            case "A2":
                id = 2;
                break;
            case "A3":
                id = 3;
                break;
            case "A4":
                id = 4;
                break;
            case "B12":
                id = 5;
                break;
            case "B3":
                id = 6;
                break;
            case "B4":
                id = 7;
                break;
            case "C12":
                id = 8;
                break;
            case "C34":
                id = 9;
                break;
            case "臨キャパ":
                id = 10;
                break;
            default:
                id = 0;
                break;
        }
        return id;
    }

    public void addblock(String blockitem) {
        {
            Map<String, String> block_raw = new HashMap<>();
            block_raw.put("block", blockitem);
            show_block.add(blockitem);
            block_raw.clear();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case PROXYCHANGE_ACTIVITY:
                proxy_id_Str = intent.getStringExtra("Proxy_id");
                proxy_room_Str = intent.getStringExtra("Proxy_room");
                //proxy_name_Str = intent.getStringExtra("Proxy_name");
                proxy_room_name_text.setText("代理人: " + proxy_room_Str);
            default:
        }
    }

    public void search_show(String name) {
        show_ryosei.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT uid, room_name, ryosei_name  FROM ryosei WHERE " + "ryosei_name LIKE '%" + name + "%' " + "OR ryosei_name_kana LIKE '%" + name + "%' " + "OR ryosei_name_alphabet LIKE '%" + name + "%' " + "OR room_name LIKE '%" + name + "%' ";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while (cursor.moveToNext()) {
            Map<String, String> ryosei_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            String ryosei_id = "";
            // カラムのインデックス値を取得。
            int idNote = cursor.getColumnIndex("uid");
            // カラムのインデックス値を元に実際のデータを取得。
            ryosei_id = cursor.getString(idNote);
            ryosei_raw.put("id", cursor.getString(idNote));

            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            note += cursor.getString(roomNameNote);
            note += " ";
            int ryouseiNote = cursor.getColumnIndex("ryosei_name");
            note += cursor.getString(ryouseiNote);
            ryosei_raw.put("room_name", note);
            //int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            //int parcels_count = cursor.getInt(index_parcels_current_count);
            //ryosei_raw.put("parcels_current_count", String.valueOf(parcels_count));

            //parcelsテーブルからIDで荷物を検索
            //String sql_parcels_count = "select count(*) from parcels where owner_uid ='" + ryosei_id + "' AND is_released = 0;";
            //cursor_parcels_count = db.rawQuery(sql_parcels_count, null);
            //cursor_parcels_count.moveToFirst();
            //int parcels_count = cursor_parcels_count.getInt(0);
            //ryosei_raw.put("parcels_current_count", String.valueOf(parcels_count));
            //cursor_parcels_count.close();

            blocks_roomname_name.add(note);
            blocks_ryosei_id.add(ryosei_id);
            show_ryosei.add(ryosei_raw);
        }
        cursor.close();

        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter adapter = new SimpleAdapter
                (this,
                        show_ryosei,
                        android.R.layout.simple_list_item_2,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        ListView listView = findViewById(R.id.release_ryosei_list);
        listView.setAdapter(adapter);
        ListView listListener = findViewById(R.id.release_ryosei_list);
        listListener.setOnItemClickListener(new ReleaseActivity.ListRyoseiClickListener());
        _helper.close();
    }

    public void addRecord(int block, String heya, String ryousei_name) {
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        String sqlInsert = "INSERT INTO ryosei (block_id, room_name, ryosei_name) VALUES (?, ?, ?)";
        SQLiteStatement stmt = db.compileStatement(sqlInsert);
        // 変数のバイド。
        stmt.bindLong(1, block);
        stmt.bindString(2, heya);
        stmt.bindString(3, ryousei_name);
        // インサートSQLの実行。
        stmt.executeInsert();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 戻るボタンの処理
            _helper.close();

            Intent event_refresh_intent = new Intent();
            event_refresh_intent.putExtra("EventRefresh", true);
            setResult(RESULT_OK, event_refresh_intent);
            finish();
        }

        return true;
    }

    public void onBackButtonClick(View view) {
        _helper.close();

        Intent event_refresh_intent = new Intent();
        event_refresh_intent.putExtra("EventRefresh", true);
        setResult(RESULT_OK, event_refresh_intent);
        finish();
    }

    public void closeActivity() {
        _helper.close();
        Intent event_refresh_intent = new Intent();
        event_refresh_intent.putExtra("EventRefresh", true);
        setResult(RESULT_OK, event_refresh_intent);
        finish();
    }

    public class ListBlockClickListener extends OnOneItemClickListener {
        @Override
        public void onOneItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedBlock = (String) parent.getItemAtPosition(position);
            show_block_ryosei(selectedBlock);
            get_room(selectedBlock);
            show_room();
            touchsound.releasecursorblock();
        }
    }

    public class ListRoomClickListener extends OnOneItemClickListener{
        @Override
        public void onOneItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedRoom = (String) parent.getItemAtPosition(position);
            show_room_ryosei(selectedRoom);
            touchsound.releasecursorroom();
        }
    }

    private class RyoseiSearchListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            int count = 0;
            EditText input = findViewById(R.id.release_search_ryosei_name);
            String input_name = input.getText().toString();
            input_name = input_name.replaceAll("　", "").replaceAll(" ", "");
            input_name = Normalizer.normalize(input_name, Normalizer.Form.NFKC);
            Pattern p = Pattern.compile("([0-9A-zぁ-んァ-ヶｱ-ﾝ\\u4E00-\\u9FFF\\u3005-\\u3007]+)"
                    // + " \\p{InHiragana}|" + " \\p{InKatakana}|"
                    // + " \\p{InCJKUnifiedIdeographs}+)"
                    , Pattern.COMMENTS);
            //if(input_name.matches( "^[A-zぁ-んァ-ヶｱ-ﾝﾞﾟ\u4E00-\u9FFF\u3005-\u3007]*$") ) {
            if (p.matcher(input_name).matches()) {
                search_show(input_name);
            } else if (count > 5) {
                Toast.makeText(ReleaseActivity.this, "漢字、ひらがな、カタカナしか使えません。", Toast.LENGTH_SHORT).show();
                count++;
            } else {
                Toast.makeText(ReleaseActivity.this, "漢字、ひらがな、カタカナしか使えません。", Toast.LENGTH_SHORT).show();
                count++;
            }

        }
    }


    //Listの寮生がタップされたときの処理
    private class ListRyoseiClickListener extends OnOneItemClickListener {

        public void onOneItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String, String> item = (Map) parent.getItemAtPosition(position);
            //音声再生
            touchsound.releasecursorryosei();
            Integer parcels_count = null;

            // show_all_ryosei()で表示されたところはViewにparcels_current_countが設定されていない
            if (item.get("parcels_current_count") == null) {
                SQLiteDatabase db_to_get_parcels_count = _helper.getWritableDatabase();
                String sql_parcels_count = "select count(*) from parcels where owner_uid ='" + item.get("id") + "' AND is_released = 0;";
                cursor_parcels_count = db_to_get_parcels_count.rawQuery(sql_parcels_count, null);
                cursor_parcels_count.moveToFirst();
                parcels_count = cursor_parcels_count.getInt(0);
                cursor_parcels_count.close();
            } else {
                parcels_count = Integer.parseInt(item.get("parcels_current_count"));
            }

            //代理受け取りモード
            if (proxy_check) {
                if (parcels_count == 0) {
                    String show = item.get("room_name") + "には現在荷物が一つも登録されていません。";
                    Toast.makeText(ReleaseActivity.this, show, Toast.LENGTH_LONG).show();
                } else if (proxy_id_Str == null) {
                    Toast.makeText(ReleaseActivity.this, "代理人を設定してください。", Toast.LENGTH_LONG).show();
                } else {
                    this.showProxyDialog(view, item.get("room_name"), item.get("id"), proxy_id_Str, proxy_room_Str, proxy_name_Str);
                    this.showIDCheckDialog(view);
                }
            }

            //通常受け取りモード
            else {
                if (parcels_count == 0) {
                    String show = item.get("room_name") + "には現在荷物が一つも登録されていません。";
                    Toast.makeText(ReleaseActivity.this, show, Toast.LENGTH_LONG).show();
                } else {
                    this.showDialog(view, item.get("room_name"), item.get("id"));
                    this.showIDCheckDialog(view);
                }
            }

        }

        public void showDialog(View view, String owner_room_name, String owner_id) {
            DialogFragment dialogFragment = new ReleaseDialog();
            String[] newStr = owner_room_name.split("\\s+");
            Bundle args = new Bundle();
            args.putString("owner_room", newStr[0]);
            args.putString("owner_name", newStr[1]);
            args.putString("owner_id", owner_id);
            args.putString("release_staff_room", jimuto_room_Str);
            args.putString("release_staff_name", jimuto_name_Str);
            args.putString("release_staff_id", jimuto_id_Str);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Nimotsu_Register_Dialog");
        }

        public void showIDCheckDialog(View view) {
            DialogFragment dialogFragment = new ReleaseIDCheckDialog();
            Bundle args = new Bundle();
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Nimotsu_Register_Dialog");
        }

        public void showProxyDialog(View view, String owner_room_name, String owner_id, String proxy_id, String proxy_room, String proxy_name) {
            DialogFragment dialogFragment = new ReleaseProxyDialog();
            String[] newStr = owner_room_name.split("\\s+");
            Bundle args = new Bundle();
            args.putString("owner_room", newStr[0]);
            args.putString("owner_name", newStr[1]);
            args.putString("owner_id", owner_id);
            args.putString("release_staff_room", jimuto_room_Str);
            args.putString("release_staff_name", jimuto_name_Str);
            args.putString("release_staff_id", jimuto_id_Str);
            args.putString("proxy_room", proxy_room);
            args.putString("proxy_name", proxy_name);
            args.putString("proxy_id", proxy_id);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Nimotsu_Proxy_Register_Dialog");
        }

    }
   /* public class ListRyoseiClickListener implements AdapterView.OnItemClickListener{

        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Map<String ,String> item = (Map)parent.getItemAtPosition(position);
            if(Integer.parseInt(item.get("parcels_current_count"))==0){
                String show = item.get("room_name") + "には現在荷物が一つも登録されていません。";
                Toast.makeText(Double_Buttoned_Register.this, show ,Toast.LENGTH_LONG).show();
            }else {
                this.showDialog(view, item.get("room_name"), item.get("id"));


            }
        }
        public void showDialog(View view,String owner_room_name,String owner_id) {
            DialogFragment dialogFragment = new Nimotsu_Register_Dialog();
            String[] newStr = owner_room_name.split("\\s+");
            Bundle args = new Bundle();
            args.putString("owner_room",newStr[0]);
            args.putString("owner_name",newStr[1]);
            args.putString("owner_id",owner_id);
            args.putString("release_staff_room",jimuto_room_Str);
            args.putString("release_staff_name",jimuto_name_Str);
            args.putString("release_staff_id",jimuto_id_Str);

            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Nimotsu_Register_Dialog");


        }

    }
    */

    public class ProxySwitchListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                proxy_room_name_text.setVisibility(View.VISIBLE);
                //proxy_change_button.setVisibility(View.VISIBLE);
                Intent proxy_intent = new Intent(ReleaseActivity.this, ReleaseProxyActivity.class);
                proxy_intent.putExtra("Proxy_name", proxy_room_Str + " " + proxy_name_Str);
                proxy_intent.putExtra("Proxyid", proxy_id_Str);
                startActivityForResult(proxy_intent, PROXYCHANGE_ACTIVITY);
                //ConstraintLayout double_buttoned_register = (ConstraintLayout)findViewById(R.id.double_buttoned_register_constraintlayout);
                //double_buttoned_register.setBackgroundColor(Color.rgb(255,200,180));
                //titleText.setText("代理人荷物引き渡しの画面です。");
            } else {
                proxy_room_name_text.setVisibility(View.GONE);
                //proxy_change_button.setVisibility(View.GONE);
                //ConstraintLayout double_buttoned_register = (ConstraintLayout)findViewById(R.id.double_buttoned_register_constraintlayout);
                //double_buttoned_register.setBackgroundColor(Color.rgb(255,255,255));
            }
            proxy_check = isChecked;
        }
    }

    private class DoubleProxyChangeListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            Intent proxy_intent = new Intent(ReleaseActivity.this, ReleaseProxyActivity.class);
            proxy_intent.putExtra("Proxy_name", proxy_room_Str + " " + proxy_name_Str);
            proxy_intent.putExtra("Proxyid", proxy_id_Str);
            startActivityForResult(proxy_intent, PROXYCHANGE_ACTIVITY);
        }
    }

}

