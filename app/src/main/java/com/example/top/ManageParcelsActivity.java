package com.example.top;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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

public class ManageParcelsActivity extends AppCompatActivity{
    //表示用のデータを格納するリスト
    public List< ManageParcelsActivity.Data> dataListA = new ArrayList<>();
    public List< ManageParcelsActivity.Data> dataListB = new ArrayList<>();
    public List< ManageParcelsActivity.Data> dataListC = new ArrayList<>();
    public List< ManageParcelsActivity.Data> dataListD = new ArrayList<>();

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

    public ListAdapter adapterA;
    public ListAdapter adapterB;
    public ListAdapter adapterC;
    public ListAdapter adapterD;

    private TouchSound touchsound;
    private DatabaseHelper _helper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_parcels);
        touchsound = new TouchSound(this);
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

        _helper = new DatabaseHelper(ManageParcelsActivity.this);
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
            ManageParcelsActivity.Data data = new ManageParcelsActivity.Data();
            data.parcelsAttribute = placement;
            data.roomName = roomName;
            data.ryoseiName = ryoseiName;
            data.parcelsUid = parcelsUid;

            if(is_lost == 0) {
                data.previousIsLost = false;
                data.setLostDateTime(lostdatetime, false);//lost_datetimeに最終確認できた時間を入れている　カラム名の変更が手間だったため
            }else{
                data.previousIsLost = true;
                data.setLostDateTime(lostdatetime, true);//lost_datetimeに最終確認できた時間を入れている　カラム名の変更が手間だったため
            }

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
        adapterA = new ListAdapter(this, dataListA, "A");
        adapterB = new ListAdapter(this, dataListB, "B");
        adapterC = new ListAdapter(this, dataListC, "C");
        adapterD = new ListAdapter(this, dataListD, "D");
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

        finish();
    }

    public class UpdateParcelTableListener  extends OnOneClickListener {
        @Override
        public void onOneClick(View view){
            touchsound.playsoundone();
            List< ManageParcelsActivity.Data> joinedDataList = dataListA;
            joinedDataList.addAll(dataListB);
            joinedDataList.addAll(dataListC);
            joinedDataList.addAll(dataListD);
            this.showButtomDialog(view);
        }

        public void showButtomDialog(View view) {
            DialogFragment dialogFragment = new Night_Duty_Dialog();
            Bundle args = new Bundle();
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Night_Duty_Dialog");
        }
    }


    // データ格納用クラス
    class Data {
        private String roomName;
        private String ryoseiName;
        private String parcelsAttribute;
        private String parcelsUid;
        private String lostDateTime;
        private Boolean previousIsLost;
        private Boolean existCheckdata = false;
        private Boolean lostCheckdata = false;
        private String note;
        private String show_lost_datetime;
        //外部に出力する値。listviewの値を保持するために一時的に保持する。

        public void setLostDateTime(String lostDateTime,boolean lost_status) {
            if (lostDateTime != null) {
                if(lost_status) {
                    this.lostDateTime = lostDateTime.replace('-', '/').substring(5, 10) + "紛失";
                }else{

                    this.lostDateTime = lostDateTime.replace('-', '/').substring(5, 10) + " 確認済み";
                }
            }else{
                this.lostDateTime = "未チェック";
            }
            show_lost_datetime = this.lostDateTime;
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
                view = inflater.inflate(R.layout.outdated_manage_parcels_raw, parent, false);
            }
            final Data data = this.getItem(position);
            TextView tvData1 = view.findViewById(R.id.raw1);
            tvData1.setText(data.roomName);
            TextView tvData2 = view.findViewById(R.id.raw2);
            tvData2.setText(data.ryoseiName);
            TextView tvData3 = view.findViewById(R.id.raw3);
            tvData3.setText(data.parcelsAttribute);
            TextView tvData4 = view.findViewById(R.id.raw4);
            tvData4.setText(data.lostDateTime);
            Button removeButton  = view.findViewById(R.id.remove_button);
            removeButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
                });
            if (data != null) {
                //1列目は部屋番号
                tvData1.setText(data.roomName);
                //2列目は名前
                tvData2.setText(data.ryoseiName);
                //3列目は荷物札の種類
                tvData3.setText(data.parcelsAttribute);
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

    }


}