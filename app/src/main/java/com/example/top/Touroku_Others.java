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
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;

import org.w3c.dom.Text;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.*;
import java.util.regex.Pattern;

public class Touroku_Others extends AppCompatActivity {

    private DatabaseHelper _helper;
    String owner_ryosei_name = "";
    String owner_ryosei_room = "";
    String owner_ryosei_id = "";
    String register_staff_name = "";
    String register_staff_room = "";
    String register_staff_id = "";
    String placement = "";
    String detail = null;
    String showtext = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touroku_others_layout);

        Intent intent = getIntent();
        owner_ryosei_name = intent.getStringExtra("Owner_name");
        owner_ryosei_room = intent.getStringExtra("Owner_room");
        owner_ryosei_id = intent.getStringExtra("Owner_id");
        register_staff_name = intent.getStringExtra("Jimuto_name");
        register_staff_room = intent.getStringExtra("Jimuto_room");
        register_staff_id = intent.getStringExtra("Jimuto_id");
        placement = intent.getStringExtra("placement");
        Button buttonAdd = findViewById(R.id.touroku_others_add);
        buttonAdd.setOnClickListener(new Touroku_Others.buttonAddListener());
        Button buttonCancel = findViewById(R.id.touroku_others_cancel);
        buttonCancel.setOnClickListener(new Touroku_Others.buttonCancelListener());
        TextView ShowRyoseiText = findViewById(R.id.touroku_others_text);
        showtext = owner_ryosei_room + " " +owner_ryosei_name + "にその他で荷物登録します。";
        ShowRyoseiText.setText(showtext);

    }

    private class buttonAddListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            EditText others_detail = findViewById(R.id.touroku_others_edit);
            EditText input = findViewById(R.id.touroku_editTextTextPersonName);
            String input_detail = others_detail.getText().toString();
            input_detail = input_detail.replaceAll("　", "").replaceAll(" ", "");
            input_detail = Normalizer.normalize(input_detail, Normalizer.Form.NFKC);
            Pattern p = Pattern.compile("([0-9A-zぁ-んァ-ヶｱ-ﾝ\\u4E00-\\u9FFF\\u3005-\\u3007]+)"
                    // + " \\p{InHiragana}|" + " \\p{InKatakana}|"
                    // + " \\p{InCJKUnifiedIdeographs}+)"
                    , Pattern.COMMENTS);
            //if(input_name.matches( "^[A-zぁ-んァ-ヶｱ-ﾝﾞﾟ\u4E00-\u9FFF\u3005-\u3007]*$") ) {
            if(p.matcher(input_detail).matches()) {
                //その他の荷物を追加
                _helper = new com.example.top.DatabaseHelper(getApplicationContext());
                SQLiteDatabase db = _helper.getWritableDatabase();
                _helper.addParcelOthers(db,owner_ryosei_id,owner_ryosei_room,owner_ryosei_name,
                        register_staff_id,register_staff_room,register_staff_name,Integer.parseInt(placement),input_detail);
                _helper.close();
                Toast.makeText(Touroku_Others.this, owner_ryosei_room +" "+owner_ryosei_name +"に"+input_detail+"の荷物を登録しました。", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(Touroku_Others.this, "漢字、ひらがな、カタカナしか使えません。", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class buttonCancelListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }
}