package com.example.top;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.top.ClickListener.OnOneClickListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TwoCheckBoxesNightDutyActivity extends AppCompatActivity{
    //表示用のデータを格納するリスト
    public List<TwoCheckBoxesNightDutyActivity.Data> dataListA = new ArrayList<>();
    public List<TwoCheckBoxesNightDutyActivity.Data> dataListB = new ArrayList<>();
    public List<TwoCheckBoxesNightDutyActivity.Data> dataListC = new ArrayList<>();
    public List<TwoCheckBoxesNightDutyActivity.Data> dataListD = new ArrayList<>();

    public String staff_room = "";
    public String staff_ryosei = "";
    public String staff_id = "";

    public String today;

    //説明文　切り替えボタン
    public TextView title;
    public TextView explain;
    public TextView explain_sub;
    public Button button_phase1;
    public Button button_phase2;

    public ListView listViewA;
    public ListView listViewB;
    public ListView listViewC;
    public ListView listViewD;

    private TouchSound touchsound;
    private DatabaseHelper _helper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_check_boxes_night_duty);
        touchsound = new TouchSound(this);
        // 事務当番の名前を受け取る
        //事務当番の名前を受け取る
        Intent intent = getIntent();
        staff_room = intent.getStringExtra("Jimuto_name");
        staff_ryosei = intent.getStringExtra("Jimuto_id");
        staff_id = intent.getStringExtra("Jimuto_room");
        // 事務当番の名前を表示
        TextView jimuto_name = findViewById(R.id.main_jimuto_show);
        jimuto_name.setText(staff_id);
        //日付けの取得
        DateFormat dateFormat = new SimpleDateFormat("MM/dd");
        Date date = new Date();
        today = dateFormat.format(date);


        listViewA = findViewById(R.id.listA);
        listViewB = findViewById(R.id.listB);
        listViewC = findViewById(R.id.listC);
        listViewD = findViewById(R.id.listD);

        ImageButton go_back_button = findViewById(R.id.tomari_go_back_button);
        go_back_button.setOnClickListener(this::onBackButtonClick);

        title = findViewById(R.id.night_duty_title);
        title.setText("泊まり事務当ー①現物確認");
        explain = findViewById(R.id.tomari_explanation_1);
        explain_sub = findViewById(R.id.tomari_explanation_2);
        button_phase1 = findViewById(R.id.tomari_result_show_button_1);//現物確認ボタン
        button_phase2 = findViewById(R.id.tomari_result_show_button_2);//札確認ボタン
        //説明文とボタン部分を指定

        import_parcels_data();
        showListView();
        ActivityHelper.enableTransparentFooter(this);
    }



    public void import_parcels_data(){
        String roomName, ryoseiName, ryoseiUid;
        int placement_id, is_lost, is_operation_error;
        String placement, lostdatetime, parcelsUid, note;
        List<Map<String, String>> onesParcels = null;
        int block_id;
        Cursor blockCursor;

        _helper = new DatabaseHelper(TwoCheckBoxesNightDutyActivity.this);
        SQLiteDatabase db = _helper.getWritableDatabase();
        String sql = "select * from parcels where is_released = 0 AND is_deleted = 0 AND is_operation_error = 0 ORDER BY owner_room_name asc,owner_ryosei_name asc ;";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);

        // A棟から臨キャパの処理を行う。
        while (cursor.moveToNext()) {
            ryoseiUid = cursor.getString(cursor.getColumnIndex("owner_uid"));
            String sql_get_block_from_owneruid = "SELECT block_id FROM ryosei WHERE uid ='" + ryoseiUid + "';";
            blockCursor = db.rawQuery(sql_get_block_from_owneruid, null);
            blockCursor.moveToFirst();
            block_id = blockCursor.getInt(blockCursor.getColumnIndex("block_id"));
            blockCursor.close();
            roomName = cursor.getString(cursor.getColumnIndex("owner_room_name"));
            ryoseiName = cursor.getString(cursor.getColumnIndex("owner_ryosei_name"));
            parcelsUid = cursor.getString(cursor.getColumnIndex("uid"));
            placement_id = cursor.getInt(cursor.getColumnIndex("placement"));
            is_lost = cursor.getInt(cursor.getColumnIndex("is_lost"));
            switch (placement_id) {
                case 0:
                    placement = "普通";
                    break;
                case 1:
                    placement = "冷蔵";
                    break;
                case 2:
                    placement = "冷凍";
                    break;
                case 3:
                    placement = "大型";
                    break;
                case 4:
                    placement = "不在票";
                    break;
                case 5:
                    placement = "その他";
                    break;
                case 6:
                    placement = "新入寮生";
                    break;
                default:
                    placement = "unknown";
            }


            lostdatetime = cursor.getString(cursor.getColumnIndex("lost_datetime"));
            onesParcels = _helper.nimotsuCountOfRyosei(db, ryoseiUid);
            TwoCheckBoxesNightDutyActivity.Data data = new TwoCheckBoxesNightDutyActivity.Data();
            data.setParcelsAttribute(placement);
            data.setRoomName(roomName);
            data.setRyoseiName(ryoseiName);
            data.setParcelUid(parcelsUid);
            data.setLostDateTime(lostdatetime);//lost_datetimeに最終確認できた時間を入れている　カラム名の変更が手間だったため
            data.setExistChecked(false);

            //ブロックごとに仕分けをする。
            if (block_id >= 1 && block_id < 5) {
                dataListA.add(data);
            } else if (block_id >= 5 && block_id < 8) {
                dataListB.add(data);
            } else if (block_id >= 8 && block_id < 10) {
                dataListC.add(data);
            } else if (block_id == 10) {
                dataListD.add(data);
            }
        }
        cursor.close();

    }

    public void showListView(){

        // リストにサンプル用のデータを受け渡す
        ListAdapter adapterA = new ListAdapter(this, dataListA, "A");
        ListAdapter adapterB = new ListAdapter(this, dataListB, "B");
        ListAdapter adapterC = new ListAdapter(this, dataListC, "C");
        ListAdapter adapterD = new ListAdapter(this, dataListD, "D");
        listViewA.setAdapter(adapterA);
        listViewB.setAdapter(adapterB);
        listViewC.setAdapter(adapterC);
        listViewD.setAdapter(adapterD);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 戻るボタンの処理
            Intent event_refresh_intent = new Intent();
            event_refresh_intent.putExtra("EventRefresh", true);
            setResult(RESULT_OK, event_refresh_intent);
            finish();
        }
        return true;
    }

    public void onBackButtonClick(View view) {

        Intent event_refresh_intent = new Intent();
        event_refresh_intent.putExtra("EventRefresh", true);
        setResult(RESULT_OK, event_refresh_intent);
        finish();
    }

    class UpdateParcelTable extends OnOneClickListener {
        @Override
        public void onOneClick(View view){
            touchsound.playsoundone();
            List<TwoCheckBoxesNightDutyActivity.Data> joinedDataList = dataListA;
            joinedDataList.addAll(dataListB);
            joinedDataList.addAll(dataListC);
            joinedDataList.addAll(dataListD);


            this.showButtomDialog(view, joinedDataList);
        }

        public void showButtomDialog(View view, List<Data> dataAll) {
            DialogFragment dialogFragment = new Night_Duty_AllCheck_Dialog();
            Bundle args = new Bundle();
            args.putString("register_staff_room", staff_room);
            args.putString("register_staff_name", staff_ryosei);
            args.putString("register_staff_id", staff_id);
            args.putStringArrayList("dataAll", dataAll);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Duty_Night_Dialog");
        }
    }


    // データ格納用クラス
    class Data {
        private String roomName;
        private String ryoseiName;
        private String parcelsAttribute;
        private String parcelsUid;
        private String lostDateTime;
        private Boolean exsitCheckdata = false;
        private Boolean lostCheckdata = false;
        private String note;
        private int is_lost;
        private int is_checked;
        //0 チェックなし 1現物あり 2現物なし
        private String show_lost_datetime;
        //外部に出力する値。listviewの値を保持するために一時的に保持する。

        public void setParcelUid(String uid) {
            this.parcelsUid = uid;
        }

        public void setExistChecked(Boolean b) {
            exsitCheckdata = b;
        }

        public void setLostChecked(Boolean b) {
            lostCheckdata = b;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String room) {
            this.roomName = room;
        }

        public String getRyoseiName() {
            return ryoseiName;
        }

        public int getIs_lost() {
            return is_lost;
        }

        public void setRyoseiName(String ryosei) {
            this.ryoseiName = ryosei;
        }

        public String getNote() {
            return note;
        }

        public String getParcelsAttribute() {
            return parcelsAttribute;
        }

        public void setParcelsAttribute(String parcels) {
            this.parcelsAttribute = parcels;
        }

        public void setIs_lost(int islost) {
            this.is_lost = islost;
        }

        public String getParcelsUid() {
            return parcelsUid;
        }

        public String getLostDateTime() {
            return lostDateTime;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public void setLostDateTime(String lostDateTime) {
            if (lostDateTime != null) {
                this.lostDateTime = lostDateTime.replace('-', '/').substring(5, 10) + " 確認済み";
            }else{
                this.lostDateTime = "未チェック";
            }
            show_lost_datetime = this.lostDateTime;
        }

        public void setShowLostDatetime(String s){
            show_lost_datetime = s;
        }
        public String getShowlostdatetime(){
            return show_lost_datetime;
        }

        public Boolean existIsChecked() {
            return exsitCheckdata;
        }
        public Boolean lostIsChecked() {
            return lostCheckdata;
        }
    }

    // リスト表示制御用クラス
    class ListAdapter extends ArrayAdapter<Data> {
        private final LayoutInflater inflater;
        // values/colors.xmlより設定値を取得するために利用。
        private final Resources r;
        private String building = "";

        public ListAdapter(Context context, List<Data> objects, String building) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            r = context.getResources();
            this.building = building;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            // layout/raw.xmlを紐付ける
            if (view == null) {
                view = inflater.inflate(R.layout.outdated_twocheckboxes_nimotsufuda_raw, parent, false);
            }
            final Data data = this.getItem(position);
            TextView tvData1 = view.findViewById(R.id.raw1);
            tvData1.setText(data.getRoomName());
            TextView tvData2 = view.findViewById(R.id.raw2);
            tvData2.setText(data.getRyoseiName());
            TextView tvData3 = view.findViewById(R.id.raw3);
            tvData3.setText(data.getParcelsAttribute());
            TextView tvData4 = view.findViewById(R.id.raw4);
            tvData4.setText(data.getShowlostdatetime());
            CheckBox existCheckBox = view.findViewById(R.id.parcel_exist_checkbox);
            existCheckBox.setOnCheckedChangeListener(null);
            existCheckBox.setChecked(data.existIsChecked());
            CheckBox lostCheckBox = view.findViewById(R.id.parcel_lost_checkbox);
            lostCheckBox.setOnCheckedChangeListener(null);
            lostCheckBox.setChecked(data.lostIsChecked());
            existCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        tvData4.setText(today+"現物あり");
                        data.setShowLostDatetime(today+"現物あり");
                        data.setExistChecked(true);
                        data.setLostChecked(false);
                        lostCheckBox.setChecked(false);
                    }else{
                        if(data.lostCheckdata == false) {
                            data.setShowLostDatetime(data.lostDateTime);
                            tvData4.setText(data.lostDateTime);
                        }
                        data.setExistChecked(false);
                    }
                }
            });
            lostCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        tvData4.setText(today+"紛失");
                        data.setShowLostDatetime(today+"紛失");
                        data.setExistChecked(false);
                        data.setLostChecked(true);
                        existCheckBox.setChecked(false);
                    }else{
                        if(data.exsitCheckdata == false) {
                            data.setShowLostDatetime(data.lostDateTime);
                            tvData4.setText(data.lostDateTime);
                        };
                        data.setLostChecked(false);
                    }
                }
            });
            TextView is_lost = view.findViewById(R.id.lost_status_text);


            if (data != null) {
                //1列目は部屋番号
                tvData1.setText(data.getRoomName());
                //2列目は名前
                tvData2.setText(data.getRyoseiName());
                //3列目は荷物札の種類
                tvData3.setText(data.getParcelsAttribute());
            }
            switch (building) {
                case "A": {
                    //偶数行の場合の背景色を設定
                    if (position % 2 == 0) {
                        tvData1.setBackgroundColor(r.getColor(R.color.data1A));
                        tvData2.setBackgroundColor(r.getColor(R.color.data1A));
                        tvData3.setBackgroundColor(r.getColor(R.color.data1A));
                        tvData4.setBackgroundColor(r.getColor(R.color.data1A));
                        //checkBoxA.setBackgroundColor(r.getColor(R.color.data1A));
                    }
                    //奇数行の場合の背景色を設定
                    else {
                        tvData1.setBackgroundColor(r.getColor(R.color.data2A));
                        tvData2.setBackgroundColor(r.getColor(R.color.data2A));
                        tvData3.setBackgroundColor(r.getColor(R.color.data2A));
                        tvData4.setBackgroundColor(r.getColor(R.color.data2A));
                        // checkBoxA.setBackgroundColor(r.getColor(R.color.data2A));
                    }
                    break;
                }
                case "B": {
                    //偶数行の場合の背景色を設定
                    if (position % 2 == 0) {
                        tvData1.setBackgroundColor(r.getColor(R.color.data1B));
                        tvData2.setBackgroundColor(r.getColor(R.color.data1B));
                        tvData3.setBackgroundColor(r.getColor(R.color.data1B));
                        tvData4.setBackgroundColor(r.getColor(R.color.data1B));
                        //checkBoxB.setBackgroundColor(r.getColor(R.color.data1B));
                    }
                    //奇数行の場合の背景色を設定
                    else {
                        tvData1.setBackgroundColor(r.getColor(R.color.data2B));
                        tvData2.setBackgroundColor(r.getColor(R.color.data2B));
                        tvData3.setBackgroundColor(r.getColor(R.color.data2B));
                        tvData4.setBackgroundColor(r.getColor(R.color.data2B));
                    }
                    break;
                }
                case "C": {
                    //偶数行の場合の背景色を設定
                    if (position % 2 == 0) {
                        tvData1.setBackgroundColor(r.getColor(R.color.data1C));
                        tvData2.setBackgroundColor(r.getColor(R.color.data1C));
                        tvData3.setBackgroundColor(r.getColor(R.color.data1C));
                        tvData4.setBackgroundColor(r.getColor(R.color.data1C));
                        //checkCoxC.setCackgroundColor(r.getColor(R.color.data1C));
                    }
                    //奇数行の場合の背景色を設定
                    else {
                        tvData1.setBackgroundColor(r.getColor(R.color.data2C));
                        tvData2.setBackgroundColor(r.getColor(R.color.data2C));
                        tvData3.setBackgroundColor(r.getColor(R.color.data2C));
                        tvData4.setBackgroundColor(r.getColor(R.color.data2C));
                    }
                    break;

                }
                case "D": {
                    //偶数行の場合の背景色を設定
                    if (position % 2 == 0) {
                        tvData1.setBackgroundColor(r.getColor(R.color.data1D));
                        tvData2.setBackgroundColor(r.getColor(R.color.data1D));
                        tvData3.setBackgroundColor(r.getColor(R.color.data1D));
                        tvData4.setBackgroundColor(r.getColor(R.color.data1D));
                        //checkBoxD.setBackgroundColor(r.getColor(R.color.data1D));
                    }
                    //奇数行の場合の背景色を設定
                    else {
                        tvData1.setBackgroundColor(r.getColor(R.color.data2D));
                        tvData2.setBackgroundColor(r.getColor(R.color.data2D));
                        tvData3.setBackgroundColor(r.getColor(R.color.data2D));
                        tvData4.setBackgroundColor(r.getColor(R.color.data2D));
                        // checkBoxD.setBackgroundColor(r.getColor(R.color.data2D));
                    }
                    break;
                }
                case "E": {
                    //偶数行の場合の背景色を設定
                    if (position % 2 == 0) {
                        tvData1.setBackgroundColor(r.getColor(R.color.data1E));
                        tvData2.setBackgroundColor(r.getColor(R.color.data1E));
                        tvData3.setBackgroundColor(r.getColor(R.color.data1E));
                        tvData4.setBackgroundColor(r.getColor(R.color.data1E));
                        //checkBoxE.setBackgroundColor(r.getColor(R.color.data1E));
                    }
                    //奇数行の場合の背景色を設定
                    else {
                        tvData1.setBackgroundColor(r.getColor(R.color.data2E));
                        tvData2.setBackgroundColor(r.getColor(R.color.data2E));
                        tvData3.setBackgroundColor(r.getColor(R.color.data2E));
                        tvData4.setBackgroundColor(r.getColor(R.color.data2E));
                        // checkBoxE.setBackgroundColor(r.getColor(R.color.data2E));
                    }
                    break;
                }
                default: {
                    break;
                }


            }


            return view;
        }
        public void showLostDialog(String room_name,String owner_name,String parcels_id,Boolean lost_status) {
            DialogFragment dialogFragment = new Night_Duty_Lost_Dialog();
            Bundle args = new Bundle();
            args.putString("room_name", room_name);
            args.putString("owner_name", owner_name);
            args.putString("parcels_id", parcels_id);
            args.putBoolean("lost_status", lost_status);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Duty_Night_Dialog");
        }



    }


}