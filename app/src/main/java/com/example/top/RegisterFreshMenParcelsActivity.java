package com.example.top;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.top.ClickListener.OnOneClickListener;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class RegisterFreshMenParcelsActivity extends AppCompatActivity {

    String owner_ryosei_name = "";
    String owner_ryosei_room = "";
    String owner_ryosei_id = "";
    String staff_name = "";
    String staff_room = "";
    String staff_id = "";
    String placement = "";
    String detail = null;
    String showtext = "";
    int times = 1;
    private DatabaseHelper _helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_freshmen);

        Intent intent = getIntent();
        owner_ryosei_name = intent.getStringExtra("Owner_name");
        owner_ryosei_room = intent.getStringExtra("Owner_room");
        owner_ryosei_id = intent.getStringExtra("Owner_id");
        staff_name = intent.getStringExtra("Jimuto_name");
        staff_room = intent.getStringExtra("Jimuto_room");
        staff_id = intent.getStringExtra("Jimuto_id");
        placement = intent.getStringExtra("placement");
        Button buttonAdd = findViewById(R.id.touroku_others_add);
        buttonAdd.setOnClickListener(new RegisterFreshMenParcelsActivity.buttonAddListener());
        ImageButton buttonCancel = findViewById(R.id.register_others_go_back_button);
        TextView titletext = findViewById(R.id.register_others_go_back_text);
        buttonCancel.setOnClickListener(new RegisterFreshMenParcelsActivity.buttonCancelListener());
        //titletext.setOnClickListener(new RegisterOthersActivity.buttonCancelListener());
        TextView ShowRyoseiText = findViewById(R.id.touroku_others_text);
        showtext = owner_ryosei_room + " " + owner_ryosei_name + "に新入寮生の荷物登録します。";
        ShowRyoseiText.setText(showtext);
        //事務当番の名前を表示する
        TextView jimuto_name = findViewById(R.id.register_others_jimuto_show);
        jimuto_name.setText(staff_room + staff_name);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("1個");
        adapter.add("2個");
        adapter.add("3個");
        adapter.add("4個");
        adapter.add("5個");
        adapter.add("6個");
        adapter.add("7個");
        adapter.add("8個");
        Spinner spinner = (Spinner) findViewById(R.id.spinner_add_times);
        spinner.setAdapter(adapter);

        ActivityHelper.enableTransparentFooter(this);

    }

    private class buttonAddListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            EditText others_detail = findViewById(R.id.touroku_others_edit);
            EditText input = findViewById(R.id.register_search_ryosei_name);
            String fresh_men_number = others_detail.getText().toString();
            fresh_men_number = fresh_men_number.replaceAll("　", "").replaceAll(" ", "");
            fresh_men_number = Normalizer.normalize(fresh_men_number, Normalizer.Form.NFKC);
            fresh_men_number = trim_text(fresh_men_number);
            Pattern p = Pattern.compile("([0-9]+)", Pattern.COMMENTS);
            if (fresh_men_number == null) {
                Toast.makeText(RegisterFreshMenParcelsActivity.this, "1~3桁の面接番号を入力してください。", Toast.LENGTH_SHORT).show();
            } else if (p.matcher(fresh_men_number).matches()) {
                //新入寮生の荷物を追加
                _helper = new DatabaseHelper(getApplicationContext());
                SQLiteDatabase db = _helper.getWritableDatabase();
                Spinner spinner = findViewById(R.id.spinner_add_times);
                String selected_string = (String) spinner.getSelectedItem();
                int selected_int = Integer.parseInt(selected_string.substring(0, 1));
                //よくない実装。項目はstringでそこから一文字目をとってintに変換している。
                for (int i = 0; i < selected_int; i++) {
                    _helper.register(db, owner_ryosei_id, staff_id, staff_room, staff_name, Integer.parseInt(placement), fresh_men_number);
                }
                _helper.close();
                Toast.makeText(RegisterFreshMenParcelsActivity.this, owner_ryosei_room + " " + owner_ryosei_name + "に" + fresh_men_number + "の荷物を登録しました。", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterFreshMenParcelsActivity.this, "1~3桁の面接番号を入力してください。", Toast.LENGTH_SHORT).show();
            }
        }

        private String trim_text(String text) {
            int len = text.length();
            if (len > 3)
                len = 3;
            return text.substring(0, len);
        }
    }

    private class buttonCancelListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }
}