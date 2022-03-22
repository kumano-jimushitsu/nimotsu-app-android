package com.example.top;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.top.ClickListener.OnOneClickListener;
import com.example.top.ClickListener.OnOneItemClickListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int JIMUTOCHANGE_ACTIVITY = 1001;
    private static final int NIGHT_DUTY_ACTIVITY = 1002;
    private static final int EVENT_REFRESH_ACTIVITY = 1003;
    private static final int INVALID_POINTER_ID = -1;
    private static int mActivePointerId = INVALID_POINTER_ID;
    private static Context context;
    final String[] from = {"id", "text"};
    final int[] to = {android.R.id.text2, android.R.id.text1};
    final Handler handler = new Handler();
    String jimuto_room = "";
    //String jimuto_name = "";
    String jimuto_id = null;
    String qr_uuid = "";
    private TouchSound touchsound;
    private DatabaseHelper _helper;
    private SQLiteDatabase db;

    public static Context getMainActivityContext() {
        return MainActivity.context;
    }

    public void eventRefresher() {
        eventLogshow();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        _helper = new com.example.top.DatabaseHelper(this);
        db = _helper.getWritableDatabase();
        touchsound = new TouchSound(this);

        ImageButton image_button_touroku = findViewById(R.id.image_button_register);
        TourokuListener listener3 = new TourokuListener();
        image_button_touroku.setOnClickListener(listener3);

        ImageButton jimutou_change = findViewById(R.id.jimuto_change_button);
        JimutoChangeListener listener4 = new JimutoChangeListener();
        jimutou_change.setOnClickListener(listener4);

        ImageButton image_button_register = findViewById(R.id.image_button_release);
        RegisterListener listener5 = new RegisterListener();
        image_button_register.setOnClickListener(listener5);


        ListView eventLogshower = findViewById(R.id.event_show);
        EventShowListener showListener = new EventShowListener();
        eventLogshower.setOnItemClickListener(showListener);

        ImageButton duty_night = findViewById(R.id.duty_night_button);
        duty_night_listener listener6 = new duty_night_listener();
        duty_night.setOnClickListener(listener6);

        ImageButton qr_scanner = findViewById(R.id.qr_scanner);
        QRScanListener qr_Listener = new QRScanListener();
        qr_scanner.setOnClickListener(qr_Listener);

        ImageButton nimotsufuda = findViewById(R.id.event_history_refresh_button);
        RefreshListener listenerNimotsufuda = new RefreshListener();
        nimotsufuda.setOnClickListener(listenerNimotsufuda);

        Button oldnote = findViewById(R.id.old_note);
        OldNoteListener oldnotelistener = new OldNoteListener();
        oldnote.setOnClickListener(oldnotelistener);
        //oldnote.setVisibility(View.GONE);

        Button othersbutton = findViewById(R.id.others_button);
        OthersButtonListener othersbuttonlistener = new OthersButtonListener();
        othersbutton.setOnClickListener(othersbuttonlistener);

        //   ButteryChecker butterychecker = new ButteryChecker();
        // Listenerを設定
        //  butterychecker.setListener(createListener());
        //  butterychecker.execute(0);

        //同期処理部分
        new HttpTask(null, "parcels", "create").execute();
        new HttpTask(null, "ryosei", "create").execute();
        new HttpTask(null, "parcel_event", "create").execute();

        //同期処理部分ここまで
        eventLogshow();

        // システムナビゲーションバーの色を変更
        ActivityHelper.enableTransparentFooter(this);

        //事務当の名前を表示
        TextView jimuto_name = findViewById(R.id.main_jimuto_show);
        jimuto_room = _helper.jimuto_at_oncreate(db);
        jimuto_name.setText(jimuto_room);
        jimuto_id = _helper.jimuto_id_at_oncreate(db);

    }

    private BatteryChecker.Listener createListener() {
        return new BatteryChecker.Listener() {
            @Override
            public void onSuccess(int count) {
                TextView textView2 = findViewById(R.id.battery_timer);
                textView2.setText(String.valueOf(count));
                if (count > 5) {
                    touchsound.playsounderror();
                }
            }
        };


    }

    public void event_delete_failed_toast() {

        Toast.makeText(this, "5分経過しているため削除できませんでした。", Toast.LENGTH_SHORT).show();
    }

    public void onReturnValue() {
        eventLogshow();
    }

    public void eventLogshow() {
        List<Map<String, String>> show_eventlist = new ArrayList<>();
        String sql = "SELECT uid, created_at, event_type, parcel_uid, room_name, ryosei_name, target_event_uid,is_deleted FROM parcel_event where is_deleted = 0 order by created_at desc limit 100";
        Cursor cursor = db.rawQuery(sql, null);
        show_eventlist.clear();
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("is_deleted")).equals("1")) {//論理削除されている場合表示しない
                continue;
            }
            if (cursor.getInt(cursor.getColumnIndex("event_type")) == 3) {//削除をしめすレコードの場合表示しない
                continue;
            }
            Map<String, String> event_raw = new HashMap<>();
            String text = "";

            text += "" + cursor.getString(cursor.getColumnIndex("created_at")).substring(5).replace('-', '/');
            text += "   ";
            switch (cursor.getInt(cursor.getColumnIndex("event_type"))) {
                case 1://荷物登録
                    text += "受け取り       ";
                    break;
                case 2://荷物受取
                    text += "引き渡し       ";
                    break;
                case 3://イベント削除：表示しなくてもいいかもね→上の方で分岐
                    //text="イベントが削除されました";
                    break;
                case 4: // 発見
                    text += "荷物発見     ";
                    break;
                case 5: // 紛失
                    text += "荷物確認     ";
                    break;
                case 10:
                    text += "事務当交代     ";
                    break;
                case 11:
                    text += "泊まり事務当モード開始     ";
                    break;
                case 12:
                    text += "泊まり事務当モード終了     ";
                    break;
                case 20:
                    text += "本人確認完了     ";
                    break;
            }
            text += cursor.getString(cursor.getColumnIndex("room_name"));
            text += "    ";
            text += cursor.getString(cursor.getColumnIndex("ryosei_name"));

            String event_id = cursor.getString(cursor.getColumnIndex("uid"));
            event_raw.put("id", event_id);
            event_raw.put("text", text);
            show_eventlist.add(event_raw);
        }
        cursor.close();
        SimpleAdapter adapter = new SimpleAdapter(this, show_eventlist, android.R.layout.simple_list_item_1, from, to);
        ListView listView = findViewById(R.id.event_show);
        listView.setAdapter(adapter);
        ListView listListener = findViewById(R.id.event_show);
        listListener.setOnItemClickListener(new EventShowListener());
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //インテント終了後、メイン画面に戻ったときの処理を記載する部分
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case JIMUTOCHANGE_ACTIVITY:
                jimuto_id = intent.getStringExtra("Jimuto_id");
                jimuto_room = intent.getStringExtra("Jimuto_room_name");

                // jimuto_idがnull出ないときのみ事務当の表示変更
                if (jimuto_id != null) {
                    TextView jimuto_show = findViewById(R.id.main_jimuto_show);
                    jimuto_show.setText(jimuto_room);
                    _helper.jimuto_change_event(db, jimuto_id);
                }
                eventLogshow();
                break;
            case NIGHT_DUTY_ACTIVITY:
                _helper.night_duty_exit_event(db, jimuto_id);
                eventLogshow();
            case EVENT_REFRESH_ACTIVITY:
                boolean event_update = intent.getBooleanExtra("EventRefresh", false);
                eventLogshow();
                break;
            default:
                //ここにケースを追加！
        }


        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            qr_uuid = scanResult.getContents();
            // DBヘルパーオブジェクトを生成。
            _helper = new DatabaseHelper(MainActivity.this);
            SQLiteDatabase db = _helper.getWritableDatabase();
            String sql = " SELECT uid,is_released,fragile,owner_ryosei_name,owner_room_name,owner_uid FROM parcels WHERE uid ='" + qr_uuid + "';";
            Cursor cursor = db.rawQuery(sql, null);
            int is_released = 0;
            String uid = "";
            String owner_ryosei_name = "";
            String owner_room_name = "";
            int fragile = 0;
            if (cursor.getCount() == 0) {//QRコードがparcelsテーブルにない場合
                //uuidがryoseiテーブルにある場合は本人確認
                String identify_sql = "SELECT uid, room_name, ryosei_name,status FROM ryosei WHERE uid ='" + qr_uuid + "'";
                Cursor identify_cursor = db.rawQuery(identify_sql, null);
                if (identify_cursor.getCount() == 0) {
                    if (qr_uuid == null) {

                    } else if (qr_uuid.indexOf("発送") == -1) {
                        this.showMyDialog(null, getString(R.string.error), getString(R.string.qr_no_qr), getString(R.string.ok), "");
                    } else {
                        this.showMyDialog(null, "持ち主", qr_uuid, getString(R.string.ok), "");
                    }
                } else if (identify_cursor.getCount() == 1) {
                    identify_cursor.moveToFirst();
                    String identify_uid = identify_cursor.getString(identify_cursor.getColumnIndex("uid"));
                    String identify_name = identify_cursor.getString(identify_cursor.getColumnIndex("ryosei_name"));
                    String identify_room = identify_cursor.getString(identify_cursor.getColumnIndex("room_name"));
                    int identify_status = identify_cursor.getInt(identify_cursor.getColumnIndex("status"));
                    if (identify_status == 1) {
                        this.showMyDialog(null, "確認済み", "すでに本人確認が済んでおります。", getString(R.string.ok), "");
                    } else if (identify_status == 5) {
                        MainActivity.context = getApplicationContext();
                        DialogFragment dialogFragment = new IdentifyDialog();
                        Bundle args = new Bundle();
                        args.putString("id", identify_uid);
                        args.putString("room", identify_room);
                        args.putString("name", identify_name);
                        dialogFragment.setArguments(args);
                        dialogFragment.show(getSupportFragmentManager(), "identify_dialog");
                    } else {
                        this.showMyDialog(null, "えっ。。。", "どうしよこの場合、、、詳しい人呼んで", getString(R.string.ok), "");
                    }
                } else {
                    this.showMyDialog(null, getString(R.string.error), "寮生が重複登録されてる場合があります。開発者に連絡してください。", getString(R.string.ok), "");
                }
            } else if (cursor.getCount() == 1) {//QRコードがデータベースに一つある場合
                while (cursor.moveToNext()) {
                    uid = cursor.getString(cursor.getColumnIndex("owner_uid"));
                    owner_ryosei_name = cursor.getString(cursor.getColumnIndex("owner_ryosei_name"));
                    owner_room_name = cursor.getString(cursor.getColumnIndex("owner_room_name"));
                    is_released = cursor.getInt(cursor.getColumnIndex("is_released"));
                }

                if (is_released == 0) {//その荷物が未受け取りの時
                    this.showQRDialog(null, owner_room_name, owner_ryosei_name, uid);
                } else {//その荷物が受け取り済みの時
                    this.showMyDialog(null, getString(R.string.error), getString(R.string.qr_already), getString(R.string.ok), "");
                }
            } else {//QRコードがデータベースに二つ以上ある場合
                this.showMyDialog(null, getString(R.string.error), getString(R.string.qr_more_than_two), getString(R.string.ok), "");
            }
        }

        //同期処理部分
        new HttpTask(null, "parcels", "create").execute();
        new HttpTask(null, "ryosei", "create").execute();
        new HttpTask(null, "parcel_event", "create").execute();

        //同期処理部分ここまで
        eventLogshow();

    }

    public void showQRDialog(View view, String owner_room, String owner_name, String owner_id) {
        DialogFragment dialogFragment = new ReleaseQRDialog();
        Bundle args = new Bundle();
        args.putString("owner_room", owner_room);
        args.putString("owner_name", owner_name);
        args.putString("owner_id", owner_id);
        //args.putString("release_staff_room", jimuto_room);
        //args.putString("release_staff_name", jimuto_name);
        args.putString("release_staff_id", jimuto_id);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "Nimotsu_Register_Dialog");
    }

    public void showMyDialog(View view, String title, String mainText, String positiveButton, String neutralButton) {
        DialogFragment dialogFragment = new myDialog();


        Bundle args = new Bundle();
        args.putString("positivebutton", positiveButton);
        args.putString("neutralbutton", neutralButton);
        args.putString("title", title);
        args.putString("maintext", mainText);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "myDialog");
    }

    /*これをいじれば複数ボタンの同時押しおよび連続タップを防げそう。ただし拡張性はなさそう。
    class buttonClick extends OnOneClickListener {
        @Override
        public void onClick(View view) {
            //if (view.getId() == R.id.jimuto_change_button || view.getId() == R.id.image_button_touroku || view.getId() == R.id.image_button_register || view.getId() == R.id.event_show || view.getId() == R.id.ryosei_insert_button || view.getId() == R.id.parcel_insert_button || view.getId() == R.id.parcel_event_insert_button || view.getId() == R.id.duty_night_button) {
            if (view.getId() == R.id.jimuto_change_button || view.getId() == R.id.image_button_release || view.getId() == R.id.image_button_register || view.getId() == R.id.event_show || view.getId() == R.id.duty_night_button) {
                final Button button = findViewById(view.getId());
                button.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        button.setEnabled(true);
                    }
                }, 3000L);
            }

        }
    }*/

    private class QRScanListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            if (jimuto_id == null) {
                this.showMyDialog(null, getString(R.string.main_not_selected_staff), "", getString(R.string.ok), "");
                touchsound.playsounderror();
            } else {
                //カメラの呼び出し
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                touchsound.playsoundtransition();
                integrator.initiateScan();
            }
        }

        public void showMyDialog(View view, String title, String mainText, String positiveButton, String neutralButton) {
            DialogFragment dialogFragment = new myDialog();
            Bundle args = new Bundle();
            args.putString("positivebutton", positiveButton);
            args.putString("neutralbutton", neutralButton);
            args.putString("title", title);
            args.putString("maintext", mainText);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "myDialog");
        }

    }

    private class TourokuListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            //同期処理部分
            new HttpTask(null, "parcels", "create").execute();
            new HttpTask(null, "ryosei", "create").execute();
            new HttpTask(null, "parcel_event", "create").execute();

            //同期処理部分ここまで
            if (jimuto_id == null) {
                this.showMyDialog(null, getString(R.string.main_not_selected_staff), "", getString(R.string.ok), "");
                touchsound.playsounderror();
            } else {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.putExtra("Jimuto_id", jimuto_id);
                intent.putExtra("Jimuto_room", jimuto_room);
                //intent.putExtra("Jimuto_name", jimuto_name);
                startActivityForResult(intent, EVENT_REFRESH_ACTIVITY);
                touchsound.playsoundtransition();
            }
        }

        public void showMyDialog(View view, String title, String mainText, String positiveButton, String neutralButton) {
            DialogFragment dialogFragment = new myDialog();
            Bundle args = new Bundle();
            args.putString("positivebutton", positiveButton);
            args.putString("neutralbutton", neutralButton);
            args.putString("title", title);
            args.putString("maintext", mainText);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "myDialog");
        }
    }

    private class RegisterListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            //同期処理部分
            new HttpTask(null, "parcels", "create").execute();
            new HttpTask(null, "ryosei", "create").execute();
            new HttpTask(null, "parcel_event", "create").execute();

            //同期処理部分ここまで
            if (jimuto_id == null) {
                this.showMyDialog(null, getString(R.string.main_not_selected_staff), "", getString(R.string.ok), "");
                touchsound.playsounderror();
            } else {
                Intent intent = new Intent(MainActivity.this, ReleaseActivity.class);
                intent.putExtra("Jimuto_id", jimuto_id);
                intent.putExtra("Jimuto_room", jimuto_room);
                //intent.putExtra("Jimuto_name", jimuto_name);
                startActivityForResult(intent, EVENT_REFRESH_ACTIVITY);
                touchsound.playsoundtransition();
            }

        }

        public void showMyDialog(View view, String title, String mainText, String positiveButton, String neutralButton) {
            DialogFragment dialogFragment = new myDialog();
            Bundle args = new Bundle();
            args.putString("positivebutton", positiveButton);
            args.putString("neutralbutton", neutralButton);
            args.putString("title", title);
            args.putString("maintext", mainText);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "myDialog");
        }

    }

    private class JimutoChangeListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            //同期処理部分
            new HttpTask(null, "parcels", "create").execute();
            new HttpTask(null, "ryosei", "create").execute();
            new HttpTask(null, "parcel_event", "create").execute();

            //同期処理部分ここまで
            Intent jimuto_intent = new Intent(MainActivity.this, JimutoChangeActivity.class);
            jimuto_intent.putExtra("Jimuto_name", jimuto_room);
            jimuto_intent.putExtra("Jimuto_id", jimuto_id);
            startActivityForResult(jimuto_intent, JIMUTOCHANGE_ACTIVITY);
            touchsound.playsoundtransition();
        }
    }

    private class RefreshListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {

            eventLogshow();
            touchsound.registercursorblock();
            //同期処理部分
            new HttpTask(null, "parcels", "create").execute();
            new HttpTask(null, "ryosei", "create").execute();
            new HttpTask(null, "parcel_event", "create").execute();

            //同期処理部分ここまで
        }
    }

    public class duty_night_listener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            //enableWaitHandler(1000L,view);
            if (jimuto_id == null) {
                this.showMyDialog(null, getString(R.string.main_not_selected_staff), "", getString(R.string.ok), "");
                touchsound.playsounderror();
            } else {
                Toast.makeText(getMainActivityContext(), "Now Loading...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, NightDutyActivity.class);
                intent.putExtra("Jimuto_id", jimuto_id);
                intent.putExtra("Jimuto_room", jimuto_room);
                //intent.putExtra("Jimuto_name", jimuto_name);
                startActivityForResult(intent, NIGHT_DUTY_ACTIVITY);
                _helper.night_duty_enter_event(db, jimuto_id);
                touchsound.playsoundtransition();
            }
        }

        public void showMyDialog(View view, String title, String mainText, String positiveButton, String neutralButton) {
            DialogFragment dialogFragment = new myDialog();
            Bundle args = new Bundle();
            args.putString("positivebutton", positiveButton);
            args.putString("neutralbutton", neutralButton);
            args.putString("title", title);
            args.putString("maintext", mainText);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "myDialog");
        }

        public void enableWaitHandler(long stopTime, final View view) {

            view.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setEnabled(true);
                }
            }, stopTime);
        }
    }

    private class EventShowListener extends OnOneItemClickListener {
        @Override
        public void onOneItemClick(AdapterView<?> parent, View view, int position, long id) {
            //同期処理部分
            new HttpTask(null, "parcels", "create").execute();
            new HttpTask(null, "ryosei", "create").execute();
            new HttpTask(null, "parcel_event", "create").execute();

            //同期処理部分ここまで
            String event_id = "";
            String ryosei_uid = "";
            String created_at = null;
            String event_type = null;
            String parcel_uid = "";
            String room_name = "";
            String ryosei_name = "";
            String target_event_uid = "";
            String is_finished = "";
            Map<String, String> item = (Map) parent.getItemAtPosition(position);
            //TextView configshow = findViewById(R.id.showText);
            //configshow.setText(item.get("id"));
            item.get("id");
            String sql = "SELECT uid, created_at, event_type,ryosei_uid, parcel_uid, room_name, ryosei_name, target_event_uid FROM parcel_event WHERE uid = '" + item.get("id") + "'";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                event_id = cursor.getString(cursor.getColumnIndex("uid"));
                created_at = cursor.getString(cursor.getColumnIndex("created_at"));
                event_type = String.valueOf(cursor.getInt(cursor.getColumnIndex("event_type")));//int型をStringにしている
                parcel_uid = cursor.getString(cursor.getColumnIndex("parcel_uid"));
                room_name = cursor.getString(cursor.getColumnIndex("room_name"));
                ryosei_name = cursor.getString(cursor.getColumnIndex("ryosei_name"));
                target_event_uid = cursor.getString(cursor.getColumnIndex("target_event_uid"));
                ryosei_uid = cursor.getString(cursor.getColumnIndex("ryosei_uid"));
            }
            cursor.close();

            if (event_id == "") {
                return;
            }

            DialogFragment dialogFragment = new Delete_Event_Dialog();
            Bundle args = new Bundle();
            args.putString("event_id", event_id);
            args.putString("parcel_id", parcel_uid);
            args.putString("ryosei_id", ryosei_uid);
            args.putString("jimuto_id", jimuto_id);
            args.putString("event_type", event_type);
            dialogFragment.setArguments(args);
            //dialogFragment.setTargetFragment(this, EVENT_REFRESH_ACTIVITY);
            dialogFragment.show(getSupportFragmentManager(), "Delete_Event_Dialog");
            touchsound.playsoundtransition();

        }


    }

    private class OldNoteListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            Intent intent = new Intent(MainActivity.this, OldNoteActivity.class);
            startActivity(intent);
        }
    }

    private class OthersButtonListener extends OnOneClickListener{
        @Override
        public void onOneClick(View view){
            final EditText editText = new EditText(MainActivity.this);
            editText.setHint("enter password");
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("管理用画面")
                    .setMessage("パスワードを入力してください。")
                    .setView(editText)
                    .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input = editText.getText().toString();
                            if(input.equals("PassworD")) {
                                Toast.makeText(MainActivity.this, editText.getText(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, OthersActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(MainActivity.this, "パスワードが違います。", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
        }
    }

    public class HttpTask {
        String result;
        String table;
        String method;

        public HttpTask(String result, String table, String method) {
            this.result = result;
            this.table = table;
            this.method = method;

        }

        public void execute() {
            HttpListener httpListener = new HttpListener(result, table, method);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(httpListener);
        }
    }

    private class HttpListener implements Runnable {
        String result;
        String table;
        String method;

        public HttpListener(String result, String table, String method) {
            this.result = result;
            this.table = table;
            this.method = method;
        }


        @Override
        public void run() {
            //String json = getJsonFromDatabase();
            //一旦一度に同期するのは5つ分と決めるが、後から変えられるように作る
            //allaylistにuidを格納する
            int uids_per_one_sync = 5;
            boolean onemore = true;

            while (onemore) {//PC側にデータが残っているorタブレット側にデータが残っている限り回り続けるwhile
                ArrayList<String> uids_for_sync = _helper.select_for_sync(db, table, uids_per_one_sync);
                onemore = (uids_per_one_sync == uids_for_sync.size());//タブレット側で同期すべきデータが尽きたら、ArrayListに5個格納されなくなりfalseになる

                String json = _helper.select_for_json(db, table, uids_for_sync);
                OkHttpPost postTask = new OkHttpPost(MainActivity.this, handler, json, db, _helper, method, table);
                postTask.url = postTask.url + "/" + table + "/" + method;
                postTask.setListener(createListener());
                postTask.execute();

                //待機（通信が返ってきたらthis.resultが更新される）
                int c = 0;
                while (c < 1000) {
                    if (this.result == null) {
                        try {
                            Thread.sleep(100);
                            c++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (this.result.equals("Success")) {
                        //PC側にデータが残っている場合（
                        _helper.update_sharing_status_for_success(db, table, uids_for_sync);
                        result = null;
                        onemore = true;
                        break;
                    } else if (this.result.equals("")) {
                        //PC側にデータが残っていない
                        _helper.update_sharing_status_for_success(db, table, uids_for_sync);
                        result = null;
                        break;
                    }
                }
                if (c == 1000)
                    return;

            }
        }

        private OkHttpPost.Listener createListener() {
            return new OkHttpPost.Listener() {
                @Override
                public void onReceiveResponseFromPC(String res) {
                    result = res;
                }
            };
        }


    }
}
