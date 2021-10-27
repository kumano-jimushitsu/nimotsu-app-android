package com.example.top;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Double_Buttoned_Touroku extends AppCompatActivity {


    private DatabaseHelper _helper;
    Cursor cursor;

    //ArrayListを用意
    private String jimuto_name_Str= "";
    private String jimuto_id_Str= "";
    private String jimuto_room_Str= "";
    private String selectedBlock="";//選択されたブロック
    private String selectedRoom="";//選択された部屋
    private ArrayList<String> blocks_roomname_name = new ArrayList<>();
    private ArrayList<String> blocks_ryosei_id = new ArrayList<>();
    private List<Map<String,String>> show_ryosei = new ArrayList<>();//表示する寮生
    private List<String> show_block = new ArrayList<>();//全てのブロック
    private List<String> show_room = new ArrayList<>();//全ての部屋or選択されたブロックの部屋
    private String[] from={"id","room_name"};
    private int[] to = {android.R.id.text2,android.R.id.text1};



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.double_buttoned_touroku_layout);

        //事務当番の名前を受け取る
        Intent intent = getIntent();
        jimuto_name_Str = intent.getStringExtra("Jimuto_name");
        jimuto_id_Str = intent.getStringExtra("Jimuto_id");
        jimuto_room_Str = intent.getStringExtra("Jimuto_room");
        //事務当番の名前を表示する
        TextView jimuto_name =findViewById(R.id.double_jimutou_name_show);
        jimuto_name.setText("ただいまの事務当番は " + jimuto_room_Str +" "+jimuto_name_Str+" です。");

        selectedBlock = null;

        Button backbutton =(Button)findViewById(R.id.double_touroku_go_back_button);
        backbutton.setOnClickListener(this::onBackButtonClick);



        // DBヘルパーオブジェクトを生成。
        _helper = new DatabaseHelper(Double_Buttoned_Touroku.this);

        SQLiteDatabase db = _helper.getWritableDatabase();

        this.show_block_ryosei("");//""を渡すと全寮生を表示
        ListView ryoseilistListener = findViewById(R.id.double_buttoned_touroku_ryosei_list);
        ryoseilistListener.setOnItemClickListener(new ListRyoseiClickListener());
        get_block();
        show_block();
        get_room(" ");
        Collections.sort(show_room);
        show_room();
        ListView blocklistListener = findViewById(R.id.double_buttoned_touroku_block_list);
        blocklistListener.setOnItemClickListener(new ListBlockClickListener());
        ListView roomlistListener = findViewById(R.id.double_buttoned_touroku_room_list);
        roomlistListener.setOnItemClickListener(new ListRoomClickListener());

    }
    public void show_block_ryosei (String block){
        show_ryosei.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        String sql;
        // 主キーによる検索SQL文字列の用意。
        if (block == ""){
            sql = "SELECT _id, room_name, ryosei_name FROM ryosei;";
        }else {
            sql = "SELECT _id, room_name, ryosei_name FROM ryosei WHERE block_id = '" + block_to_id(block) + "';";
        }// SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while(cursor.moveToNext()) {
            Map<String,String> ryosei_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            String ryosei_id = "";
            // カラムのインデックス値を取得。
            int idNote = cursor.getColumnIndex("_id");
            // カラムのインデックス値を元に実際のデータを取得。
            ryosei_id = String.valueOf(cursor.getInt(idNote));
            ryosei_raw.put("id",String.valueOf(cursor.getInt(idNote)));
            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            note += cursor.getString(roomNameNote);
            note += " ";
            int ryouseiNote = cursor.getColumnIndex("ryosei_name");
            note += cursor.getString(ryouseiNote);
            ryosei_raw.put("room_name",note);
            blocks_roomname_name.add(note);
            blocks_ryosei_id.add(ryosei_id);
            show_ryosei.add(ryosei_raw);

        }
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter blocktoryoseiadapter = new SimpleAdapter
                (this,
                        show_ryosei,
                        android.R.layout.simple_list_item_1,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.double_buttoned_touroku_ryosei_list);
        listView.setAdapter(blocktoryoseiadapter);
        ListView listListener = findViewById(R.id.double_buttoned_touroku_ryosei_list);
        listListener.setOnItemClickListener(new ListRyoseiClickListener());
    }
    public void show_room_ryosei (String room){
        show_ryosei.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT _id, room_name, ryosei_name FROM ryosei WHERE room_name = '"+ room +"';" ;
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while(cursor.moveToNext()) {
            Map<String,String> ryosei_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            String ryosei_id = "";
            // カラムのインデックス値を取得。
            int idNote = cursor.getColumnIndex("_id");
            // カラムのインデックス値を元に実際のデータを取得。
            ryosei_id = String.valueOf(cursor.getInt(idNote));
            ryosei_raw.put("id",String.valueOf(cursor.getInt(idNote)));
            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            note += cursor.getString(roomNameNote);
            note += " ";
            int ryouseiNote = cursor.getColumnIndex("ryosei_name");
            note += cursor.getString(ryouseiNote);
            ryosei_raw.put("room_name",note);
            blocks_roomname_name.add(note);
            blocks_ryosei_id.add(ryosei_id);
            show_ryosei.add(ryosei_raw);

        }
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter adapter = new SimpleAdapter
                (this,
                        show_ryosei,
                        android.R.layout.simple_list_item_1,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.double_buttoned_touroku_ryosei_list);
        listView.setAdapter(adapter);
        ListView listListener = findViewById(R.id.double_buttoned_touroku_ryosei_list);
        listListener.setOnItemClickListener(new ListRyoseiClickListener());
    }
    public void show_block(){
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        ArrayAdapter blockadapter = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1, show_block);
        // ListViewにArrayAdapterを設定する
        ListView blocklistView = (ListView)findViewById(R.id.double_buttoned_touroku_block_list);
        blocklistView.setAdapter(blockadapter);
        ListView blocklistListener = findViewById(R.id.double_buttoned_touroku_block_list);
        blocklistListener.setOnItemClickListener(new ListRyoseiClickListener());
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
    public void show_room(){
        // ListViewにArrayAdapterを設定する
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        ArrayAdapter blockadapter = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1, show_room);
        // ListViewにArrayAdapterを設定する
        ListView roomlistView = (ListView)findViewById(R.id.double_buttoned_touroku_room_list);
        roomlistView.setAdapter(blockadapter);
        ListView roomlistListener = findViewById(R.id.double_buttoned_touroku_room_list);
        roomlistListener.setOnItemClickListener(new ListRoomClickListener());
    }
    public void get_room(String block) {
        show_room.clear();
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql;
        if(block_to_id(block) == 0){
            sql = "SELECT DISTINCT room_name FROM ryosei ;";
        }else {
            sql = "SELECT DISTINCT room_name FROM ryosei WHERE block_id = '" + block_to_id(block) + "';";
        }
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while(cursor.moveToNext()) {
            Map<String,String> room_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            room_raw.put("room_name",cursor.getString(roomNameNote));
            show_room.add(cursor.getString(roomNameNote));
            room_raw.clear();
        }
    }
    public int block_to_id(String block){
        int id = 0;
        switch(block){
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
    public void addblock(String blockitem){
        {
            Map<String, String> block_raw = new HashMap<>();
            block_raw.put("block", blockitem);
            show_block.add(blockitem);
            block_raw.clear();
        }
    }
    public class ListBlockClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedBlock= (String) parent.getItemAtPosition(position);
            show_block_ryosei(selectedBlock);
            get_room(selectedBlock);
            show_room();
        }
    }
    public class ListRoomClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedRoom = (String) parent.getItemAtPosition(position);
            show_room_ryosei(selectedRoom);
        }
    }
    private class ListRyoseiClickListener implements AdapterView.OnItemClickListener{

        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Map<String ,String> item = (Map)parent.getItemAtPosition(position);
            this.showDialog(view,item.get("room_name"),item.get("id"));
        }
        public void showDialog(View view,String owner_room_name,String owner_id) {
            DialogFragment dialogFragment = new Nimotsu_Touroku_Dialog();
            String[] newStr = owner_room_name.split("\\s+");
            Bundle args = new Bundle();
            args.putString("owner_room",newStr[0]);
            args.putString("owner_name",newStr[1]);
            args.putString("owner_id",owner_id);
            args.putString("register_staff_room",jimuto_room_Str);
            args.putString("register_staff_name",jimuto_name_Str);
            args.putString("register_staff_id",jimuto_id_Str);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Nimotsu_Touroku_Dialog");
        }

    }

    public void addRecord (int block, String heya, String ryousei_name) {
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
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // 戻るボタンの処理
            finish();
        }

        return true;
    }

    public void onBackButtonClick(View view){
        finish();
    }

}

