package com.example.top;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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


public class Double_Proxy_Change extends AppCompatActivity {

    String selectedBlock = null;
    String selectedRoom = null;

    public DatabaseHelper _helper;
    Cursor cursor;

    //ArrayListを用意
    private ArrayList<String> blocks_roomname_name = new ArrayList<>();
    private ArrayList<String> blocks_ryosei_id = new ArrayList<>();
    private List<Map<String,String>> show_list = new ArrayList<>();
    private ArrayList<Integer> ryosei_parcels_count = new ArrayList<>();
    private List<Map<String,String>> show_ryosei = new ArrayList<>();//表示する寮生
    private List<String> show_block = new ArrayList<>();//全てのブロック
    private List<String> show_room = new ArrayList<>();//全ての部屋or選択されたブロックの部屋
    private String[] from={"id","room_name"};
    private int[] to = {android.R.id.text2,android.R.id.text1};
    private String proxy_room_name = "";
    private String proxy_id = null;
    private static final int PROXYCHANGE_ACTIVITY = 2001;
    private ConstraintLayout double_proxy_change;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.double_proxy_change_layout);

        Button backbutton = (Button) findViewById(R.id.double_proxy_change_go_back_button);
        backbutton.setOnClickListener(this::onBackButtonClick);

        //代理受取人の名前を受け取る
        Intent intent = getIntent();
        proxy_room_name = intent.getStringExtra("proxy_name");
        proxy_id = intent.getStringExtra("proxy_id");
        //代理受取人の名前を表示する
        TextView proxy_name = findViewById(R.id.double_proxyu_name_show);
        if (proxy_room_name == "") {
            proxy_name.setText("代理受取人が設定されていません。");
        } else{
            proxy_name.setText("ただいまの代理受取人は " + proxy_room_name + " です。");
        }
        get_block();
        show_block();
        get_room(" ");
        Collections.sort(show_room);
        show_room();
        this.show_block_ryosei(null);//nullを渡すと全寮生を表示
        ListView listListener = findViewById(R.id.double_proxy_change_ryosei_list);
        listListener.setOnItemClickListener(new ListRyoseiClickListener());
        ListView listenerblock = findViewById(R.id.double_proxy_change_block_list);
        listenerblock.setOnItemClickListener(new Double_Proxy_Change.ListBlockClickListener());
        ListView listenerroom = findViewById(R.id.double_proxy_change_room_list);
        listenerroom.setOnItemClickListener(new Double_Proxy_Change.ListRoomClickListener());
        Button ryosei_search_button = findViewById(R.id.proxy_name_search);
        ryosei_search_button.setOnClickListener(new Double_Proxy_Change.RyoseiSearchListener());

        // DBヘルパーオブジェクトを生成。
        _helper = new DatabaseHelper(Double_Proxy_Change.this);
        SQLiteDatabase db = _helper.getWritableDatabase();

        double_proxy_change = findViewById(R.id.double_proxy_constraintlayout);
        double_proxy_change.setBackgroundColor(Color.rgb(255,200,180));

    }
    public void show_ryosei (String block){
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        String sql;
        if(block==null){
            sql = "SELECT uid, room_name, ryosei_name, parcels_current_count FROM ryosei ;";
        }else {
            // 主キーによる検索SQL文字列の用意。
            sql = "SELECT uid, room_name, ryosei_name, parcels_current_count FROM ryosei WHERE block_id = '" + block + "';";
        }
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
            show_list.add(ryosei_raw);

        }
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter adapter = new SimpleAdapter
                (this,
                        show_list,
                        android.R.layout.simple_list_item_1,
                        from,
                        to);

        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.double_proxy_change_ryosei_list);
        listView.setAdapter(adapter);
        ListView listListener = findViewById(R.id.double_proxy_change_ryosei_list);
        listListener.setOnItemClickListener(new Double_Proxy_Change.ListRyoseiClickListener());
    }
    public void show_block_ryosei (String block){
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        _helper = new DatabaseHelper(Double_Proxy_Change.this);
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
            show_list.add(ryosei_raw);

        }
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter blocktoryoseiadapter = new SimpleAdapter
                (this,
                        show_list,
                        android.R.layout.simple_list_item_1,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.double_proxy_change_ryosei_list);
        listView.setAdapter(blocktoryoseiadapter);
        ListView listListener = findViewById(R.id.double_proxy_change_ryosei_list);
        listListener.setOnItemClickListener(new Double_Proxy_Change.ListRyoseiClickListener());
        get_room(block);
        show_room();
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
            show_list.add(ryosei_raw);
        }
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter roomtoryoseiadapter = new SimpleAdapter
                (this,
                        show_list,
                        android.R.layout.simple_list_item_1,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.double_proxy_change_ryosei_list);
        listView.setAdapter(roomtoryoseiadapter);
        ListView listListener = findViewById(R.id.double_proxy_change_ryosei_list);
        listListener.setOnItemClickListener(new Double_Proxy_Change.ListRyoseiClickListener());
    }
    public void show_block(){
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        ArrayAdapter blockadapter = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1, show_block);
        // ListViewにArrayAdapterを設定する
        ListView blocklistView = (ListView)findViewById(R.id.double_proxy_change_block_list);
        blocklistView.setAdapter(blockadapter);
        ListView blocklistListener = findViewById(R.id.double_proxy_change_block_list);
        blocklistListener.setOnItemClickListener(new Double_Proxy_Change.ListRyoseiClickListener());
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
        ListView roomlistView = (ListView)findViewById(R.id.double_proxy_change_room_list);
        roomlistView.setAdapter(blockadapter);
        ListView roomlistListener = findViewById(R.id.double_proxy_change_room_list);
        roomlistListener.setOnItemClickListener(new Double_Proxy_Change.ListRoomClickListener());
    }
    public void get_room(String block) {
        show_room.clear();
        _helper = new DatabaseHelper(Double_Proxy_Change.this);
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
            show_room.add(cursor.getString(roomNameNote));
            room_raw.clear();
        }
    }
    public int block_to_id(String block){
        int id = 0;
        if(block != null){
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
        }}
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
    public void search_show(String name){
        show_ryosei.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT uid, room_name, ryosei_name FROM ryosei WHERE " +
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
        SimpleAdapter blocktoryoseiadapter = new SimpleAdapter
                (this,
                        show_ryosei,
                        android.R.layout.simple_list_item_1,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.double_proxy_change_ryosei_list);
        listView.setAdapter(blocktoryoseiadapter);
        ListView listListener = findViewById(R.id.double_proxy_change_ryosei_list);
        listListener.setOnItemClickListener(new Double_Proxy_Change.ListRyoseiClickListener());
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent proxy_intent = new Intent();
            if (proxy_id == null){
                Toast.makeText(Double_Proxy_Change.this, "代理受取人を選択してください。", Toast.LENGTH_SHORT).show();
            }else{
                String[] newStr = proxy_room_name.split("\\s+");
                String proxy_room = newStr[0];
                String proxy_name = newStr[1];
                proxy_intent.putExtra("Proxy_room", proxy_room);
                proxy_intent.putExtra("Proxy_name", proxy_name);
                proxy_intent.putExtra("Proxy_id", proxy_id);
                setResult(RESULT_OK,proxy_intent);
                finish();
            }
        }
        return true;
    }

    public void onBackButtonClick(View view){
        Intent proxy_intent = new Intent();
        if (proxy_id == null){
            Toast.makeText(Double_Proxy_Change.this, "代理受取人を選択してください。", Toast.LENGTH_SHORT).show();
        }else{
            String[] newStr = proxy_room_name.split("\\s+");
            String proxy_room = newStr[0];
            String proxy_name = newStr[1];
            proxy_intent.putExtra("Proxy_room", proxy_room);
            proxy_intent.putExtra("Proxy_name", proxy_name);
            proxy_intent.putExtra("Proxy_id", proxy_id);
            setResult(RESULT_OK,proxy_intent);
            finish();
        }
    }

    public void onReturnValue(String value,String id) {
        TextView proxy_name =findViewById(R.id.double_proxyu_name_show);
        proxy_name.setText("ただいまの代理受取人は "+value+" です。");
        proxy_room_name = value;
        proxy_id = id;
    }


    private class RyoseiSearchListener implements  View.OnClickListener {
        @Override
        public void onClick(View view) {
            int count = 0;
            EditText input = findViewById(R.id.proxy_editTextTextPersonName);
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
                Toast.makeText(Double_Proxy_Change.this, "漢字、ひらがな、カタカナしか使えません。", Toast.LENGTH_SHORT).show();
                count++;
            }else{
                Toast.makeText(Double_Proxy_Change.this, "漢字、ひらがな、カタカナしか使えません。", Toast.LENGTH_SHORT).show();
                count++;
            }

        }
    }


    private class ListRyoseiClickListener implements AdapterView.OnItemClickListener{

        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Map<String ,String> item = (Map)parent.getItemAtPosition(position);
            this.showDialog(view,item.get("room_name"),item.get("id"));

        }
        public void showDialog(View view,String room_name,String id) {
            Bundle args = new Bundle();
            args.putString("room_ryosei",room_name);
            args.putString("id",id);
            DialogFragment dialogFragment = new Proxy_Change_Dialog();
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "proxy_Change_Dialog");
        }


    }
}