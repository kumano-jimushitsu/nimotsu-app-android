package com.example.top;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.top.ClickListener.OnOneClickListener;

public class OthersActivity extends AppCompatActivity {


    private Button ryosei10button;
    private Button parcels10button;
    private Button events10button;
    private Button All10button;
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

        setRyosei10Listener listener = new setRyosei10Listener();
        All10button.setOnClickListener(listener);
        ryosei10button.setOnClickListener(listener);
        parcels10button.setOnClickListener(listener);
        events10button.setOnClickListener(listener);
    }

    private class setRyosei10Listener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            if(view==All10button){
            Toast.makeText(OthersActivity.this, "All。", Toast.LENGTH_SHORT).show();
        }else if (view==ryosei10button){

                Toast.makeText(OthersActivity.this, "ryosei。", Toast.LENGTH_SHORT).show();
            }
        }}
}