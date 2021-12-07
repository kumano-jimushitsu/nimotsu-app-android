package com.example.top;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class Double_Buttoned_Uketori extends AppCompatActivity {

    private DatabaseHelper _helper;
    Cursor cursor;

    //ArrayListを用意
    private ArrayList<String > ryosei_block = new ArrayList<>();
    private String jimuto_name_Str= "";
    private String jimuto_id_Str= "";
    private String jimuto_room_Str= "";
    private String selectedBlock="";//選択されたブロック
    private String selectedRoom="";//選択された部屋
    private ArrayList<String> blocks_roomname_name = new ArrayList<>();
    private ArrayList<String> blocks_ryosei_id = new ArrayList<>();
    private ArrayList<Integer> ryosei_parcels_count = new ArrayList<>();
    private List<Map<String,String>> show_list = new ArrayList<>();
    private List<Map<String,String>> show_ryosei = new ArrayList<>();//表示する寮生
    private List<String> show_block = new ArrayList<>();//全てのブロック
    private List<String> show_room = new ArrayList<>();//全ての部屋or選択されたブロックの部屋
    private String[] from={"parcels_current_count","room_name"};
    private int[] to = {android.R.id.text2,android.R.id.text1};
    private TextView  proxy_room_name_text;
    private Button  proxy_change_button;
    private boolean proxy_check = false;

    private static final int PROXYCHANGE_ACTIVITY = 2001;
    String proxy_room_Str = "";
    String proxy_name_Str = "";
    String proxy_id_Str = null;

    private ConstraintLayout double_buttoned_uketori;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.double_buttoned_uketori_layout);

        double_buttoned_uketori = findViewById(R.id.double_buttoned_uketori_constraintlayout);
        //事務当番の名前を受け取る
        Intent intent = getIntent();
        jimuto_name_Str = intent.getStringExtra("Jimuto_name");
        jimuto_id_Str = intent.getStringExtra("Jimuto_id");
        jimuto_room_Str = intent.getStringExtra("Jimuto_room");

        proxy_name_Str = intent.getStringExtra("Proxy_name");
        proxy_id_Str = intent.getStringExtra("Proxy_id");
        proxy_room_Str = intent.getStringExtra("Proxy_room");
        //事務当番の名前を表示する
        TextView jimuto_name =findViewById(R.id.double_jimutou_name_show);
        jimuto_name.setText("ただいまの事務当番は " + jimuto_room_Str +" "+jimuto_name_Str+" です。");
        Button backbutton =(Button)findViewById(R.id.double_uketori_go_back_button);
        backbutton.setOnClickListener(this::onBackButtonClick);
        selectedBlock = null;
        // DBヘルパーオブジェクトを生成。
        _helper = new DatabaseHelper(Double_Buttoned_Uketori.this);
        SQLiteDatabase db = _helper.getWritableDatabase();
        get_block();
        show_block();
        get_room(" ");
        Collections.sort(show_room);
        show_room();
        this.show_block_ryosei(null);//nullを渡すと全寮生を表示
        ListView blocklistListener = findViewById(R.id.double_buttoned_uketori_block_list);
        blocklistListener.setOnItemClickListener(new Double_Buttoned_Uketori.ListBlockClickListener());
        ListView roomlistListener = findViewById(R.id.double_buttoned_uketori_room_list);
        roomlistListener.setOnItemClickListener(new Double_Buttoned_Uketori.ListRoomClickListener());
        Button ryosei_search_button = findViewById(R.id.uketori_name_search);
        ryosei_search_button.setOnClickListener(new Double_Buttoned_Uketori.RyoseiSearchListener());
        Switch proxySwitch = findViewById(R.id.proxy_switch);
        proxySwitch.setOnCheckedChangeListener(new Double_Buttoned_Uketori.ProxySwitchListener());
        proxy_room_name_text = findViewById(R.id.proxy_textview);
        proxy_change_button = findViewById(R.id.proxy_cahnge_button);
        proxy_room_name_text.setVisibility(View.GONE);
        proxy_change_button.setVisibility(View.GONE);
        proxy_change_button.setOnClickListener(new DoubleProxyChangeListener());
        if(proxy_name_Str == null && proxy_room_Str == null) {
            proxy_room_name_text.setText("代理受取人が設定されていません。");
        }else{
            proxy_room_name_text.setText("代理受取人: " + proxy_room_Str + " " + proxy_name_Str);
        }
    }
    public void show_ryosei (int block){
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT uid, room_name, ryosei_name, parcels_current_count FROM ryosei WHERE block_id = '"+ String.valueOf(block) +"';" ;
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while(cursor.moveToNext()) {
            Map<String,String> ryosei_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            String ryosei_id = "";
            // カラムのインデックス値を取得。
            int idNote = cursor.getColumnIndex("uid");
            // カラムのインデックス値を元に実際のデータを取得。
            ryosei_id = cursor.getString(idNote);
            ryosei_raw.put("id",cursor.getString(idNote));
            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            note += cursor.getString(roomNameNote);
            note += " ";
            int ryouseiNote = cursor.getColumnIndex("ryosei_name");
            note += cursor.getString(ryouseiNote);
            ryosei_raw.put("room_name",note);
            int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            int parcels_count = cursor.getInt(index_parcels_current_count);
            ryosei_raw.put("parcels_current_count",String.valueOf(parcels_count));
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
        ListView listView = (ListView)findViewById(R.id.double_buttoned_uketori_ryosei_list);
        listView.setAdapter(adapter);
        ListView listListener = findViewById(R.id.double_buttoned_uketori_ryosei_list);
        listListener.setOnItemClickListener(new ListRyoseiClickListener());
        _helper.close();
    }
    public void show_block_ryosei (String block){
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        String sql;
        // 主キーによる検索SQL文字列の用意。
        if (block == null){
            sql = "SELECT uid, room_name, ryosei_name,parcels_current_count FROM ryosei;";
        }else {
            sql = "SELECT uid, room_name, ryosei_name ,parcels_current_count FROM ryosei WHERE block_id = '" + block_to_id(block) + "';";
        }// SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while(cursor.moveToNext()) {
            Map<String,String> ryosei_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            String ryosei_id = "";
            // カラムのインデックス値を取得。
            int idNote = cursor.getColumnIndex("uid");
            // カラムのインデックス値を元に実際のデータを取得。
            ryosei_id = cursor.getString(idNote);
            ryosei_raw.put("id",cursor.getString(idNote));
            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            note += cursor.getString(roomNameNote);
            note += " ";
            int ryouseiNote = cursor.getColumnIndex("ryosei_name");
            note += cursor.getString(ryouseiNote);
            ryosei_raw.put("room_name",note);
            int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            int parcels_count = cursor.getInt(index_parcels_current_count);
            ryosei_raw.put("parcels_current_count",String.valueOf(parcels_count));
            blocks_roomname_name.add(note);
            blocks_ryosei_id.add(ryosei_id);
            ryosei_parcels_count.add(parcels_count);
            show_list.add(ryosei_raw);

        }
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter blocktoryoseiadapter = new SimpleAdapter
                (this,
                        show_list,
                        android.R.layout.simple_list_item_2,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.double_buttoned_uketori_ryosei_list);
        listView.setAdapter(blocktoryoseiadapter);
        ListView listListener = findViewById(R.id.double_buttoned_uketori_ryosei_list);
        listListener.setOnItemClickListener(new Double_Buttoned_Uketori.ListRyoseiClickListener());
        _helper.close();
    }
    public void show_room_ryosei (String room){
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT uid, room_name, ryosei_name, parcels_current_count FROM ryosei WHERE room_name = '"+ room +"';" ;
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while(cursor.moveToNext()) {
            Map<String,String> ryosei_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            String ryosei_id = "";
            // カラムのインデックス値を取得。
            int idNote = cursor.getColumnIndex("uid");
            // カラムのインデックス値を元に実際のデータを取得。
            ryosei_id = cursor.getString(idNote);
            ryosei_raw.put("id",cursor.getString(idNote));
            // カラムのインデックス値を取得。
            int roomNameNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            note += cursor.getString(roomNameNote);
            note += " ";
            int ryouseiNote = cursor.getColumnIndex("ryosei_name");
            note += cursor.getString(ryouseiNote);
            ryosei_raw.put("room_name",note);
            int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            int parcels_count = cursor.getInt(index_parcels_current_count);
            ryosei_raw.put("parcels_current_count",String.valueOf(parcels_count));
            blocks_roomname_name.add(note);
            blocks_ryosei_id.add(ryosei_id);
            ryosei_parcels_count.add(parcels_count);
            show_list.add(ryosei_raw);
        }
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter roomtoryoseiadapter = new SimpleAdapter
                (this,
                        show_list,
                        android.R.layout.simple_list_item_2,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.double_buttoned_uketori_ryosei_list);
        listView.setAdapter(roomtoryoseiadapter);
        ListView listListener = findViewById(R.id.double_buttoned_uketori_ryosei_list);
        listListener.setOnItemClickListener(new Double_Buttoned_Uketori.ListRyoseiClickListener());
        _helper.close();
    }
    public void show_block(){
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        ArrayAdapter blockadapter = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1, show_block);
        // ListViewにArrayAdapterを設定する
        ListView blocklistView = (ListView)findViewById(R.id.double_buttoned_uketori_block_list);
        blocklistView.setAdapter(blockadapter);
        ListView blocklistListener = findViewById(R.id.double_buttoned_uketori_block_list);
        blocklistListener.setOnItemClickListener(new Double_Buttoned_Uketori.ListRyoseiClickListener());
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
    public void show_room(){
        // ListViewにArrayAdapterを設定する
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        ArrayAdapter blockadapter = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1, show_room);
        // ListViewにArrayAdapterを設定する
        ListView roomlistView = (ListView)findViewById(R.id.double_buttoned_uketori_room_list);
        roomlistView.setAdapter(blockadapter);
        ListView roomlistListener = findViewById(R.id.double_buttoned_uketori_room_list);
        roomlistListener.setOnItemClickListener(new Double_Buttoned_Uketori.ListRoomClickListener());

        _helper.close();
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

        _helper.close();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case PROXYCHANGE_ACTIVITY:
                proxy_id_Str = intent.getStringExtra("Proxy_id");
                proxy_room_Str = intent.getStringExtra("Proxy_room");
                proxy_name_Str = intent.getStringExtra("Proxy_name");
                proxy_room_name_text.setText("代理受取人: " + proxy_room_Str + " " + proxy_name_Str);
            default:
        }
    }
    public void search_show(String name){
        show_ryosei.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT uid, room_name, ryosei_name ,parcels_current_count  FROM ryosei WHERE " +
                "ryosei_name LIKE '%" + name +"%' " +
                "OR ryosei_name_kana LIKE '%" + name +"%' " +
                "OR ryosei_name_alphabet LIKE '%" + name +"%' "+
                "OR room_name LIKE '%" + name +"%' ";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while(cursor.moveToNext()) {
            Map<String,String> ryosei_raw = new HashMap<>();
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            String ryosei_id = "";
            // カラムのインデックス値を取得。
            int idNote = cursor.getColumnIndex("uid");
            // カラムのインデックス値を元に実際のデータを取得。
            ryosei_id = cursor.getString(idNote);
            ryosei_raw.put("id",cursor.getString(idNote));

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
                        android.R.layout.simple_list_item_2,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.double_buttoned_uketori_ryosei_list);
        listView.setAdapter(adapter);
        ListView listListener = findViewById(R.id.double_buttoned_uketori_ryosei_list);
        listListener.setOnItemClickListener(new Double_Buttoned_Uketori.ListRyoseiClickListener());
        _helper.close();
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
            _helper.close();

            Intent event_refresh_intent = new Intent();
            event_refresh_intent.putExtra("EventRefresh",true);
            setResult(RESULT_OK,event_refresh_intent);
            finish();
        }

        return true;
    }

    public void onBackButtonClick(View view){
        _helper.close();

        Intent event_refresh_intent = new Intent();
        event_refresh_intent.putExtra("EventRefresh",true);
        setResult(RESULT_OK,event_refresh_intent);
        finish();
    }

    public  void closeActivity() {
        _helper.close();
        Intent event_refresh_intent = new Intent();
        event_refresh_intent.putExtra("EventRefresh",true);
        setResult(RESULT_OK,event_refresh_intent);
        finish();
    }

    private class RyoseiSearchListener implements  View.OnClickListener {
        @Override
        public void onClick(View view) {
            int count = 0;
            EditText input = findViewById(R.id.uketori_editTextTextPersonName);
            String input_name = input.getText().toString();
            input_name = input_name.replaceAll("　", "").replaceAll(" ", "");
            input_name = Normalizer.normalize(input_name, Normalizer.Form.NFKC);
            Pattern p = Pattern.compile("([0-9A-zぁ-んァ-ヶｱ-ﾝ\\u4E00-\\u9FFF\\u3005-\\u3007]+)"
                    // + " \\p{InHiragana}|" + " \\p{InKatakana}|"
                    // + " \\p{InCJKUnifiedIdeographs}+)"
                    , Pattern.COMMENTS);
            //if(input_name.matches( "^[A-zぁ-んァ-ヶｱ-ﾝﾞﾟ\u4E00-\u9FFF\u3005-\u3007]*$") ) {
            if(p.matcher(input_name).matches()) {
                search_show(input_name);
            }else if(count > 5){
                Toast.makeText(Double_Buttoned_Uketori.this, "漢字、ひらがな、カタカナしか使えません。", Toast.LENGTH_SHORT).show();
                count++;
            }else{
                Toast.makeText(Double_Buttoned_Uketori.this, "漢字、ひらがな、カタカナしか使えません。", Toast.LENGTH_SHORT).show();
                count++;
            }

        }
    }

    private class ListRyoseiClickListener implements AdapterView.OnItemClickListener{

        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Map<String ,String> item = (Map)parent.getItemAtPosition(position);
            //代理受け取りモード
            if(proxy_check) {
                if (Integer.parseInt(item.get("parcels_current_count")) == 0) {
                    String show = item.get("room_name") + "には現在荷物が一つも登録されていません。";
                    Toast.makeText(Double_Buttoned_Uketori.this, show, Toast.LENGTH_LONG).show();
                } else if(proxy_id_Str == null){
                    Toast.makeText(Double_Buttoned_Uketori.this, "代理受取人を設定してください。", Toast.LENGTH_LONG).show();
                }else   {
                    this.showProxyDialog(view, item.get("room_name"), item.get("id"),proxy_id_Str,proxy_room_Str,proxy_name_Str);

                }
            }
            //通常受け取りモード
            else{
                if(Integer.parseInt(item.get("parcels_current_count"))==0){
                    String show = item.get("room_name") + "には現在荷物が一つも登録されていません。";
                    Toast.makeText(Double_Buttoned_Uketori.this, show ,Toast.LENGTH_LONG).show();
                }else {
                    this.showDialog(view, item.get("room_name"), item.get("id"));}
                }
        }
        public void showDialog(View view,String owner_room_name,String owner_id) {
            DialogFragment dialogFragment = new Nimotsu_Uketori_Dialog();
            String[] newStr = owner_room_name.split("\\s+");
            Bundle args = new Bundle();
            args.putString("owner_room",newStr[0]);
            args.putString("owner_name",newStr[1]);
            args.putString("owner_id",owner_id);
            args.putString("release_staff_room",jimuto_room_Str);
            args.putString("release_staff_name",jimuto_name_Str);
            args.putString("release_staff_id",jimuto_id_Str);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Nimotsu_Uketori_Dialog");
        }
        public void showProxyDialog(View view,String owner_room_name,String owner_id,String proxy_id, String proxy_room , String proxy_name) {
            DialogFragment dialogFragment = new Nimotsu_Proxy_Uketori_Dialog();
            String[] newStr = owner_room_name.split("\\s+");
            Bundle args = new Bundle();
            args.putString("owner_room",newStr[0]);
            args.putString("owner_name",newStr[1]);
            args.putString("owner_id",owner_id);
            args.putString("release_staff_room",jimuto_room_Str);
            args.putString("release_staff_name",jimuto_name_Str);
            args.putString("release_staff_id",jimuto_id_Str);
            args.putString("proxy_room",proxy_room);
            args.putString("proxy_name",proxy_name);
            args.putString("proxy_id",proxy_id);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Nimotsu_Proxy_Uketori_Dialog");
        }

    }
   /* public class ListRyoseiClickListener implements AdapterView.OnItemClickListener{

        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Map<String ,String> item = (Map)parent.getItemAtPosition(position);
            if(Integer.parseInt(item.get("parcels_current_count"))==0){
                String show = item.get("room_name") + "には現在荷物が一つも登録されていません。";
                Toast.makeText(Double_Buttoned_Uketori.this, show ,Toast.LENGTH_LONG).show();
            }else {
                this.showDialog(view, item.get("room_name"), item.get("id"));


            }
        }
        public void showDialog(View view,String owner_room_name,String owner_id) {
            DialogFragment dialogFragment = new Nimotsu_Uketori_Dialog();
            String[] newStr = owner_room_name.split("\\s+");
            Bundle args = new Bundle();
            args.putString("owner_room",newStr[0]);
            args.putString("owner_name",newStr[1]);
            args.putString("owner_id",owner_id);
            args.putString("release_staff_room",jimuto_room_Str);
            args.putString("release_staff_name",jimuto_name_Str);
            args.putString("release_staff_id",jimuto_id_Str);

            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Nimotsu_Uketori_Dialog");


        }

    }
    */

    public class ProxySwitchListener extends Activity implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                proxy_room_name_text.setVisibility(View.VISIBLE);
                proxy_change_button.setVisibility(View.VISIBLE);
                //ConstraintLayout double_buttoned_uketori = (ConstraintLayout)findViewById(R.id.double_buttoned_uketori_constraintlayout);
                double_buttoned_uketori.setBackgroundColor(Color.rgb(255,200,180));
            } else {
                proxy_room_name_text.setVisibility(View.GONE);
                proxy_change_button.setVisibility(View.GONE);
                //ConstraintLayout double_buttoned_uketori = (ConstraintLayout)findViewById(R.id.double_buttoned_uketori_constraintlayout);
                double_buttoned_uketori.setBackgroundColor(Color.rgb(255,255,255));
            }
            proxy_check = isChecked;
        }
    }
    private class DoubleProxyChangeListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent proxy_intent = new Intent(Double_Buttoned_Uketori.this, Double_Proxy_Change.class);
            proxy_intent.putExtra("Proxy_name",proxy_room_Str + " " + proxy_name_Str);
            proxy_intent.putExtra("Proxyid",proxy_id_Str);
            startActivityForResult(proxy_intent,PROXYCHANGE_ACTIVITY);
        }
    }

}

