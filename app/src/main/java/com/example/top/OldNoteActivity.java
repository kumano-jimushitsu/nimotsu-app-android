package com.example.top;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class OldNoteActivity extends AppCompatActivity {

    String[] caption = {"aaa", "bcb"};
    int[] haiten = {3, 5};
    int[] categoryPoint = {32, 15};
    int[] averagePoint = {4, 52};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_note);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.old_note_tableLayout);
        for (int i = 0; i < caption.length; i++) {
            TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.old_note_table_row, null);
            TextView name = (TextView) tableRow.findViewById(R.id.rowtext1);
            name.setText(caption[i]);
            TextView point = (TextView) tableRow.findViewById(R.id.rowtext2);
            point.setText(Integer.toString(haiten[i]));
            TextView score = (TextView) tableRow.findViewById(R.id.rowtext3);
            score.setText(Integer.toString(categoryPoint[i]));
            TextView ave = (TextView) tableRow.findViewById(R.id.rowtext4);
            ave.setText(Integer.toString(averagePoint[i]));

            tableLayout.addView(tableRow);
        }


    }


}