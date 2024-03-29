package com.example.top;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.top.ClickListener.OnOneClickListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class OldNoteActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    TextView mShowSelectedDateTextFrom;
    TextView mShowSelectedDateTextTo;
    private ImageButton mPickDateButton;
    private Button searchShowButton;
    private Spinner buildings;

    private DatabaseHelper _helper;
    // values/colors.xmlより設定値を取得するために利用。
    private Resources r;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_note);


        r = getApplicationContext().getResources();

        _helper = new DatabaseHelper(OldNoteActivity.this);
        SQLiteDatabase db = _helper.getWritableDatabase();


        //materialdatepick

        // now register the text view and the button with
        // their appropriate IDs
        mPickDateButton = findViewById(R.id.datebutton);
        searchShowButton = findViewById(R.id.search_show_button);
        mShowSelectedDateTextFrom = findViewById(R.id.show_selected_date_from);
        mShowSelectedDateTextTo = findViewById(R.id.show_selected_date_to);
        buildings = findViewById(R.id.spinner_buildings);

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        Date threedaysago = calendar.getTime();
        mShowSelectedDateTextFrom.setText(sdf.format(threedaysago));
        mShowSelectedDateTextTo.setText(sdf.format(today));
        dateSelectListener dateListener = new dateSelectListener();
        mPickDateButton.setOnClickListener(dateListener);
        searchAndShowListener searchandshowlistener = new searchAndShowListener();
        searchShowButton.setOnClickListener(searchandshowlistener);
        ImageButton go_back_button = findViewById(R.id.old_note_go_back_button);
        go_back_button.setOnClickListener(this::onBackButtonClick);
        //最初の表示
        ArrayList<Map<String, String>> result = _helper.showOldNote(buildings.getSelectedItemPosition(), mShowSelectedDateTextFrom.getText().toString(), mShowSelectedDateTextTo.getText().toString(), db);
        showNote(result);
    }

    public void showNote(ArrayList<Map<String, String>> input) {


        TableLayout tableLayout = (TableLayout) findViewById(R.id.old_note_tableLayout);
        for (int i = tableLayout.getChildCount(); i > 1; i--) {
            tableLayout.removeViewAt(i - 1);
        }
        for (int i = 0; i < input.size(); i++) {
            Map<String, String> row_data = input.get(i);
            TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.old_note_table_row, null);
            TextView registertime = (TextView) tableRow.findViewById(R.id.old_note_table_row_register_time);
            registertime.setText(row_data.get("register_datetime"));
            TextView owner = (TextView) tableRow.findViewById(R.id.old_note_table_row_owner);
            owner.setText(row_data.get("owner"));
            TextView register_staff = (TextView) tableRow.findViewById(R.id.old_note_table_row_register_staff);
            register_staff.setText(row_data.get("register_staff"));
            TextView placement = (TextView) tableRow.findViewById(R.id.old_note_table_row_placement);
            placement.setText(row_data.get("placement"));
            TextView release_datetime = (TextView) tableRow.findViewById(R.id.old_note_table_row_release_datetime);
            release_datetime.setText(row_data.get("release_datetime"));
            TextView receiver = (TextView) tableRow.findViewById(R.id.old_note_table_row_receiver);
            receiver.setText(row_data.get("receiver"));
            TextView release_staff = (TextView) tableRow.findViewById(R.id.old_note_table_row_release_staff);
            release_staff.setText(row_data.get("release_staff"));
            TextView lost_datetime = (TextView) tableRow.findViewById(R.id.old_note_table_row_lost_datetime);
            lost_datetime.setText(row_data.get("lost_datetime"));
            tableLayout.addView(tableRow);
            if ((i+1)%2 == 0) {
                int color = getResources().getColor(R.color.verylightgray);
                registertime.setBackgroundColor(color);
                owner.setBackgroundColor(color);
                register_staff.setBackgroundColor(color);
                placement.setBackgroundColor(color);
                release_datetime.setBackgroundColor(color);
                receiver.setBackgroundColor(color);
                release_staff.setBackgroundColor(color);
                lost_datetime.setBackgroundColor(color);
            } else {
                //色は塗らない
            }
        }


    }

    public String[] dateConverter(String[] inputdate) {
        Date date = new Date();
        SimpleDateFormat onlyYear = new SimpleDateFormat("yyyy", Locale.getDefault());
        String thisYear = onlyYear.format(date);
        String fromDateStr = inputdate[0];
        String toDateStr = inputdate[1];
        String[] outputDate = new String[2];
        if (fromDateStr.contains("年")) {
            fromDateStr = fromDateStr.replaceAll("[年月]", "/");
            fromDateStr = fromDateStr.replaceAll("[日]", "");
        } else
            fromDateStr = thisYear + "/" + fromDateStr.replaceAll("[年月]", "/");
        fromDateStr = fromDateStr.replaceAll("[日]", "");
        if (toDateStr.contains("年")) {
            toDateStr = toDateStr.replaceAll("[年月]", "/");
            toDateStr = toDateStr.replaceAll("[日]", "");
        } else
            toDateStr = thisYear + "/" + toDateStr.replaceAll("[年月]", "/");
        toDateStr = toDateStr.replaceAll("[日]", "");

        SimpleDateFormat gottenDateFormat = new SimpleDateFormat("yyyy/M/d", Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        fromDateStr = outputDateFormat.format(gottenDateFormat.parse(fromDateStr, pos));
        pos = new ParsePosition(0);
        toDateStr = outputDateFormat.format(gottenDateFormat.parse(toDateStr, pos));
        outputDate[0] = fromDateStr;
        outputDate[1] = toDateStr;
        return outputDate;

    }

    public class dateSelectListener extends OnOneClickListener {
        @Override
        public void onOneClick(View v) {
            // now create instance of the material date picker
            // builder make sure to add the "dateRangePicker"
            // which is material date range picker which is the
            // second type of the date picker in material design
            // date picker we need to pass the pair of Long
            // Long, because the start date and end date is
            // store as "Long" type value
            MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();

            // now define the properties of the
            // materialDateBuilder
            materialDateBuilder.setTitleText("表示範囲選択");

            // now create the instance of the material date
            // picker
            final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
            materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onPositiveButtonClick(Object selection) {

                    // if the user clicks on the positive
                    // button that is ok button update the
                    // selected date
                    String[] dates = materialDatePicker.getHeaderText().split("～");
                    String[] resultDates = dateConverter(dates);
                    mShowSelectedDateTextFrom.setText(resultDates[0]);
                    mShowSelectedDateTextTo.setText(resultDates[1]);

                    // in the above statement, getHeaderText
                    // will return selected date preview from the
                    // dialog

                }
            });
        }

    }

    public class searchAndShowListener extends OnOneClickListener {
        @Override
        public void onOneClick(View v) {
            progressDialog = new ProgressDialog(OldNoteActivity.this);
            progressDialog.setTitle("タイトル");
            progressDialog.setMessage("メッセージ");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            SQLiteDatabase db = _helper.getWritableDatabase();
            ArrayList<Map<String, String>> result = _helper.showOldNote(buildings.getSelectedItemPosition(), mShowSelectedDateTextFrom.getText().toString(), mShowSelectedDateTextTo.getText().toString(), db);

            showNote(result);
            // 時間のかかる処理

            progressDialog.dismiss();
        }

    }

    public void showNote(int building, String fromData, String toData, DatabaseHelper db) {
        String date_condition = "";
        String block_condition;
        if (building <= 4) {
            block_condition = "1<= ryosei.block_id AND ryosei.block_id <5";
        } else if (4 < building && building <= 7) {
            block_condition = "4< ryosei.block_id AND ryosei.block_id <8";
        } else if (4 < building && building <= 7) {
            block_condition = "8<= ryosei.block_id AND ryosei.block_id <10";
        } else {
            block_condition = " ryosei.block_id =10";
        }
        String sql = "SELECT * FROM parcels WHERE block inner join ryosei on parcels.owner_uid" + "= ryosei.uid where " + block_condition + " AND " + date_condition;
    }

    public class dataSelectListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            //constraintsBuilder.setStart(CalendarConstraints.Builder.DEFAULT_START);
            //constraintsBuilder.setEnd(CalendarConstraints.Builder.DEFAULT_END);
            builder.setCalendarConstraints(constraintsBuilder.build());
            MaterialDatePicker<?> picker = builder.build();
            picker.show(getSupportFragmentManager(), picker.toString());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }

    public void onBackButtonClick(View view) {
        finish();
    }


}
