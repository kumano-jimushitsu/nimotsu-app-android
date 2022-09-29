package com.example.top;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.top.ClickListener.OnOneClickListener;

public class OthersActivity extends AppCompatActivity {


    private Button ryosei10button;
    private Button parcels10button;
    private Button events10button;
    private Button All10button;
    private Button ryosei30button;
    private Button parcels30button;
    private Button events30button;
    private Button All30button;

    private Button addtestryoseibutton;


    private DatabaseHelper _helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
        _helper = new com.example.top.DatabaseHelper(this);
        db = _helper.getWritableDatabase();

        All10button = findViewById(R.id.set_ALL_sharing_status10);
        ryosei10button = findViewById(R.id.set_ryosei_sharing_status10);
        parcels10button = findViewById(R.id.set_parcels_sharing_status10);
        events10button = findViewById(R.id.set_events_sharing_status10);

        set10Listener listener10 = new set10Listener();
        All10button.setOnClickListener(listener10);
        ryosei10button.setOnClickListener(listener10);
        parcels10button.setOnClickListener(listener10);
        events10button.setOnClickListener(listener10);

        All30button = findViewById(R.id.set_ALL_sharing_status30);
        ryosei30button = findViewById(R.id.set_ryosei_sharing_status30);
        parcels30button = findViewById(R.id.set_parcels_sharing_status30);
        events30button = findViewById(R.id.set_events_sharing_status30);

        set30Listener listener30 = new set30Listener();
        All30button.setOnClickListener(listener30);
        ryosei30button.setOnClickListener(listener30);
        parcels30button.setOnClickListener(listener30);
        events30button.setOnClickListener(listener30);

        addtestryoseibutton = findViewById(R.id.add_test_ryosei);
        addTestRyoseiListener testryoseilistener = new addTestRyoseiListener();
        addtestryoseibutton.setOnClickListener(testryoseilistener);


    }

    private class set10Listener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            final EditText editText = new EditText(OthersActivity.this);
            editText.setHint("enter OK");
            new AlertDialog.Builder(OthersActivity.this).setTitle("set10").setMessage("本当に実行する場合は『OK』と入力してください。").setView(editText).setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String input = editText.getText().toString();
                    if (input.equals("OK")) {
                        Toast.makeText(OthersActivity.this, "実行されます。", Toast.LENGTH_SHORT).show();
                        if (view == All10button) {
                            db = _helper.getWritableDatabase();
                            _helper.setAllSharingStatus10(db);
                        } else if (view == ryosei10button) {
                            db = _helper.getWritableDatabase();
                            _helper.setRyoseiSharingStatus10(db);
                        } else if (view == parcels10button) {
                            db = _helper.getWritableDatabase();
                            _helper.setParcelsSharingStatus10(db);
                        } else if (view == events10button) {
                            db = _helper.getWritableDatabase();
                            _helper.setEventsSharingStatus10(db);
                        }

                    } else {
                        Toast.makeText(OthersActivity.this, "実行されませんでした。", Toast.LENGTH_SHORT).show();
                    }
                }
            }).show();

        }
    }

    private class set30Listener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            final EditText editText = new EditText(OthersActivity.this);
            editText.setHint("enter OK");
            new AlertDialog.Builder(OthersActivity.this).setTitle("set30").setMessage("本当に実行する場合は『OK』と入力してください。").setView(editText).setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String input = editText.getText().toString();
                    if (input.equals("OK")) {
                        Toast.makeText(OthersActivity.this, "実行されます。", Toast.LENGTH_SHORT).show();
                        if (view == All30button) {
                            db = _helper.getWritableDatabase();
                            _helper.setAllSharingStatus30(db);
                        } else if (view == ryosei30button) {
                            db = _helper.getWritableDatabase();
                            _helper.setRyoseiSharingStatus30(db);
                        } else if (view == parcels30button) {
                            db = _helper.getWritableDatabase();
                            _helper.setParcelsSharingStatus30(db);
                        } else if (view == events30button) {
                            db = _helper.getWritableDatabase();
                            _helper.setEventsSharingStatus30(db);
                        }

                    } else {
                        Toast.makeText(OthersActivity.this, "実行されませんでした。", Toast.LENGTH_SHORT).show();
                    }
                }
            }).show();

        }


    }

    private class addTestRyoseiListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            _helper = new DatabaseHelper(OthersActivity.this);
            SQLiteDatabase db = _helper.getWritableDatabase();
            _helper.add_test_ryosei(db);
        _helper.close();
        }
    }

}