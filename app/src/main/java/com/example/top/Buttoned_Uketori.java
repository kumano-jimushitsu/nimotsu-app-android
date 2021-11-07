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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Buttoned_Uketori extends AppCompatActivity {

    int selectedBlock = 1;
    //表示するブロック 初期値は,
    // A1は1 A2は2...C34は8 臨キャパは9

    private DatabaseHelper _helper;
    Cursor cursor;

    //ArrayListを用意
    private ArrayList<String > ryosei_block = new ArrayList<>();
    private String jimuto_name_Str= "";
    private String jimuto_id_Str= "";
    private String jimuto_room_Str= "";
    private ArrayList<String> blocks_roomname_name = new ArrayList<>();
    private ArrayList<String> blocks_ryosei_id = new ArrayList<>();
    private ArrayList<Integer> ryosei_parcels_count = new ArrayList<>();
    private List<Map<String,String>> show_list = new ArrayList<>();
    private String[] from={"parcels_current_count","room_name"};
    private int[] to = {android.R.id.text2,android.R.id.text1};



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buttoned_uketori_layout);

        //事務当番の名前を受け取る
        Intent intent = getIntent();
        jimuto_name_Str = intent.getStringExtra("Jimuto_name");
        jimuto_id_Str = intent.getStringExtra("Jimuto_id");
        jimuto_room_Str = intent.getStringExtra("Jimuto_room");
        //事務当番の名前を表示する
        TextView jimuto_name =findViewById(R.id.  uketori_jimutou_name_show);
        jimuto_name.setText("ただいまの事務当番は " + jimuto_room_Str +" "+jimuto_name_Str+" です。");

        selectedBlock = 1;
        Button buttonA1=(Button)findViewById(R.id.Uketori_a1_tab);
        Button buttonA2=(Button)findViewById(R.id.Uketori_a2_tab);
        Button buttonA3=(Button)findViewById(R.id.Uketori_a3_tab);
        Button buttonA4=(Button)findViewById(R.id.Uketori_a4_tab);
        Button buttonB12=(Button)findViewById(R.id.Uketori_b12_tab);

        Buttoned_Uketori.BlockSelectListener listener = new Buttoned_Uketori.BlockSelectListener();
        buttonA1.setOnClickListener(listener);
        buttonA2.setOnClickListener(listener);
        buttonA3.setOnClickListener(listener);
        buttonA4.setOnClickListener(listener);
        buttonB12.setOnClickListener(listener);

        Button backbutton =(Button)findViewById(R.id.uketori_go_back_button);
        backbutton.setOnClickListener(this::onBackButtonClick);



        // DBヘルパーオブジェクトを生成。
        _helper = new com.example.top.DatabaseHelper(Buttoned_Uketori.this);

        SQLiteDatabase db = _helper.getWritableDatabase();

        this.show_ryosei(1);
        ListView listListener = findViewById(R.id.Uketori_ryousei_list_show);
        listListener.setOnItemClickListener(new ListItemClickListener());

    }

    private class BlockSelectListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.Uketori_a1_tab:
                    selectedBlock = 1;
                    break;
                case R.id.Uketori_a2_tab:
                    selectedBlock = 2;
                    break;
                case R.id.Uketori_a3_tab:
                    selectedBlock = 3;
                    break;
                case R.id.Uketori_a4_tab:
                    selectedBlock = 4;
                    break;
                case R.id.Uketori_b12_tab:
                    selectedBlock = 5;
                    break;
            }

            show_ryosei(selectedBlock);

        }
    }



  /*  public void show_ryosei (int block){
        ryosei_block =new ArrayList<String>();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT room_name, ryosei_name FROM ryosei WHERE block_id = '"+ String.valueOf(block) +"';" ;
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //ブロックの寮生を検索しArrayListに追加
        while(cursor.moveToNext()) {
            // データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String note = "";
            // カラムのインデックス値を取得。
            int dateNote = cursor.getColumnIndex("room_name");
            // カラムのインデックス値を元に実際のデータを取得。
            note += cursor.getString(dateNote);
            note += " ";
            int ryouseiNote = cursor.getColumnIndex("ryosei_name");
            note += cursor.getString(ryouseiNote);
            ryosei_block.add(note);
        }
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ryosei_block);

        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.Uketori_ryousei_list_show);
        listView.setAdapter(adapter);
        cursor.close();
    } */

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
        ListView listView = (ListView)findViewById(R.id.Uketori_ryousei_list_show);
        listView.setAdapter(adapter);
        ListView listListener = findViewById(R.id.Uketori_ryousei_list_show);
        listListener.setOnItemClickListener(new ListItemClickListener());
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


    private class ListItemClickListener implements AdapterView.OnItemClickListener{

        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Map<String ,String> item = (Map)parent.getItemAtPosition(position);
            if(Integer.parseInt(item.get("parcels_current_count"))==0){
                String show = item.get("room_name") + "には現在荷物が一つも登録されていません。";
                Toast.makeText(Buttoned_Uketori.this, show ,Toast.LENGTH_LONG).show();
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
}
