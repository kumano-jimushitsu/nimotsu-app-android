package com.example.top;

        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.view.KeyEvent;
        import android.view.View;
        import android.widget.AdapterView;
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


public class Jimuto_Change extends AppCompatActivity {

    int selectedBlock = 1;
    //表示するブロック 初期値は,
    // A1は1 A2は2...C34は8 臨キャパは9

    private DatabaseHelper _helper;
    Cursor cursor;

    //ArrayListを用意
    private ArrayList<String> blocks_roomname_name = new ArrayList<>();
    private ArrayList<String> blocks_ryosei_id = new ArrayList<>();
    private List<Map<String,String>> show_list = new ArrayList<>();
    private String[] from={"id","room_name"};
    private int[] to = {android.R.id.text2,android.R.id.text1};
    private String jimuto_room_name = "";
    private String jimuto_id = "";



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jimuto_change_layout);

        Button backbutton =(Button)findViewById(R.id.jimuto_go_back_button);
        backbutton.setOnClickListener(this::onBackButtonClick);

        //事務当番の名前を受け取る
        Intent intent = getIntent();
        jimuto_room_name = intent.getStringExtra("Jimuto_name");
        jimuto_id = intent.getStringExtra("Jimuto_id");
        //事務当番の名前を表示する
        TextView jimuto_name =findViewById(R.id.jimutou_name_show);
        jimuto_name.setText("ただいまの事務当番は "+jimuto_room_name+" です。");

        selectedBlock = 1;
        Button buttonA1=(Button)findViewById(R.id.change_a1_tab);
        Button buttonA2=(Button)findViewById(R.id.change_a2_tab);
        Button buttonA3=(Button)findViewById(R.id.change_a3_tab);
        Button buttonA4=(Button)findViewById(R.id.change_a4_tab);
        Button buttonB12=(Button)findViewById(R.id.change_b12_tab);

        Jimuto_Change.BlockSelectListener listener = new Jimuto_Change.BlockSelectListener();
        buttonA1.setOnClickListener(listener);
        buttonA2.setOnClickListener(listener);
        buttonA3.setOnClickListener(listener);
        buttonA4.setOnClickListener(listener);
        buttonB12.setOnClickListener(listener);


        ListView listListener = findViewById(R.id.jimutou_ryousei_list_show);
        listListener.setOnItemClickListener(new ListItemClickListener());

        // DBヘルパーオブジェクトを生成。
        _helper = new com.example.top.DatabaseHelper(Jimuto_Change.this);
        SQLiteDatabase db = _helper.getWritableDatabase();
        this.show_ryosei(1);

    }

    private class BlockSelectListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.change_a1_tab:
                    selectedBlock = 1;
                    break;
                case R.id.change_a2_tab:
                    selectedBlock = 2;
                    break;
                case R.id.change_a3_tab:
                    selectedBlock = 3;
                    break;
                case R.id.change_a4_tab:
                    selectedBlock = 4;
                    break;
                case R.id.change_b12_tab:
                    selectedBlock = 5;
                    break;
            }
            show_ryosei(selectedBlock);
        }
    }



    public void show_ryosei (int block){
        show_list.clear();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 主キーによる検索SQL文字列の用意。
        String sql = "SELECT _id, room_name, ryosei_name FROM ryosei WHERE block_id = "+ String.valueOf(block) +";" ;
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
        ListView listView = (ListView)findViewById(R.id.jimutou_ryousei_list_show);
        listView.setAdapter(adapter);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // 戻るボタンの処理
            Intent jimuto_intent = new Intent();
            jimuto_intent.putExtra("Jimuto_room_name", jimuto_room_name);
            jimuto_intent.putExtra("Jimuto_id", jimuto_id);
            setResult(RESULT_OK,jimuto_intent);
            finish();
        }

        return true;
    }

    public void onBackButtonClick(View view){

        Intent jimuto_intent = new Intent();
        jimuto_intent.putExtra("Jimuto_room_name", jimuto_room_name);
        jimuto_intent.putExtra("Jimuto_id", jimuto_id);
        setResult(RESULT_OK,jimuto_intent);
        finish();
    }

    public void onReturnValue(String value,String id) {
        TextView jimuto_name =findViewById(R.id.jimutou_name_show);
        jimuto_name.setText("ただいまの事務当番は "+value+" です。");
        jimuto_room_name = value;
        jimuto_id = id;
    }


    private class ListItemClickListener implements AdapterView.OnItemClickListener{

        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Map<String ,String> item = (Map)parent.getItemAtPosition(position);
            this.showDialog(view,item.get("room_name"),item.get("id"));

        }
        public void showDialog(View view,String room_name,String id) {
            Bundle args = new Bundle();
            args.putString("room_ryosei",room_name);
            args.putString("id",id);
            DialogFragment dialogFragment = new Jimuto_Change_Dialog();
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Jimutou_Change_Dialog");
        }


    }
}
