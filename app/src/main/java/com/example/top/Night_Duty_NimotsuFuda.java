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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Night_Duty_NimotsuFuda extends AppCompatActivity {

    // データを準備
    public List<Data> dataListA = new ArrayList<>();
    public List<Data> dataListB = new ArrayList<>();
    public List<Data> dataListC = new ArrayList<>();
    public List<Data> dataListD = new ArrayList<>();
    public ArrayList<String> outputDataAll = new ArrayList<>();
    public ListView listViewA;
    public TextView result;
    public String staff_room = "";
    public String staff_ryosei = "";
    public String staff_id = "";

    private DatabaseHelper _helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomari);
        //事務当番の名前を受け取る
        Intent intent = getIntent();
        staff_room = intent.getStringExtra("Jimuto_name");
        staff_ryosei = intent.getStringExtra("Jimuto_id");
        staff_id = intent.getStringExtra("Jimuto_room");
        // 事務当番の名前を表示
        TextView jimuto_name = findViewById(R.id.main_jimutou_show);
        jimuto_name.setText(staff_id);
        ListView listViewA = findViewById(R.id.listA);
        ListView listViewB = findViewById(R.id.listB);
        ListView listViewC = findViewById(R.id.listC);
        ListView listViewD = findViewById(R.id.listD);
        ImageButton go_back_button = findViewById(R.id.tomari_go_back_button);
        go_back_button.setOnClickListener(this::onBackButtonClick);
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。

        Button resultButton = findViewById(R.id.tomari_result_show_button);
        CheckResultButtonListener listener = new CheckResultButtonListener(result, staff_room, staff_ryosei, staff_id);
        listener.importData(dataListA, dataListB, dataListC, dataListD);
        listener.importList(listViewA, listViewB, listViewC, listViewD);
        resultButton.setOnClickListener(listener);
        refresh_maintable();
        // システムナビゲーションバーの色を変更
        ActivityHelper.enableTransparentFooter(this);
    }

    public void refresh_maintable() {
        // DBヘルパーオブジェクトを生成。
        _helper = new DatabaseHelper(Night_Duty_NimotsuFuda.this);
        SQLiteDatabase db = _helper.getWritableDatabase();
        for (int i = 0; i < 4; i++) {
            String sql = null;
            switch (i) {
                case 0:
                    sql = "SELECT uid, block_id,room_name, ryosei_name, parcels_current_count FROM ryosei WHERE parcels_current_count > 0 AND block_id > 0 AND block_id <= 4 order by room_name asc,ryosei_name asc";
                    break;
                case 1:
                    sql = "SELECT uid, block_id,room_name, ryosei_name, parcels_current_count FROM ryosei WHERE parcels_current_count > 0 AND block_id > 4 AND block_id <= 7 order by room_name asc,ryosei_name asc";
                    break;
                case 2:
                    sql = "SELECT uid, block_id,room_name, ryosei_name, parcels_current_count FROM ryosei WHERE parcels_current_count > 0 AND block_id > 7 AND block_id <= 9 order by room_name asc,ryosei_name asc";
                    break;
                case 3:
                    sql = "SELECT uid, block_id,room_name, ryosei_name, parcels_current_count FROM ryosei WHERE parcels_current_count > 0 AND block_id > 9 AND block_id <= 10 order by room_name asc,ryosei_name asc";
                    break;
            }
            // 検索結果を保存
            // SQLの実行。
            Cursor cursor = db.rawQuery(sql, null);
            String roomName;
            String ryoseiName;
            String ryoseiUid;
            List<Map<String, String>> onesParcels = null;


            // サンプル用のデータを詰め込む
            while (cursor.moveToNext()) {
                int roomNameIndex = cursor.getColumnIndex("room_name");
                roomName = cursor.getString(roomNameIndex);
                int ryoseiNameIndex = cursor.getColumnIndex("ryosei_name");
                ryoseiName = cursor.getString(ryoseiNameIndex);
                int ryoseiUidIndex = cursor.getColumnIndex("uid");
                ryoseiUid = cursor.getString(ryoseiUidIndex);
                onesParcels = _helper.nimotsuCountOfRyosei(db, ryoseiUid);
                for (int j = 0; j < onesParcels.size(); j++) {
                    Data data = new Data();
                    data.setParcelsAttribute(onesParcels.get(j).get("attribute"));
                    data.setRoomName(roomName);
                    data.setRyoseiName(ryoseiName);
                    data.setParcelUid(onesParcels.get(j).get("parcels_id"));
                    data.setLostDateTime(onesParcels.get(j).get("lost_datetime"));
                    data.setChecked(false);
                    switch (i) {
                        case 0:
                            dataListA.add(data);

                            break;
                        case 1:
                            dataListB.add(data);
                            break;
                        case 2:
                            dataListC.add(data);
                            break;
                        case 3:
                            dataListD.add(data);
                            break;
                    }
                }
            }
        }
        ListView listViewA = findViewById(R.id.listA);
        ListView listViewB = findViewById(R.id.listB);
        ListView listViewC = findViewById(R.id.listC);
        ListView listViewD = findViewById(R.id.listD);
        // リストにサンプル用のデータを受け渡す
        ListAdapterA adapterA = new ListAdapterA(this, dataListA);
        ListAdapterB adapterB = new ListAdapterB(this, dataListB);
        ListAdapterC adapterC = new ListAdapterC(this, dataListC);
        ListAdapterD adapterD = new ListAdapterD(this, dataListD);
        listViewA.setAdapter(adapterA);
        listViewB.setAdapter(adapterB);
        listViewC.setAdapter(adapterC);
        listViewD.setAdapter(adapterD);
        db.close();

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

    public void onReturnValue(boolean bool) {
        if (bool) {
            SQLiteDatabase db = _helper.getWritableDatabase();
            for (int i = 0; i < outputDataAll.size(); i++) {
                _helper.night_check_updater(db, outputDataAll.get(i));
            }
            db.close();
            finish();
            Toast.makeText(Night_Duty_NimotsuFuda.this, R.string.night_duty_short, Toast.LENGTH_SHORT).show();

        }
    }

    class CheckResultButtonListener implements View.OnClickListener {
        public List<Data> dataA = new ArrayList<>();
        public List<Data> dataB = new ArrayList<>();
        public List<Data> dataC = new ArrayList<>();
        public List<Data> dataD = new ArrayList<>();
        public List<String> outputDataA = new ArrayList<>();
        public List<String> outputDataB = new ArrayList<>();
        public List<String> outputDataC = new ArrayList<>();
        public List<String> outputDataD = new ArrayList<>();
        public ListView listViewA;
        public ListView listViewB;
        public ListView listViewC;
        public ListView listViewD;
        public TextView result;
        public String staff_room;
        public String staff_ryosei;
        public String staff_id;

        public CheckResultButtonListener(TextView resultA, String staff_room, String staff_ryosei, String staff_id) {
            super();
            this.result = result;
            this.staff_room = staff_room;
            this.staff_ryosei = staff_ryosei;
            this.staff_id = staff_id;
        }

        @Override
        public void onClick(View view) {
            Boolean buttomCheck = false;
            if (dataA.size() > 0) {
                boolean[] checkListA = new boolean[dataA.size()];
                for (int i = 0; i < dataA.size(); i++) {
                    View listDataView = listViewA.getAdapter().getView(i, null, listViewA);
                    CheckBox chkDel = listDataView.findViewById(R.id.parcelcheckbox);
                    if (chkDel.isChecked()) {
                        checkListA[i] = true;
                        outputDataA.add(dataA.get(i).getParcelsUid());
                    } else {
                        checkListA[i] = false;
                    }
                    if (i == dataA.size() - 1) {
                        buttomCheck = !checkListA[i];
                    }
                }
            }
            if (dataB.size() > 0) {
                boolean[] checkListB = new boolean[dataB.size()];
                for (int i = 0; i < dataB.size(); i++) {
                    View listDataView = listViewB.getAdapter().getView(i, null, listViewB);
                    CheckBox chkDel = listDataView.findViewById(R.id.parcelcheckbox);
                    if (chkDel.isChecked()) {
                        checkListB[i] = true;
                        outputDataB.add(dataB.get(i).getParcelsUid());
                    } else {
                        checkListB[i] = false;
                    }
                    if (i == dataB.size() - 1) {
                        buttomCheck = !checkListB[i];
                    }
                }
            }
            if (dataC.size() > 0) {
                boolean[] checkListC = new boolean[dataC.size()];
                for (int i = 0; i < dataC.size(); i++) {
                    View listDataView = listViewC.getAdapter().getView(i, null, listViewC);
                    CheckBox chkDel = listDataView.findViewById(R.id.parcelcheckbox);
                    if (chkDel.isChecked()) {
                        checkListC[i] = true;
                        outputDataC.add(dataC.get(i).getParcelsUid());
                    } else {
                        checkListC[i] = false;
                    }

                    if (i == dataC.size() - 1) {
                        buttomCheck = !checkListC[i];
                    }
                }

            }
            if (dataD.size() > 0) {
                boolean[] checkListD = new boolean[dataD.size()];
                for (int i = 0; i < dataD.size(); i++) {
                    View listDataView = listViewD.getAdapter().getView(i, null, listViewD);
                    CheckBox chkDel = listDataView.findViewById(R.id.parcelcheckbox_rinjicapacity);
                    if (chkDel.isChecked()) {
                        checkListD[i] = true;
                        outputDataD.add(dataD.get(i).getParcelsUid());
                    } else {
                        checkListD[i] = false;
                    }
                    if (i == dataD.size() - 1) {
                        buttomCheck = !checkListD[i];
                    }
                }
            }
            outputDataAll.addAll(outputDataA);
            outputDataAll.addAll(outputDataB);
            outputDataAll.addAll(outputDataC);
            outputDataAll.addAll(outputDataD);
            if (buttomCheck) {
                this.showButtomDialog(view, outputDataAll);
            } else {
                SQLiteDatabase db = _helper.getWritableDatabase();
                for (int i = 0; i < outputDataAll.size(); i++) {
                    _helper.night_check_updater(db, outputDataAll.get(i));
                }
                db.close();
                Toast.makeText(Night_Duty_NimotsuFuda.this, R.string.night_duty_short, Toast.LENGTH_SHORT).show();
            }
        }

        public void importData(List<Data> dataA, List<Data> dataB, List<Data> dataC, List<Data> dataD) {
            this.dataA = dataA;
            this.dataB = dataB;
            this.dataC = dataC;
            this.dataD = dataD;
        }

        public void importList(ListView listA, ListView listB, ListView listC, ListView listD) {
            this.listViewA = listA;
            this.listViewB = listB;
            this.listViewC = listC;
            this.listViewD = listD;
        }

        public void showDialog(View view) {
            DialogFragment dialogFragment = new Duty_Night_Dialog();
            Bundle args = new Bundle();
            args.putString("register_staff_room", staff_room);
            args.putString("register_staff_name", staff_ryosei);
            args.putString("register_staff_id", staff_id);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "Duty_Night_Dialog");
        }

        public void showButtomDialog(View view, ArrayList<String> dataAll) {
            DialogFragment dialogFragment = new Duty_Night_Dialog();
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
        private Boolean checkdata;

        public void setParcelUid(String uid) {
            this.parcelsUid = uid;
        }

        public void setChecked(Boolean b) {
            checkdata = b;
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

        public void setRyoseiName(String ryosei) {
            this.ryoseiName = ryosei;
        }

        public String getParcelsAttribute() {
            return parcelsAttribute;
        }

        public void setParcelsAttribute(String parcels) {
            this.parcelsAttribute = parcels;
        }

        public String getParcelsUid() {
            return parcelsUid;
        }

        public String getLostDateTime() {
            return lostDateTime;
        }

        public void setLostDateTime(String lostDateTime) {
            this.lostDateTime = lostDateTime;
        }

        public Boolean isChecked() {
            return checkdata;
        }
    }

    // A棟リスト表示制御用クラス
    class ListAdapterA extends ArrayAdapter<Data> {
        private final LayoutInflater inflater;
        // values/colors.xmlより設定値を取得するために利用。
        private final Resources r;

        public ListAdapterA(Context context, List<Data> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            r = context.getResources();

        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            Data item = getItem(position);
            // layout/raw.xmlを紐付ける
            if (view == null) {
                view = inflater.inflate(R.layout.outdated_nimotsufuda_raw, parent, false);
            }
            final Data data = this.getItem(position);
            TextView tvData1A = view.findViewById(R.id.raw1);
            tvData1A.setText(item.getRoomName());
            TextView tvData2A = view.findViewById(R.id.raw2);
            tvData2A.setText(item.getRyoseiName());
            TextView tvData3A = view.findViewById(R.id.raw3);
            tvData3A.setText(item.getParcelsAttribute());
            TextView tvData4A = view.findViewById(R.id.raw4);
            tvData3A.setText(item.getLostDateTime());
            CheckBox checkBoxA = view.findViewById(R.id.parcelcheckbox);
            checkBoxA.setOnCheckedChangeListener(null);
            checkBoxA.setChecked(item.isChecked());
            checkBoxA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Data MyData = getItem(position);
                    MyData.setChecked(isChecked);
                }
            });
            if (data != null) {
                //1列目は部屋番号
                tvData1A.setText(data.getRoomName());
                //2列目は名前
                tvData2A.setText(data.getRyoseiName());
                //3列目は荷物札の種類
                tvData3A.setText(data.getParcelsAttribute());
                tvData4A.setText(data.getLostDateTime());
            }
            //偶数行の場合の背景色を設定
            if (position % 2 == 0) {
                tvData1A.setBackgroundColor(r.getColor(R.color.data1A));
                tvData2A.setBackgroundColor(r.getColor(R.color.data1A));
                tvData3A.setBackgroundColor(r.getColor(R.color.data1A));
                tvData4A.setBackgroundColor(r.getColor(R.color.data1A));
                //checkBoxA.setBackgroundColor(r.getColor(R.color.data1A));
            }
            //奇数行の場合の背景色を設定
            else {
                tvData1A.setBackgroundColor(r.getColor(R.color.data2A));
                tvData2A.setBackgroundColor(r.getColor(R.color.data2A));
                tvData3A.setBackgroundColor(r.getColor(R.color.data2A));
                tvData4A.setBackgroundColor(r.getColor(R.color.data2A));
                // checkBoxA.setBackgroundColor(r.getColor(R.color.data2A));
            }
            return view;
        }
    }

    // B棟リスト表示制御用クラス
    class ListAdapterB extends ArrayAdapter<Data> {
        private final LayoutInflater inflater;
        // values/colors.xmlより設定値を取得するために利用。
        private final Resources r;

        public ListAdapterB(Context context, List<Data> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            r = context.getResources();

        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            Data item = getItem(position);
            // layout/raw.xmlを紐付ける
            if (view == null) {
                view = inflater.inflate(R.layout.outdated_nimotsufuda_raw, parent, false);
            }
            final Data data = this.getItem(position);
            TextView tvData1A = view.findViewById(R.id.raw1);
            tvData1A.setText(item.getRoomName());
            TextView tvData2A = view.findViewById(R.id.raw2);
            tvData2A.setText(item.getRyoseiName());
            TextView tvData3A = view.findViewById(R.id.raw3);
            tvData3A.setText(item.getParcelsAttribute());
            TextView tvData4A = view.findViewById(R.id.raw4);
            tvData3A.setText(item.getLostDateTime());
            CheckBox checkBoxA = view.findViewById(R.id.parcelcheckbox);
            checkBoxA.setOnCheckedChangeListener(null);
            checkBoxA.setChecked(item.isChecked());
            checkBoxA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Data MyData = getItem(position);
                    MyData.setChecked(isChecked);
                }
            });
            if (data != null) {
                //1列目は部屋番号
                tvData1A.setText(data.getRoomName());
                //2列目は名前
                tvData2A.setText(data.getRyoseiName());
                //3列目は荷物札の種類
                tvData3A.setText(data.getParcelsAttribute());
                tvData4A.setText(data.getLostDateTime());
            }
            //偶数行の場合の背景色を設定
            if (position % 2 == 0) {
                tvData1A.setBackgroundColor(r.getColor(R.color.data1B));
                tvData2A.setBackgroundColor(r.getColor(R.color.data1B));
                tvData3A.setBackgroundColor(r.getColor(R.color.data1B));
                tvData4A.setBackgroundColor(r.getColor(R.color.data1B));
                //checkBoxA.setBackgroundColor(r.getColor(R.color.data1A));
            }
            //奇数行の場合の背景色を設定
            else {
                tvData1A.setBackgroundColor(r.getColor(R.color.data2B));
                tvData2A.setBackgroundColor(r.getColor(R.color.data2B));
                tvData3A.setBackgroundColor(r.getColor(R.color.data2B));
                tvData4A.setBackgroundColor(r.getColor(R.color.data2B));
                // checkBoxA.setBackgroundColor(r.getColor(R.color.data2A));
            }
            return view;
        }
    }

    // C棟リスト表示制御用クラス
    class ListAdapterC extends ArrayAdapter<Data> {
        private final LayoutInflater inflater;
        // values/colors.xmlより設定値を取得するために利用。
        private final Resources r;

        public ListAdapterC(Context context, List<Data> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            r = context.getResources();

        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            Data item = getItem(position);
            // layout/raw.xmlを紐付ける
            if (view == null) {
                view = inflater.inflate(R.layout.outdated_nimotsufuda_raw, parent, false);
            }
            final Data data = this.getItem(position);
            TextView tvData1A = view.findViewById(R.id.raw1);
            tvData1A.setText(item.getRoomName());
            TextView tvData2A = view.findViewById(R.id.raw2);
            tvData2A.setText(item.getRyoseiName());
            TextView tvData3A = view.findViewById(R.id.raw3);
            tvData3A.setText(item.getParcelsAttribute());
            TextView tvData4A = view.findViewById(R.id.raw4);
            tvData4A.setText(item.getLostDateTime());
            CheckBox checkBoxA = view.findViewById(R.id.parcelcheckbox);
            checkBoxA.setOnCheckedChangeListener(null);
            checkBoxA.setChecked(item.isChecked());
            checkBoxA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Data MyData = getItem(position);
                    MyData.setChecked(isChecked);
                }
            });
            if (data != null) {
                //1列目は部屋番号
                tvData1A.setText(data.getRoomName());
                //2列目は名前
                tvData2A.setText(data.getRyoseiName());
                //3列目は荷物札の種類
                tvData3A.setText(data.getParcelsAttribute());
                tvData4A.setText(data.getLostDateTime());
            }
            //偶数行の場合の背景色を設定
            if (position % 2 == 0) {
                tvData1A.setBackgroundColor(r.getColor(R.color.data1C));
                tvData2A.setBackgroundColor(r.getColor(R.color.data1C));
                tvData3A.setBackgroundColor(r.getColor(R.color.data1C));
                tvData4A.setBackgroundColor(r.getColor(R.color.data1C));
                //checkBoxA.setBackgroundColor(r.getColor(R.color.data1A));
            }
            //奇数行の場合の背景色を設定
            else {
                tvData1A.setBackgroundColor(r.getColor(R.color.data2C));
                tvData2A.setBackgroundColor(r.getColor(R.color.data2C));
                tvData3A.setBackgroundColor(r.getColor(R.color.data2C));
                tvData4A.setBackgroundColor(r.getColor(R.color.data2C));
                // checkBoxA.setBackgroundColor(r.getColor(R.color.data2A));
            }
            return view;
        }
    }

    // D棟リスト表示制御用クラス
    class ListAdapterD extends ArrayAdapter<Data> {
        private final LayoutInflater inflater;
        // values/colors.xmlより設定値を取得するために利用。
        private final Resources r;

        public ListAdapterD(Context context, List<Data> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            r = context.getResources();

        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            Data item = getItem(position);
            // layout/raw.xmlを紐付ける
            if (view == null) {
                view = inflater.inflate(R.layout.outdated_nimotsufuda_raw_rinjicapacity, parent, false);
            }
            final Data data = this.getItem(position);
            TextView tvData1A = view.findViewById(R.id.raw1_rinjicapacity);
            tvData1A.setText(item.getRoomName());
            TextView tvData2A = view.findViewById(R.id.raw2_rinjicapacity);
            tvData2A.setText(item.getRyoseiName());
            TextView tvData3A = view.findViewById(R.id.raw3_rinjicapacity);
            tvData3A.setText(item.getParcelsAttribute());
            TextView tvData4A = view.findViewById(R.id.raw4_rinjicapacity);
            tvData3A.setText(item.getLostDateTime());
            CheckBox checkBoxA = view.findViewById(R.id.parcelcheckbox_rinjicapacity);
            checkBoxA.setOnCheckedChangeListener(null);
            checkBoxA.setChecked(item.isChecked());
            checkBoxA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Data MyData = getItem(position);
                    MyData.setChecked(isChecked);
                }
            });
            if (data != null) {
                //1列目は部屋番号
                tvData1A.setText(data.getRoomName());
                //2列目は名前
                tvData2A.setText(data.getRyoseiName());
                //3列目は荷物札の種類
                tvData3A.setText(data.getParcelsAttribute());
                tvData4A.setText(data.getLostDateTime());
            }
            //偶数行の場合の背景色を設定
            if (position % 2 == 0) {
                tvData1A.setBackgroundColor(r.getColor(R.color.data1D));
                tvData2A.setBackgroundColor(r.getColor(R.color.data1D));
                tvData3A.setBackgroundColor(r.getColor(R.color.data1D));
                tvData4A.setBackgroundColor(r.getColor(R.color.data1D));
                //checkBoxA.setBackgroundColor(r.getColor(R.color.data1A));
            }
            //奇数行の場合の背景色を設定
            else {
                tvData1A.setBackgroundColor(r.getColor(R.color.data2D));
                tvData2A.setBackgroundColor(r.getColor(R.color.data2D));
                tvData3A.setBackgroundColor(r.getColor(R.color.data2D));
                tvData4A.setBackgroundColor(r.getColor(R.color.data2D));
                // checkBoxA.setBackgroundColor(r.getColor(R.color.data2A));
            }
            return view;
        }
    }


}