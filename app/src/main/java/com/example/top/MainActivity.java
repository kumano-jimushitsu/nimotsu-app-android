package com.example.top;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

        //   ButteryChecker butterychecker = new ButteryChecker();
        // Listener?????????
        //  butterychecker.setListener(createListener());
        //  butterychecker.execute(0);

        //??????????????????
        new HttpTask(null, "parcels", "create").execute();
        new HttpTask(null, "ryosei", "create").execute();
        new HttpTask(null, "parcel_event", "create").execute();

        //??????????????????????????????
        eventLogshow();

        // ??????????????????????????????????????????????????????
        ActivityHelper.enableTransparentFooter(this);

        //???????????????????????????
        TextView jimuto_name = findViewById(R.id.main_jimuto_show);
        jimuto_room = _helper.jimuto_at_oncreate(db);
        jimuto_name.setText(jimuto_room);
        jimuto_id=_helper.jimuto_id_at_oncreate(db);

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

    public void event_delete_failed_toast(){

        Toast.makeText(this, "5????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
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
            if (cursor.getString(cursor.getColumnIndex("is_deleted")).equals("1")) {//????????????????????????????????????????????????
                continue;
            }
            if (cursor.getInt(cursor.getColumnIndex("event_type")) == 3) {//??????????????????????????????????????????????????????
                continue;
            }
            Map<String, String> event_raw = new HashMap<>();
            String text = "";

            text += "" + cursor.getString(cursor.getColumnIndex("created_at")).substring(5).replace('-', '/');
            text += "   ";
            switch (cursor.getInt(cursor.getColumnIndex("event_type"))) {
                case 1://????????????
                    text += "????????????       ";
                    break;
                case 2://????????????
                    text += "????????????       ";
                    break;
                case 3://??????????????????????????????????????????????????????????????????????????????
                    //text="????????????????????????????????????";
                    break;
                case 4: // ??????
                    text += "????????????     ";
                    break;
                case 5: // ??????
                    text += "????????????     ";
                    break;
                case 10:
                    text += "???????????????     ";
                    break;
                case 11:
                    text += "?????????????????????????????????     ";
                    break;
                case 12:
                    text += "?????????????????????????????????     ";
            }
            text += cursor.getString(cursor.getColumnIndex("room_name"));
            text += "    ";
            text += cursor.getString(cursor.getColumnIndex("ryosei_name"));

            String event_id = cursor.getString(cursor.getColumnIndex("uid"));
            event_raw.put("id", event_id);
            event_raw.put("text", text);
            show_eventlist.add(event_raw);

        }
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                show_eventlist,
                android.R.layout.simple_list_item_1,
                from,
                to
        );
        ListView listView = findViewById(R.id.event_show);
        listView.setAdapter(adapter);
        ListView listListener = findViewById(R.id.event_show);
        listListener.setOnItemClickListener(new EventShowListener());
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //??????????????????????????????????????????????????????????????????????????????????????????
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case JIMUTOCHANGE_ACTIVITY:
                jimuto_id = intent.getStringExtra("Jimuto_id");
                jimuto_room = intent.getStringExtra("Jimuto_room_name");

                // jimuto_id???null?????????????????????????????????????????????
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
                //??????????????????????????????
        }


        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            qr_uuid = scanResult.getContents();
            // DB??????????????????????????????????????????
            _helper = new DatabaseHelper(MainActivity.this);
            SQLiteDatabase db = _helper.getWritableDatabase();
            String sql = " SELECT uid,is_released,fragile,owner_ryosei_name,owner_room_name,owner_uid FROM parcels WHERE uid ='" + qr_uuid + "';";
            Cursor cursor = db.rawQuery(sql, null);
            int is_released = 0;
            String uid = "";
            String owner_ryosei_name = "";
            String owner_room_name = "";
            int fragile = 0;
            if (cursor.getCount() == 0) {//QR?????????????????????????????????????????????
                this.showMyDialog(null, getString(R.string.error), getString(R.string.qr_no_qr), getString(R.string.ok), "");
            } else if (cursor.getCount() == 1) {//QR???????????????????????????????????????????????????
                while (cursor.moveToNext()) {
                    uid = cursor.getString(cursor.getColumnIndex("owner_uid"));
                    owner_ryosei_name = cursor.getString(cursor.getColumnIndex("owner_ryosei_name"));
                    owner_room_name = cursor.getString(cursor.getColumnIndex("owner_room_name"));
                    is_released = cursor.getInt(cursor.getColumnIndex("is_released"));
                }

                if (is_released == 0) {//????????????????????????????????????
                    this.showQRDialog(null, owner_room_name, owner_ryosei_name, uid);
                } else {//???????????????????????????????????????
                    this.showMyDialog(null, getString(R.string.error), getString(R.string.qr_already), getString(R.string.ok), "");
                }
            } else {//QR?????????????????????????????????????????????????????????
                this.showMyDialog(null, getString(R.string.error), getString(R.string.qr_more_than_two), getString(R.string.ok), "");
            }
        }

        //??????????????????
        new HttpTask(null, "parcels", "create").execute();
        new HttpTask(null, "ryosei", "create").execute();
        new HttpTask(null, "parcel_event", "create").execute();

        //??????????????????????????????
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

    public void showMyDialog(View view, String title, String mainText, String positiveButton, String negativeButton) {
        DialogFragment dialogFragment = new myDialog();


        Bundle args = new Bundle();
        args.putString("positivebutton", positiveButton);
        args.putString("negativebutton", negativeButton);
        args.putString("title", title);
        args.putString("maintext", mainText);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "myDialog");
    }

    /*?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
                //????????????????????????
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                touchsound.playsoundtransition();
                integrator.initiateScan();
            }
        }

        public void showMyDialog(View view, String title, String mainText, String positiveButton, String negativeButton) {
            DialogFragment dialogFragment = new myDialog();
            Bundle args = new Bundle();
            args.putString("positivebutton", positiveButton);
            args.putString("negativebutton", negativeButton);
            args.putString("title", title);
            args.putString("maintext", mainText);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "myDialog");
        }

    }

    private class TourokuListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            //??????????????????
            new HttpTask(null, "parcels", "create").execute();
            new HttpTask(null, "ryosei", "create").execute();
            new HttpTask(null, "parcel_event", "create").execute();

            //??????????????????????????????
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

        public void showMyDialog(View view, String title, String mainText, String positiveButton, String negativeButton) {
            DialogFragment dialogFragment = new myDialog();
            Bundle args = new Bundle();
            args.putString("positivebutton", positiveButton);
            args.putString("negativebutton", negativeButton);
            args.putString("title", title);
            args.putString("maintext", mainText);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "myDialog");
        }
    }

    private class RegisterListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            //??????????????????
            new HttpTask(null, "parcels", "create").execute();
            new HttpTask(null, "ryosei", "create").execute();
            new HttpTask(null, "parcel_event", "create").execute();

            //??????????????????????????????
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

        public void showMyDialog(View view, String title, String mainText, String positiveButton, String negativeButton) {
            DialogFragment dialogFragment = new myDialog();
            Bundle args = new Bundle();
            args.putString("positivebutton", positiveButton);
            args.putString("negativebutton", negativeButton);
            args.putString("title", title);
            args.putString("maintext", mainText);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "myDialog");
        }

    }

    private class JimutoChangeListener extends OnOneClickListener {
        @Override
        public void onOneClick(View view) {
            //??????????????????
            new HttpTask(null, "parcels", "create").execute();
            new HttpTask(null, "ryosei", "create").execute();
            new HttpTask(null, "parcel_event", "create").execute();

            //??????????????????????????????
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
            //??????????????????
            new HttpTask(null, "parcels", "create").execute();
            new HttpTask(null, "ryosei", "create").execute();
            new HttpTask(null, "parcel_event", "create").execute();

            //??????????????????????????????
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
                Intent intent = new Intent(MainActivity.this, NightDutyActivity.class);
                intent.putExtra("Jimuto_id", jimuto_id);
                intent.putExtra("Jimuto_room", jimuto_room);
                //intent.putExtra("Jimuto_name", jimuto_name);
                startActivityForResult(intent, NIGHT_DUTY_ACTIVITY);
                _helper.night_duty_enter_event(db, jimuto_id);
                touchsound.playsoundtransition();
            }
        }

        public void showMyDialog(View view, String title, String mainText, String positiveButton, String negativeButton) {
            DialogFragment dialogFragment = new myDialog();
            Bundle args = new Bundle();
            args.putString("positivebutton", positiveButton);
            args.putString("negativebutton", negativeButton);
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
            //??????????????????
            new HttpTask(null, "parcels", "create").execute();
            new HttpTask(null, "ryosei", "create").execute();
            new HttpTask(null, "parcel_event", "create").execute();

            //??????????????????????????????
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
            String sql = "SELECT uid, created_at, event_type,ryosei_uid, parcel_uid, room_name, ryosei_name, target_event_uid FROM parcel_event WHERE uid = '" +
                    item.get("id") + "'";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                event_id = cursor.getString(cursor.getColumnIndex("uid"));
                created_at = cursor.getString(cursor.getColumnIndex("created_at"));
                event_type = String.valueOf(cursor.getInt(cursor.getColumnIndex("event_type")));//int??????String???????????????
                parcel_uid = cursor.getString(cursor.getColumnIndex("parcel_uid"));
                room_name = cursor.getString(cursor.getColumnIndex("room_name"));
                ryosei_name = cursor.getString(cursor.getColumnIndex("ryosei_name"));
                target_event_uid = cursor.getString(cursor.getColumnIndex("target_event_uid"));
                ryosei_uid = cursor.getString(cursor.getColumnIndex("ryosei_uid"));
            }

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
            //?????????????????????????????????5???????????????????????????????????????????????????????????????
            //allaylist???uid???????????????
            int uids_per_one_sync = 5;
            boolean onemore = true;

            while (onemore) {//PC?????????????????????????????????or?????????????????????????????????????????????????????????????????????while
                ArrayList<String> uids_for_sync = _helper.select_for_sync(db, table, uids_per_one_sync);
                onemore = (uids_per_one_sync == uids_for_sync.size());//???????????????????????????????????????????????????????????????ArrayList???5???????????????????????????false?????????

                String json = _helper.select_for_json(db, table, uids_for_sync);
                OkHttpPost postTask = new OkHttpPost(MainActivity.this, handler, json, db, _helper, method, table);
                postTask.url = postTask.url + "/" + table + "/" + method;
                postTask.setListener(createListener());
                postTask.execute();

                //????????????????????????????????????this.result?????????????????????
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
                        //PC??????????????????????????????????????????
                        _helper.update_sharing_status_for_success(db, table, uids_for_sync);
                        result = null;
                        onemore = true;
                        break;
                    } else if (this.result.equals("")) {
                        //PC????????????????????????????????????
                        _helper.update_sharing_status_for_success(db, table, uids_for_sync);
                        result = null;
                        break;
                    }
                }
                if (c == 1000) return;

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
