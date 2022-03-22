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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.top.ClickListener.OnOneClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NightDutyActivity extends AppCompatActivity {

    // データを準備
    public List<Data> dataListA = new ArrayList<>();
    public List<Data> dataListB = new ArrayList<>();
    public List<Data> dataListC = new ArrayList<>();
    public List<Data> dataListD = new ArrayList<>();
    public List<Data> dataListE = new ArrayList<>();
    public ListView listViewA;
    public TextView result;
    public String staff_room = "";
    public String staff_ryosei = "";
    public String staff_id = "";

    //説明文とボタン部分ここから
    public TextView title;
    public TextView explain;
    public TextView explain_sub;
    public Button button_phase1;
    public Button button_phase2;

    //説明文とボタン部分ここまで
    private TouchSound touchsound;
    private DatabaseHelper _helper;

    //新入寮生のLinearLayout
    public LinearLayout freshmenLinear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night_duty);
        freshmenLinear = findViewById(R.id.all_fresh_component);
        touchsound = new TouchSound(this);
        //事務当番の名前を受け取る
        Intent intent = getIntent();
        staff_room = intent.getStringExtra("Jimuto_name");
        staff_ryosei = intent.getStringExtra("Jimuto_id");
        staff_id = intent.getStringExtra("Jimuto_room");
        // 事務当番の名前を表示
        TextView jimuto_name = findViewById(R.id.main_jimuto_show);
        jimuto_name.setText(staff_id);
        ListView listViewA = findViewById(R.id.listA);
        ListView listViewB = findViewById(R.id.listB);
        ListView listViewC = findViewById(R.id.listC);
        ListView listViewD = findViewById(R.id.listD);
        ListView listViewE = findViewById(R.id.listE);
        ImageButton go_back_button = findViewById(R.id.tomari_go_back_button);
        go_back_button.setOnClickListener(this::onBackButtonClick);
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。

        title = findViewById(R.id.night_duty_title);
        title.setText("泊まり事務当ー①現物確認");
        explain = findViewById(R.id.tomari_explanation_1);
        explain_sub = findViewById(R.id.tomari_explanation_2);
        button_phase1 = findViewById(R.id.tomari_result_show_button_1);//現物確認ボタン
        button_phase2 = findViewById(R.id.tomari_result_show_button_2);//札確認ボタン
        //説明文とボタン部分を指定

        //ボタンにリスナーを設定、荷物チェック
        NimotsuCheckResultButtonListener nimotsulistener = new NimotsuCheckResultButtonListener(result, staff_room, staff_ryosei, staff_id);
        nimotsulistener.importData(dataListA, dataListB, dataListC, dataListD, dataListE);
        nimotsulistener.importList(listViewA, listViewB, listViewC, listViewD, listViewE);
        button_phase1.setOnClickListener(nimotsulistener);
        //ボタンにリスナーを設定、荷物札チェック
        FudaCheckResultButtonListener fudalistener = new FudaCheckResultButtonListener();
        button_phase2.setOnClickListener(fudalistener);

        // システムナビゲーションバーの色を変更

        refresh_all(1);
        ActivityHelper.enableTransparentFooter(this);
    }

    public void refresh_all(int kakunin_phase) {
        //確認フェーズは、1:現物確認、2:札確認とする
        //説明文とボタンのフェーズによる切り替え部分
        switch (kakunin_phase) {
            case 1:
                button_phase1.setVisibility(View.VISIBLE);
                button_phase2.setVisibility(View.GONE);
                explain_sub.setText(getString(R.string.night_duty_1_explanation));
                break;
            case 2:
                button_phase1.setVisibility(View.GONE);
                button_phase2.setVisibility(View.VISIBLE);
                title.setText("泊まり事務当ー②荷物札確認");
                explain_sub.setText(getString(R.string.night_duty_2_explanation));
                freshmenLinear.setVisibility(View.GONE);


        }
        //テーブルの荷物表示部分の更新
        dataListA.clear();
        dataListB.clear();
        dataListC.clear();
        dataListD.clear();
        dataListE.clear();
        // DBヘルパーオブジェクトを生成。
        _helper = new DatabaseHelper(NightDutyActivity.this);
        SQLiteDatabase db = _helper.getWritableDatabase();
        /*for (int i = 0; i < 4; i++) {
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
                    data.setLostDateTime(onesParcels.get(j).get("lost_datetime"));//lost_datetimeに最終確認できた時間を入れている　カラム名の変更が手間だったため
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
            cursor.close();
        }*/
        //新入寮生以外の荷物をやる。
        String sql = "select * from parcels where is_released = 0 AND is_deleted = 0 AND placement != 6 ORDER BY owner_room_name asc,owner_ryosei_name asc ;";

        // 検索結果を保存
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        String roomName;
        String ryoseiName;
        String ryoseiUid;
        int placement_id;
        String placement;
        String lostdatetime;
        String parcelsUid;
        String note;
        int is_lost;
        List<Map<String, String>> onesParcels = null;
        Cursor blockCursor;
        int block_id;


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
            Data data = new Data();
            data.setParcelsAttribute(placement);
            data.setRoomName(roomName);
            data.setRyoseiName(ryoseiName);
            data.setParcelUid(parcelsUid);
            data.setLostDateTime(lostdatetime);//lost_datetimeに最終確認できた時間を入れている　カラム名の変更が手間だったため
            data.setChecked(false);
            if(kakunin_phase==1) {// 0現物あり(荷物確認中)  1紛失中(荷物確認中) 10現物あり(札確認中)　11紛失中(札確認中)
                data.setIs_lost(is_lost);
            }else{
                data.setIs_lost(is_lost+10);
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


        ListView listViewA = findViewById(R.id.listA);
        ListView listViewB = findViewById(R.id.listB);
        ListView listViewC = findViewById(R.id.listC);
        ListView listViewD = findViewById(R.id.listD);
        // リストにサンプル用のデータを受け渡す
        ListAdapter adapterA = new ListAdapter(this, dataListA,"A");
        ListAdapter adapterB = new ListAdapter(this, dataListB,"B");
        ListAdapter adapterC = new ListAdapter(this, dataListC,"C");
        ListAdapter adapterD = new ListAdapter(this, dataListD,"D");
        listViewA.setAdapter(adapterA);
        listViewB.setAdapter(adapterB);
        listViewC.setAdapter(adapterC);
        listViewD.setAdapter(adapterD);


        //新入寮生の荷物に関しては別個で扱う。
        String sql_freshmen = "select * from parcels where is_released = 0 AND is_deleted = 0 AND placement = 6 ORDER BY note asc ;";
        Cursor cursor_freshmen = db.rawQuery(sql_freshmen, null);
        if (cursor_freshmen.moveToNext()) {//新入寮生の荷物が一件でもあった場合
            cursor_freshmen.moveToFirst();
            while (cursor_freshmen.moveToNext()) {
                ryoseiUid = cursor_freshmen.getString(cursor_freshmen.getColumnIndex("owner_uid"));
                String sql_get_block_from_owneruid = "SELECT block_id FROM ryosei WHERE uid ='" + ryoseiUid + "';";
                blockCursor = db.rawQuery(sql_get_block_from_owneruid, null);
                blockCursor.moveToFirst();
                block_id = blockCursor.getInt(blockCursor.getColumnIndex("block_id"));
                blockCursor.close();

                roomName = cursor_freshmen.getString(cursor_freshmen.getColumnIndex("owner_room_name"));
                ryoseiName = cursor_freshmen.getString(cursor_freshmen.getColumnIndex("owner_ryosei_name"));
                parcelsUid = cursor_freshmen.getString(cursor_freshmen.getColumnIndex("uid"));
                note = cursor_freshmen.getString(cursor_freshmen.getColumnIndex("note"));
                lostdatetime = cursor_freshmen.getString(cursor_freshmen.getColumnIndex("lost_datetime"));
                is_lost = cursor_freshmen.getInt(cursor_freshmen.getColumnIndex("is_lost"));
                onesParcels = _helper.nimotsuCountOfRyosei(db, ryoseiUid);
                Data data = new Data();
                data.setParcelsAttribute(note);//札が一意に定まるのでここでは番号とする。
                data.setRoomName(roomName);
                data.setRyoseiName(ryoseiName);
                data.setParcelUid(parcelsUid);
                data.setLostDateTime(lostdatetime);//lost_datetimeに最終確認できた時間を入れている　カラム名の変更が手間だったため
                data.setChecked(false);
                data.setIs_lost(is_lost);
                dataListE.add(data);
            }


            ListView listViewE = findViewById(R.id.listE);
            // リストにサンプル用のデータを受け渡す
            ListAdapter adapterE = new ListAdapter(this, dataListE,"E");
            listViewE.setAdapter(adapterE);
        } else {
            freshmenLinear.setVisibility(View.GONE);
        }

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
/*
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

 */

    class NimotsuCheckResultButtonListener extends OnOneClickListener {
        public List<Data> dataA = new ArrayList<>();
        public List<Data> dataB = new ArrayList<>();
        public List<Data> dataC = new ArrayList<>();
        public List<Data> dataD = new ArrayList<>();
        public List<Data> dataE = new ArrayList<>();
        public List<String> outputDataA = new ArrayList<>();
        public List<String> outputDataB = new ArrayList<>();
        public List<String> outputDataC = new ArrayList<>();
        public List<String> outputDataD = new ArrayList<>();
        public List<String> outputDataE = new ArrayList<>();
        public ListView listViewA;
        public ListView listViewB;
        public ListView listViewC;
        public ListView listViewD;
        public ListView listViewE;
        public TextView result;
        public String staff_room;
        public String staff_ryosei;
        public String staff_id;

        public NimotsuCheckResultButtonListener(TextView resultA, String staff_room, String staff_ryosei, String staff_id) {
            super();
            this.result = result;
            this.staff_room = staff_room;
            this.staff_ryosei = staff_ryosei;
            this.staff_id = staff_id;
        }

        @Override
        public void onOneClick(View view) {
            //
            Boolean all_checked = false;
            ArrayList<String> outputDataAll = new ArrayList<>();
            touchsound.playsoundone();


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
                        all_checked = !checkListA[i];
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
                        all_checked = !checkListB[i];
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
                        all_checked = !checkListC[i];
                    }
                }

            }
            if (dataD.size() > 0) {
                boolean[] checkListD = new boolean[dataD.size()];
                for (int i = 0; i < dataD.size(); i++) {
                    View listDataView = listViewD.getAdapter().getView(i, null, listViewD);
                    CheckBox chkDel = listDataView.findViewById(R.id.parcelcheckbox);
                    if (chkDel.isChecked()) {
                        checkListD[i] = true;
                        outputDataD.add(dataD.get(i).getParcelsUid());
                    } else {
                        checkListD[i] = false;
                    }
                    if (i == dataD.size() - 1) {
                        all_checked = !checkListD[i];
                    }
                }
            }
            if (dataE.size() > 0) {
                boolean[] checkListE = new boolean[dataE.size()];
                for (int i = 0; i < dataE.size(); i++) {
                    View listDataView = listViewE.getAdapter().getView(i, null, listViewE);
                    CheckBox chkDel = listDataView.findViewById(R.id.parcelcheckbox);
                    if (chkDel.isChecked()) {
                        checkListE[i] = true;
                        outputDataE.add(dataE.get(i).getParcelsUid());
                    } else {
                        checkListE[i] = false;
                    }
                    if (i == dataE.size() - 1) {
                        all_checked = !checkListE[i];
                    }
                }
            }
            outputDataAll.addAll(outputDataA);
            outputDataAll.addAll(outputDataB);
            outputDataAll.addAll(outputDataC);
            outputDataAll.addAll(outputDataD);
            outputDataAll.addAll(outputDataE);
            //ここまででbool

            if (all_checked) {
                this.showButtomDialog(view, outputDataAll);
            } else {
                SQLiteDatabase db = _helper.getWritableDatabase();
                for (int i = 0; i < outputDataAll.size(); i++) {
                    _helper.night_check_updater(db, outputDataAll.get(i));
                }
                db.close();
                refresh_all(2);
                Toast.makeText(NightDutyActivity.this, R.string.night_duty_short, Toast.LENGTH_SHORT).show();

            }
        }

        public void importData(List<Data> dataA, List<Data> dataB, List<Data> dataC, List<Data> dataD, List<Data> dataE) {
            this.dataA = dataA;
            this.dataB = dataB;
            this.dataC = dataC;
            this.dataD = dataD;
            this.dataE = dataE;
        }

        public void importList(ListView listA, ListView listB, ListView listC, ListView listD, ListView listE) {
            this.listViewA = listA;
            this.listViewB = listB;
            this.listViewC = listC;
            this.listViewD = listD;
            this.listViewE = listE;
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

    class FudaCheckResultButtonListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            ArrayList<String> outputDataAll = new ArrayList<>();
            Intent event_refresh_intent = new Intent();
            event_refresh_intent.putExtra("EventRefresh", true);
            setResult(RESULT_OK, event_refresh_intent);
            Toast.makeText(NightDutyActivity.this, "荷物札の確認を行いました。泊まり事務当番が完了しました。", Toast.LENGTH_LONG).show();
            touchsound.playsoundtransition();
            finish();
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
        private String note;
        private int is_lost;

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
            this.lostDateTime = lostDateTime;
            if (lostDateTime != null)
                this.lostDateTime = lostDateTime.replace('-', '/').substring(5, 10) + " 確認済み";
        }

        public Boolean isChecked() {
            return checkdata;
        }
    }

    // リスト表示制御用クラス
    class ListAdapter extends ArrayAdapter<Data> {
        private final LayoutInflater inflater;
        // values/colors.xmlより設定値を取得するために利用。
        private final Resources r;
        private String building ="";
        public ListAdapter(Context context, List<Data> objects,String building) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            r = context.getResources();
            this.building = building;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            Data item = getItem(position);
            // layout/raw.xmlを紐付ける
            if (view == null) {
                view = inflater.inflate(R.layout.outdated_nimotsufuda_raw, parent, false);
            }
            final Data data = this.getItem(position);
            TextView tvData1 = view.findViewById(R.id.raw1);
            tvData1.setText(item.getRoomName());
            TextView tvData2 = view.findViewById(R.id.raw2);
            tvData2.setText(item.getRyoseiName());
            TextView tvData3 = view.findViewById(R.id.raw3);
            tvData3.setText(item.getParcelsAttribute());
            TextView tvData4 = view.findViewById(R.id.raw4);
            tvData4.setText(item.getLostDateTime());
            CheckBox checkBox = view.findViewById(R.id.parcelcheckbox);
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(item.isChecked());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Data MyData = getItem(position);
                    MyData.setChecked(isChecked);
                }
            });

            ImageButton is_foundButton = view.findViewById(R.id.is_found);
            ImageButton is_lostButton = view.findViewById(R.id.is_lost);
            is_foundButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    is_foundButton.setVisibility(View.GONE);
                    is_lostButton.setVisibility(View.VISIBLE);
                    Data MyData =getItem(position);
                    SQLiteDatabase db = _helper.getWritableDatabase();
                    _helper.is_lost_updater(db,MyData.getParcelsUid(),1);

                }
            });
            is_lostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    is_foundButton.setVisibility(View.VISIBLE);
                    is_lostButton.setVisibility(View.GONE);
                    SQLiteDatabase db = _helper.getWritableDatabase();
                    Data MyData =getItem(position);
                    _helper.is_lost_updater(db,MyData.getParcelsUid(),0);
                }
            });
            // 0現物あり(荷物確認中)  1紛失中(荷物確認中) 10現物あり(札確認中)　11紛失中(札確認中)
            if (data.getIs_lost() == 0) {
                is_lostButton.setVisibility(View.GONE);
            } else if (data.getIs_lost() == 1){
                is_foundButton.setVisibility(View.GONE);
            }else if (data.getIs_lost() == 10){//ボタンを無効化し、紛失状況を表示するだけ
                is_lostButton.setVisibility(View.GONE);
                is_lostButton.setEnabled(false);
                is_foundButton.setEnabled(false);
            }else if (data.getIs_lost() == 11){//ボタンを無効化し、紛失状況を表示するだけ
                is_foundButton.setVisibility(View.GONE);
                is_lostButton.setEnabled(false);
                is_foundButton.setEnabled(false);
            }


            if (data != null) {
                //1列目は部屋番号
                tvData1.setText(data.getRoomName());
                //2列目は名前
                tvData2.setText(data.getRyoseiName());
                //3列目は荷物札の種類
                tvData3.setText(data.getParcelsAttribute());
                if (data.getLostDateTime() == null) {
                    tvData4.setText("未チェック");
                } else {
                    tvData4.setText(data.getLostDateTime());
                }
            }
            switch (building){
                case "A":{
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
                case "B":{
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
                case "C":{
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
                case "D":{
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
                case "E":{
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
                default:{break;}



            }


            return view;
        }
    }


    // 新入寮生リスト表示制御用クラス
    class ListAdapterE extends ArrayAdapter<Data> {
        private final LayoutInflater inflater;
        // values/colors.xmlより設定値を取得するために利用。
        private final Resources r;

        public ListAdapterE(Context context, List<Data> objects) {
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
                if (data.getLostDateTime() == null) {
                    tvData4A.setText("未チェック");
                } else {
                    tvData4A.setText(data.getLostDateTime());
                }
            }
            //偶数行の場合の背景色を設定
            if (position % 2 == 0) {
                tvData1A.setBackgroundColor(r.getColor(R.color.data1E));
                tvData2A.setBackgroundColor(r.getColor(R.color.data1E));
                tvData3A.setBackgroundColor(r.getColor(R.color.data1E));
                tvData4A.setBackgroundColor(r.getColor(R.color.data1E));
                //checkBoxA.setBackgroundColor(r.getColor(R.color.data1A));
            }
            //奇数行の場合の背景色を設定
            else {
                tvData1A.setBackgroundColor(r.getColor(R.color.data2E));
                tvData2A.setBackgroundColor(r.getColor(R.color.data2E));
                tvData3A.setBackgroundColor(r.getColor(R.color.data2E));
                tvData4A.setBackgroundColor(r.getColor(R.color.data2E));
                // checkBoxA.setBackgroundColor(r.getColor(R.color.data2A));
            }
            return view;
        }
    }

}
