package com.example.top;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.top.ClickListener.OnOneClickListener;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class RegisterOthersActivity extends AppCompatActivity {

    private DatabaseHelper _helper;
    String owner_ryosei_name = "";
    String owner_ryosei_room = "";
    String owner_ryosei_id = "";
    String staff_name = "";
    String staff_room = "";
    String staff_id = "";
    String placement = "";
    String detail = null;
    String showtext = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_others);

        Intent intent = getIntent();
        owner_ryosei_name = intent.getStringExtra("Owner_name");
        owner_ryosei_room = intent.getStringExtra("Owner_room");
        owner_ryosei_id = intent.getStringExtra("Owner_id");
        staff_name = intent.getStringExtra("Jimuto_name");
        staff_room = intent.getStringExtra("Jimuto_room");
        staff_id = intent.getStringExtra("Jimuto_id");
        placement = intent.getStringExtra("placement");
        Button buttonAdd = findViewById(R.id.touroku_others_add);
        buttonAdd.setOnClickListener(new RegisterOthersActivity.buttonAddListener());
        ImageButton buttonCancel = findViewById(R.id.register_others_go_back_button);
        TextView titletext = findViewById(R.id.register_others_go_back_text);
        buttonCancel.setOnClickListener(new RegisterOthersActivity.buttonCancelListener());
        //titletext.setOnClickListener(new RegisterOthersActivity.buttonCancelListener());
        TextView ShowRyoseiText = findViewById(R.id.touroku_others_text);
        showtext = owner_ryosei_room + " " + owner_ryosei_name + "にその他で荷物登録します。";
        ShowRyoseiText.setText(showtext);
        //事務当番の名前を表示する
        TextView jimuto_name = findViewById(R.id.register_others_jimuto_show);
        jimuto_name.setText(staff_room + staff_name);

        ActivityHelper.enableTransparentFooter(this);

    }

    private class buttonAddListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            EditText others_detail = findViewById(R.id.touroku_others_edit);
            EditText input = findViewById(R.id.register_search_ryosei_name);
            String input_detail = others_detail.getText().toString();
            input_detail = input_detail.replaceAll("　", "").replaceAll(" ", "");
            input_detail = Normalizer.normalize(input_detail, Normalizer.Form.NFKC);
            input_detail = trim_text(input_detail);
            Pattern p = Pattern.compile("([0-9A-zぁ-ゖァ-ヶｱ-ﾝ\\u4E00-\\u9FFF\\u3005-\\u3007]+)"
                    // + " \\p{InHiragana}|" + " \\p{InKatakana}|"
                    // + " \\p{InCJKUnifiedIdeographs}+)"
                    , Pattern.COMMENTS);
            //if(input_name.matches( "^[A-zぁ-んァ-ヶｱ-ﾝﾞﾟ\u4E00-\u9FFF\u3005-\u3007]*$") ) {
            if(p.matcher(input_detail).matches()) {
                //その他の荷物を追加
                _helper = new com.example.top.DatabaseHelper(getApplicationContext());
                SQLiteDatabase db = _helper.getWritableDatabase();
                _helper.register(db, owner_ryosei_id, staff_id, staff_room, staff_name, Integer.parseInt(placement), input_detail);
                _helper.close();
                Toast.makeText(RegisterOthersActivity.this, owner_ryosei_room + " " + owner_ryosei_name + "に" + input_detail + "の荷物を登録しました。", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(RegisterOthersActivity.this, "漢字、ひらがな、カタカナしか使えません。", Toast.LENGTH_SHORT).show();
            }
        }
        private String trim_text(String text){
            int len=text.length();
            if(len>200)len=200;
            return text.substring(0,len);
        }
    }

    private class buttonCancelListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }
}