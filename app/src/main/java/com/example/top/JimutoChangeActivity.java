package com.example.top;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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


public class JimutoChangeActivity extends AppCompatActivity {

    //ArrayListを用意
    private final ArrayList<String> blocks_roomname_name = new ArrayList<>();
    private final ArrayList<String> blocks_ryosei_id = new ArrayList<>();
    private final List<Map<String, String>> show_list = new ArrayList<>();
    private final ArrayList<Integer> ryosei_parcels_count = new ArrayList<>();
    private final List<Map<String, String>> show_ryosei = new ArrayList<>();//表示する寮生
    private final List<String> show_block = new ArrayList<>();//全てのブロック
    private final List<String> show_room = new ArrayList<>();//全ての部屋or選択されたブロックの部屋
    private final String[] from = {"id", "room_name"};
    private final int[] to = {android.R.id.text2, android.R.id.text1};
    public DatabaseHelper _helper;
    String selectedBlock = null;
    String selectedRoom = null;
    Cursor cursor;
    private String jimuto_room_name = "";
    private String jimuto_id = null;
    private TouchSound touchsound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jimuto_change);

        touchsound = new TouchSound(this);

        ImageButton backbutton = findViewById(R.id.jimuto_change_go_back_button);
        backbutton.setOnClickListener(this::onBackButtonClick);

        //事務当番の名前を受け取る
        Intent intent = getIntent();
        jimuto_room_name = intent.getStringExtra("Jimuto_name");
        jimuto_id = intent.getStringExtra("Jimuto_id");

        //事務当番の名前を表示する
        TextView jimuto_name = findViewById(R.id.main_jimuto_show);
        jimuto_name.setText(jimuto_room_name);

        get_block();
        show_block();
        get_room(" ");
        Collections.sort(show_room);
        show_room();
        this.show_block_ryosei(null);//nullを渡すと全寮生を表示
        ListView listListener = findViewById(R.id.double_jimuto_change_ryosei_list);
        listListener.setOnItemClickListener(new ListRyoseiClickListener());
        ListView listenerblock = findViewById(R.id.jimuto_change_block_list);
        listenerblock.setOnItemClickListener(new JimutoChangeActivity.ListBlockClickListener());
        ListView listenerroom = findViewById(R.id.jimuto_change_room_list);
        listenerroom.setOnItemClickListener(new JimutoChangeActivity.ListRoomClickListener());
        ImageButton ryosei_search_button = findViewById(R.id.jimuto_change_search_ryosei_name_button);
        ryosei_search_button.setOnClickListener(new JimutoChangeActivity.RyoseiSearchListener());

        // DBヘルパーオブジェクトを生成。
        _helper = new DatabaseHelper(JimutoChangeActivity.this);
        SQLiteDatabase db = _helper.getWritableDatabase();

        // システムナビゲーションバーの色を変更
        ActivityHelper.enableTransparentFooter(this);
    }


    public void show_block_ryosei(String block) {
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        _helper = new DatabaseHelper(JimutoChangeActivity.this);
        SQLiteDatabase db = _helper.getWritableDatabase();
        String sql;
        // 主キーによる検索SQL文字列の用意。
        if (block == null) {
            //sql = "SELECT uid, room_name, ryosei_name,parcels_current_count FROM ryosei order by room_name asc;";
            //sql = "SELECT uid, room_name, ryosei_name,parcels_current_count FROM ryosei where status<10 order by random() LIMIT 50;";
            //キチガイなのでランダムに取得
            sql = "SELECT uid, room_name, ryosei_name,parcels_current_count FROM ryosei where status<10 order by room_name asc";
            //キチガイじゃないので
        } else {
            sql = "SELECT uid, room_name, ryosei_name ,parcels_current_count FROM ryosei WHERE status<10 and block_id = '" + block_to_id(block) + "' order by room_name asc;";
        }// SQLの実行。
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
            show_list.add(ryosei_raw);
        }
        cursor.close();
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter blocktoryoseiadapter = new SimpleAdapter(this, show_list, android.R.layout.simple_list_item_1, from, to);
        // ListViewにArrayAdapterを設定する
        ListView listView = findViewById(R.id.double_jimuto_change_ryosei_list);
        listView.setAdapter(blocktoryoseiadapter);
        ListView listListener = findViewById(R.id.double_jimuto_change_ryosei_list);
        listListener.setOnItemClickListener(new JimutoChangeActivity.ListRyoseiClickListener());
        get_room(block);
        show_room();
        _helper.close();
    }

    public void show_room_ryosei(String room) {
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT uid, room_name, ryosei_name, parcels_current_count FROM ryosei WHERE status<10 AND room_name = '" + room + "' order by room_name asc;";
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
        ListView listView = findViewById(R.id.double_jimuto_change_ryosei_list);
        listView.setAdapter(roomtoryoseiadapter);
        ListView listListener = findViewById(R.id.double_jimuto_change_ryosei_list);
        listListener.setOnItemClickListener(new JimutoChangeActivity.ListRyoseiClickListener());
        cursor.close();
    }

    public void show_block() {
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        ArrayAdapter blockadapter = new ArrayAdapter
                (this, android.R.layout.simple_list_item_1, show_block);
        // ListViewにArrayAdapterを設定する
        ListView blocklistView = findViewById(R.id.jimuto_change_block_list);
        blocklistView.setAdapter(blockadapter);
        ListView blocklistListener = findViewById(R.id.jimuto_change_block_list);
        blocklistListener.setOnItemClickListener(new JimutoChangeActivity.ListRyoseiClickListener());
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
        ListView roomlistView = findViewById(R.id.jimuto_change_room_list);
        roomlistView.setAdapter(blockadapter);
        ListView roomlistListener = findViewById(R.id.jimuto_change_room_list);
        roomlistListener.setOnItemClickListener(new JimutoChangeActivity.ListRoomClickListener());
    }

    public void get_room(String block) {
        show_room.clear();
        _helper = new DatabaseHelper(JimutoChangeActivity.this);
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql;
        if (block_to_id(block) == 0) {
            sql = "SELECT DISTINCT room_name FROM ryosei where status<10 order by room_name asc;";
        } else {
            sql = "SELECT DISTINCT room_name FROM ryosei WHERE status<10 and block_id = '" + block_to_id(block) + "' order by room_name asc;";
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
            show_room.add(cursor.getString(roomNameNote));
            room_raw.clear();
        }
        cursor.close();
    }

    public int block_to_id(String block) {
        int id = 0;
        if (block != null) {
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

    public void search_show(String name) {
        show_ryosei.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT uid, room_name, ryosei_name FROM ryosei WHERE " +
                "ryosei_name LIKE '%" + name + "%' " +
                "OR ryosei_name_kana LIKE '%" + name + "%' " +
                "OR ryosei_name_alphabet LIKE '%" + name + "%' " +
                "OR room_name LIKE '%" + name + "%' ";


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
            blocks_roomname_name.add(note);
            blocks_ryosei_id.add(ryosei_id);
            show_ryosei.add(ryosei_raw);
        }
        cursor.close();

        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        SimpleAdapter blocktoryoseiadapter = new SimpleAdapter
                (this,
                        show_ryosei,
                        android.R.layout.simple_list_item_1,
                        from,
                        to);
        // ListViewにArrayAdapterを設定する
        ListView listView = findViewById(R.id.double_jimuto_change_ryosei_list);
        listView.setAdapter(blocktoryoseiadapter);
        ListView listListener = findViewById(R.id.double_jimuto_change_ryosei_list);
        listListener.setOnItemClickListener(new JimutoChangeActivity.ListRyoseiClickListener());
        _helper.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent jimuto_intent = new Intent();
            if (jimuto_id == null) {
                Toast.makeText(JimutoChangeActivity.this, "事務当番を選択してください。", Toast.LENGTH_SHORT).show();
            } else {
                jimuto_intent.putExtra("Jimuto_room_name", jimuto_room_name);
                jimuto_intent.putExtra("Jimuto_id", jimuto_id);
                setResult(RESULT_OK, jimuto_intent);
                finish();
            }
        }
        return true;
    }

    public void onBackButtonClick(View view) {
        Intent jimuto_intent = new Intent();
        if (jimuto_id != null) {
            jimuto_intent.putExtra("Jimuto_room_name", jimuto_room_name);
            jimuto_intent.putExtra("Jimuto_id", jimuto_id);
        }
        setResult(RESULT_OK, jimuto_intent);
        finish();
    }

    //Dialogから帰ってきたのをここで受ける
    //使っていないが、jimuto_change_Dialogを参考にするため残している関係で残している
    public void onReturnValue(String value, String id) {
        TextView jimuto_name = findViewById(R.id.main_jimuto_show);
        jimuto_name.setText("ただいまの事務当番は " + value + " です。");
        jimuto_room_name = value;
        jimuto_id = id;

        Intent jimuto_intent = new Intent();
        jimuto_intent.putExtra("Jimuto_room_name", jimuto_room_name);
        jimuto_intent.putExtra("Jimuto_id", jimuto_id);
        setResult(RESULT_OK, jimuto_intent);
        finish();
    }

    public class ListBlockClickListener extends OnOneItemClickListener {
        @Override
        public void onOneItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedBlock = (String) parent.getItemAtPosition(position);
            show_block_ryosei(selectedBlock);
            get_room(selectedBlock);
            show_room();
            touchsound.registercursorblock();
        }
    }

    public class ListRoomClickListener extends OnOneItemClickListener {
        @Override
        public void onOneItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedRoom = (String) parent.getItemAtPosition(position);
            show_room_ryosei(selectedRoom);
            touchsound.registercursorroom();
        }
    }

    private class RyoseiSearchListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            int count = 0;
            EditText input = findViewById(R.id.jimuto_change_search_ryosei_name);
            String input_name = input.getText().toString();
            input_name = input_name.replaceAll("　", "").replaceAll(" ", "");
            input_name = Normalizer.normalize(input_name, Normalizer.Form.NFKC);
            Pattern p = Pattern.compile("([0-9A-zぁ-ゖァ-ヶｱ-ﾝ\\u4E00-\\u9FFF\\u3005-\\u3007]+)"
                    // + " \\p{InHiragana}|" + " \\p{InKatakana}|"
                    // + " \\p{InCJKUnifiedIdeographs}+)"
                    , Pattern.COMMENTS);
            //if(input_name.matches( "^[A-zぁ-んァ-ヶｱ-ﾝﾞﾟ\u4E00-\u9FFF\u3005-\u3007]*$") ) {
            if (p.matcher(input_name).matches()) {
                search_show(input_name);
                touchsound.playsoundsearch();
            } else {
                Toast.makeText(JimutoChangeActivity.this, "漢字、ひらがな、カタカナしか使えません。", Toast.LENGTH_SHORT).show();
                touchsound.playsounderror();
            }

        }
    }


    private class ListRyoseiClickListener extends OnOneItemClickListener {
        @Override
        public void onOneItemClick(AdapterView<?> parent, View view, int position, long id) {
            touchsound.registercursorryosei();
            Map<String, String> item = (Map) parent.getItemAtPosition(position);
            //this.showDialog(view,item.get("room_name"),item.get("id"));
            //参考になるので消さないが、ダイアログでの確認をせず更新するために変更

            //2/5ダイアログでの確認を省略する変更の際に追加↓
            TextView jimuto_name = findViewById(R.id.main_jimuto_show);
            jimuto_room_name = item.get("room_name");
            jimuto_id = item.get("id");
            jimuto_name.setText(jimuto_room_name);


            Intent jimuto_intent = new Intent();
            jimuto_intent.putExtra("Jimuto_room_name", jimuto_room_name);
            jimuto_intent.putExtra("Jimuto_id", jimuto_id);
            setResult(RESULT_OK, jimuto_intent);
            finish();


        }

        public void showDialog(View view, String room_name, String id) {
            Bundle args = new Bundle();
            args.putString("room_ryosei", room_name);
            args.putString("id", id);
            DialogFragment dialogFragment = new JimutoChangeDialog();
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Jimutou_Change_Dialog");
        }


    }
}
