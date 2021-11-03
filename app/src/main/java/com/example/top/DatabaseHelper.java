package com.example.top;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * データベースファイル名の定数フィールド。
     */
    private static final String DATABASE_NAME = "nimotsuApp.db";
    /**
     * バージョン情報の定数フィールド。
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * コンストラクタ。
     */
    public DatabaseHelper(Context context) {
        // 親クラスのコンストラクタの呼び出し。
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        //parcelsテーブルの登録
        StringBuilder sb_parcels = new StringBuilder();
        sb_parcels.append("CREATE TABLE parcels (");
        sb_parcels.append(" _id INTEGER PRIMARY KEY,");
        sb_parcels.append(" owner_uid INTEGER,");
        sb_parcels.append(" owner_room_name TEXT,");
        sb_parcels.append(" owner_parcels_name TEXT,");
        sb_parcels.append(" register_datetime TEXT,");
        sb_parcels.append(" register_staff_uid INTEGER,");
        sb_parcels.append(" register_staff_room_name TEXT,");
        sb_parcels.append(" register_staff_parcels_name TEXT,");
        sb_parcels.append(" placement INTEGER default 0,");
        sb_parcels.append(" fragile INTEGER default 0,");
        sb_parcels.append(" is_released INTEGER DEFAULT 0,");
        sb_parcels.append(" release_datetime TEXT,");
        sb_parcels.append(" release_staff_uid INTEGER,");
        sb_parcels.append(" release_staff_room_name TEXT,");
        sb_parcels.append(" release_staff_parcels_name TEXT,");
        sb_parcels.append(" checked_count INTEGER DEFAULT 0,");
        sb_parcels.append(" is_lost INTEGER DEFAULT 0,");
        sb_parcels.append(" lost_datetime TEXT,");
        sb_parcels.append(" is_returned INTEGER DEFAULT 0,");
        sb_parcels.append(" returned_datetime TEXT,");
        sb_parcels.append(" is_operation_error INTEGER DEFAULT 0,");
        sb_parcels.append(" operation_error_type INTEGER,");
        sb_parcels.append(" note TEXT,");
        sb_parcels.append(" is_deleted INTEGER DEFAULT 0,");
        sb_parcels.append(" sharing_status INTEGER default 0");
        sb_parcels.append(");");
        String sql_parcels = sb_parcels.toString();
        db.execSQL(sql_parcels);

        //ryoseiテーブルの登録
        StringBuilder sb_ryosei = new StringBuilder();
        sb_ryosei.append("CREATE TABLE ryosei(");
        sb_ryosei.append(" _id INTEGER PRIMARY KEY,");//
        sb_ryosei.append(" room_name TEXT,");//
        sb_ryosei.append(" ryosei_name TEXT,");//
        sb_ryosei.append(" ryosei_name_kana TEXT,");
        sb_ryosei.append(" ryosei_name_alphabet TEXT,");
        sb_ryosei.append(" block_id INTEGER,");
        sb_ryosei.append(" slack_id TEXT,");
        sb_ryosei.append(" status INTEGER DEFAULT 1,");
        sb_ryosei.append(" parcels_current_count INTEGER DEFAULT 0,");
        sb_ryosei.append(" parcels_total_count INTEGER DEFAULT 0,");
        sb_ryosei.append(" parcels_total_waittime TEXT DEFAULT '0:00:00',");
        sb_ryosei.append(" last_event_id INTEGER,");
        sb_ryosei.append(" last_event_datetime TEXT,");
        sb_ryosei.append("created_at TEXT,");
        sb_ryosei.append("updated_at TEXT,");
        sb_ryosei.append(" sharing_status INTEGER default 0");
        sb_ryosei.append("); ");
        String sql_ryosei = sb_ryosei.toString();
        db.execSQL(sql_ryosei);

        //eventテーブルの登録
        StringBuilder sb_parcel_event = new StringBuilder();
        sb_parcel_event.append("CREATE TABLE parcel_event(");
        sb_parcel_event.append(" _id INTEGER PRIMARY KEY,");
        sb_parcel_event.append(" created_at TEXT,");
        sb_parcel_event.append(" event_type INTEGER,");
        sb_parcel_event.append(" parcel_uid INTEGER,");
        sb_parcel_event.append(" ryosei_uid INTEGER,");
        sb_parcel_event.append(" room_name TEXT,");
        sb_parcel_event.append(" ryosei_name TEXT,");
        sb_parcel_event.append("target_event_uid INTEGER,");
        sb_parcel_event.append(" note TEXT,");
        sb_parcel_event.append(" is_finished INTEGER DEFAULT 0,");
        sb_parcel_event.append(" is_deleted INTEGER DEFAULT 0,");
        sb_parcel_event.append(" sharing_status INTEGER default 0");
        sb_parcel_event.append(");");
        String sql_parcel_event = sb_parcel_event.toString();
        db.execSQL(sql_parcel_event);

        insert_test_ryosei(db);


    }
    void insert_test_ryosei(SQLiteDatabase db){
        StringBuilder sb_insert_test_ryosei = new StringBuilder();
        sb_insert_test_ryosei.append("insert into ryosei (ryosei_name,block_id,room_name) values ");
        sb_insert_test_ryosei.append("('テスト100',1,'A100'),");
        sb_insert_test_ryosei.append("('テスト101',1,'A101'),");
        sb_insert_test_ryosei.append("('テスト102',1,'A101'),");
        sb_insert_test_ryosei.append("('テスト103',1,'A101'),");
        sb_insert_test_ryosei.append("('テスト104',1,'A101'),");
        sb_insert_test_ryosei.append("('テスト105',1,'A102'),");
        sb_insert_test_ryosei.append("('テスト106',1,'A102'),");
        sb_insert_test_ryosei.append("('テスト107',1,'A102'),");
        sb_insert_test_ryosei.append("('テスト108',1,'A102'),");
        sb_insert_test_ryosei.append("('テスト109',1,'A103'),");
        sb_insert_test_ryosei.append("('テスト110',1,'A103'),");
        sb_insert_test_ryosei.append("('テスト111',1,'A103'),");
        sb_insert_test_ryosei.append("('テスト112',1,'A103'),");
        sb_insert_test_ryosei.append("('テスト113',1,'A104'),");
        sb_insert_test_ryosei.append("('テスト114',1,'A104'),");
        sb_insert_test_ryosei.append("('テスト115',1,'A104'),");
        sb_insert_test_ryosei.append("('テスト116',1,'A104'),");
        sb_insert_test_ryosei.append("('テスト117',1,'A105'),");
        sb_insert_test_ryosei.append("('テスト118',1,'A105'),");
        sb_insert_test_ryosei.append("('テスト119',1,'A105'),");
        sb_insert_test_ryosei.append("('テスト120',1,'A105'),");
        sb_insert_test_ryosei.append("('テスト121',1,'A106'),");
        sb_insert_test_ryosei.append("('テスト122',1,'A106'),");
        sb_insert_test_ryosei.append("('テスト123',1,'A106'),");
        sb_insert_test_ryosei.append("('テスト124',1,'A106'),");
        sb_insert_test_ryosei.append("('テスト125',1,'A107'),");
        sb_insert_test_ryosei.append("('テスト126',1,'A107'),");
        sb_insert_test_ryosei.append("('テスト127',1,'A107'),");
        sb_insert_test_ryosei.append("('テスト128',1,'A107'),");
        sb_insert_test_ryosei.append("('テスト129',1,'A108'),");
        sb_insert_test_ryosei.append("('テスト130',1,'A108'),");
        sb_insert_test_ryosei.append("('テスト131',1,'A108'),");
        sb_insert_test_ryosei.append("('テスト132',1,'A108'),");
        sb_insert_test_ryosei.append("('テスト133',1,'A109'),");
        sb_insert_test_ryosei.append("('テスト134',1,'A109'),");
        sb_insert_test_ryosei.append("('テスト135',1,'A109'),");
        sb_insert_test_ryosei.append("('テスト136',1,'A109'),");
        sb_insert_test_ryosei.append("('テスト137',1,'A110'),");
        sb_insert_test_ryosei.append("('テスト138',1,'A110'),");
        sb_insert_test_ryosei.append("('テスト139',1,'A110'),");
        sb_insert_test_ryosei.append("('テスト140',1,'A110'),");
        sb_insert_test_ryosei.append("('テスト141',1,'A111'),");
        sb_insert_test_ryosei.append("('テスト142',1,'A111'),");
        sb_insert_test_ryosei.append("('テスト143',1,'A111'),");
        sb_insert_test_ryosei.append("('テスト144',1,'A111'),");
        sb_insert_test_ryosei.append("('テスト145',2,'A201'),");
        sb_insert_test_ryosei.append("('テスト146',2,'A201'),");
        sb_insert_test_ryosei.append("('テスト147',2,'A201'),");
        sb_insert_test_ryosei.append("('テスト148',2,'A201'),");
        sb_insert_test_ryosei.append("('テスト149',2,'A202'),");
        sb_insert_test_ryosei.append("('テスト150',2,'A202'),");
        sb_insert_test_ryosei.append("('テスト151',2,'A202'),");
        sb_insert_test_ryosei.append("('テスト152',2,'A202'),");
        sb_insert_test_ryosei.append("('テスト153',2,'A203'),");
        sb_insert_test_ryosei.append("('テスト154',2,'A203'),");
        sb_insert_test_ryosei.append("('テスト155',2,'A203'),");
        sb_insert_test_ryosei.append("('テスト156',2,'A203'),");
        sb_insert_test_ryosei.append("('テスト157',2,'A204'),");
        sb_insert_test_ryosei.append("('テスト158',2,'A204'),");
        sb_insert_test_ryosei.append("('テスト159',2,'A204'),");
        sb_insert_test_ryosei.append("('テスト160',2,'A204'),");
        sb_insert_test_ryosei.append("('テスト161',2,'A205'),");
        sb_insert_test_ryosei.append("('テスト162',2,'A205'),");
        sb_insert_test_ryosei.append("('テスト163',2,'A205'),");
        sb_insert_test_ryosei.append("('テスト164',2,'A205'),");
        sb_insert_test_ryosei.append("('テスト165',2,'A206'),");
        sb_insert_test_ryosei.append("('テスト166',2,'A206'),");
        sb_insert_test_ryosei.append("('テスト167',2,'A206'),");
        sb_insert_test_ryosei.append("('テスト168',2,'A206'),");
        sb_insert_test_ryosei.append("('テスト169',2,'A207'),");
        sb_insert_test_ryosei.append("('テスト170',2,'A207'),");
        sb_insert_test_ryosei.append("('テスト171',2,'A207'),");
        sb_insert_test_ryosei.append("('テスト172',2,'A207'),");
        sb_insert_test_ryosei.append("('テスト173',2,'A208'),");
        sb_insert_test_ryosei.append("('テスト174',2,'A208'),");
        sb_insert_test_ryosei.append("('テスト175',2,'A208'),");
        sb_insert_test_ryosei.append("('テスト176',2,'A208'),");
        sb_insert_test_ryosei.append("('テスト177',2,'A209'),");
        sb_insert_test_ryosei.append("('テスト178',2,'A209'),");
        sb_insert_test_ryosei.append("('テスト179',2,'A209'),");
        sb_insert_test_ryosei.append("('テスト180',2,'A209'),");
        sb_insert_test_ryosei.append("('テスト181',2,'A210'),");
        sb_insert_test_ryosei.append("('テスト182',2,'A210'),");
        sb_insert_test_ryosei.append("('テスト183',2,'A210'),");
        sb_insert_test_ryosei.append("('テスト184',2,'A210'),");
        sb_insert_test_ryosei.append("('テスト185',2,'A211'),");
        sb_insert_test_ryosei.append("('テスト186',2,'A211'),");
        sb_insert_test_ryosei.append("('テスト187',2,'A211'),");
        sb_insert_test_ryosei.append("('テスト188',2,'A211'),");
        sb_insert_test_ryosei.append("('テスト189',3,'A301'),");
        sb_insert_test_ryosei.append("('テスト190',3,'A301'),");
        sb_insert_test_ryosei.append("('テスト191',3,'A301'),");
        sb_insert_test_ryosei.append("('テスト192',3,'A301'),");
        sb_insert_test_ryosei.append("('テスト193',3,'A302'),");
        sb_insert_test_ryosei.append("('テスト194',3,'A302'),");
        sb_insert_test_ryosei.append("('テスト195',3,'A302'),");
        sb_insert_test_ryosei.append("('テスト196',3,'A302'),");
        sb_insert_test_ryosei.append("('テスト197',3,'A303'),");
        sb_insert_test_ryosei.append("('テスト198',3,'A303'),");
        sb_insert_test_ryosei.append("('テスト199',3,'A303'),");
        sb_insert_test_ryosei.append("('テスト200',3,'A303'),");
        sb_insert_test_ryosei.append("('テスト201',3,'A304'),");
        sb_insert_test_ryosei.append("('テスト202',3,'A304'),");
        sb_insert_test_ryosei.append("('テスト203',3,'A304'),");
        sb_insert_test_ryosei.append("('テスト204',3,'A304'),");
        sb_insert_test_ryosei.append("('テスト205',3,'A305'),");
        sb_insert_test_ryosei.append("('テスト206',3,'A305'),");
        sb_insert_test_ryosei.append("('テスト207',3,'A305'),");
        sb_insert_test_ryosei.append("('テスト208',3,'A305'),");
        sb_insert_test_ryosei.append("('テスト209',3,'A306'),");
        sb_insert_test_ryosei.append("('テスト210',3,'A306'),");
        sb_insert_test_ryosei.append("('テスト211',3,'A306'),");
        sb_insert_test_ryosei.append("('テスト212',3,'A306'),");
        sb_insert_test_ryosei.append("('テスト213',3,'A307'),");
        sb_insert_test_ryosei.append("('テスト214',3,'A307'),");
        sb_insert_test_ryosei.append("('テスト215',3,'A307'),");
        sb_insert_test_ryosei.append("('テスト216',3,'A307'),");
        sb_insert_test_ryosei.append("('テスト217',3,'A308'),");
        sb_insert_test_ryosei.append("('テスト218',3,'A308'),");
        sb_insert_test_ryosei.append("('テスト219',3,'A308'),");
        sb_insert_test_ryosei.append("('テスト220',3,'A308'),");
        sb_insert_test_ryosei.append("('テスト221',3,'A309'),");
        sb_insert_test_ryosei.append("('テスト222',3,'A309'),");
        sb_insert_test_ryosei.append("('テスト223',3,'A309'),");
        sb_insert_test_ryosei.append("('テスト224',3,'A309'),");
        sb_insert_test_ryosei.append("('テスト225',3,'A310'),");
        sb_insert_test_ryosei.append("('テスト226',3,'A310'),");
        sb_insert_test_ryosei.append("('テスト227',3,'A310'),");
        sb_insert_test_ryosei.append("('テスト228',3,'A310'),");
        sb_insert_test_ryosei.append("('テスト229',3,'A311'),");
        sb_insert_test_ryosei.append("('テスト230',3,'A311'),");
        sb_insert_test_ryosei.append("('テスト231',3,'A311'),");
        sb_insert_test_ryosei.append("('テスト232',3,'A311'),");
        sb_insert_test_ryosei.append("('テスト231',4,'A401'),");
        sb_insert_test_ryosei.append("('テスト232',4,'A401'),");
        sb_insert_test_ryosei.append("('テスト233',4,'A401'),");
        sb_insert_test_ryosei.append("('テスト234',4,'A401'),");
        sb_insert_test_ryosei.append("('テスト235',4,'A402'),");
        sb_insert_test_ryosei.append("('テスト236',4,'A402'),");
        sb_insert_test_ryosei.append("('テスト237',4,'A402'),");
        sb_insert_test_ryosei.append("('テスト238',4,'A402'),");
        sb_insert_test_ryosei.append("('テスト239',4,'A403'),");
        sb_insert_test_ryosei.append("('テスト240',4,'A403'),");
        sb_insert_test_ryosei.append("('テスト241',4,'A403'),");
        sb_insert_test_ryosei.append("('テスト242',4,'A403'),");
        sb_insert_test_ryosei.append("('テスト245',10,'図書室'),");
        sb_insert_test_ryosei.append("('テスト246',10,'図書室'),");
        sb_insert_test_ryosei.append("('テスト247',10,'図書室'),");
        sb_insert_test_ryosei.append("('テスト248',10,'図書室'),");
        sb_insert_test_ryosei.append("('テスト249',10,'図書室'),");
        sb_insert_test_ryosei.append("('テスト250',10,'図書室'),");
        sb_insert_test_ryosei.append("('テスト251',10,'旧印刷室'),");
        sb_insert_test_ryosei.append("('テスト252',10,'旧印刷室'),");
        sb_insert_test_ryosei.append("('テスト253',10,'旧印刷室'),");
        sb_insert_test_ryosei.append("('テスト254',10,'旧印刷室'),");
        sb_insert_test_ryosei.append("('テスト255',10,'旧会議室'),");
        sb_insert_test_ryosei.append("('テスト256',10,'旧会議室'),");
        sb_insert_test_ryosei.append("('テスト257',10,'旧会議室'),");
        sb_insert_test_ryosei.append("('テスト258',10,'旧会議室'),");
        sb_insert_test_ryosei.append("('テスト259',10,'旧会議室');");
        String sql_insert_test_ryosei = sb_insert_test_ryosei.toString();
        db.execSQL(sql_insert_test_ryosei);
    }


    public void addParcel (
            SQLiteDatabase db,
            String owner_uid,
            String owner_room,
            String owner_ryosei_name,
            String register_staff_uid,
            String register_staff_room_name,
            String register_staff_ryosei_name,
            int placement){
        StringBuilder sb_insert_Parcel = new StringBuilder();
        sb_insert_Parcel.append("insert into parcels (" +
                "owner_uid,owner_room_name,owner_parcels_name," +
                "register_datetime," +
                "register_staff_uid,register_staff_room_name,register_staff_parcels_name,placement" +
                ") values (");
        sb_insert_Parcel.append( owner_uid +",");
        sb_insert_Parcel.append( " \"" + owner_room +" \",");
        sb_insert_Parcel.append( " \"" + owner_ryosei_name +"\",");
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format( dateObj );
        sb_insert_Parcel.append( " \"" + string_register_time +"\",");
        sb_insert_Parcel.append( register_staff_uid +",");
        sb_insert_Parcel.append( " \"" + register_staff_room_name +"\",");
        sb_insert_Parcel.append( " \"" + register_staff_ryosei_name +"\",");
        sb_insert_Parcel.append( " \"" + placement +"\")");

        String sql_insert_test_parcel = sb_insert_Parcel.toString();
        db.execSQL(sql_insert_test_parcel);

        nimotsuCountAdder(db,owner_uid);
        event_add_touroku(db,owner_uid,owner_room,owner_ryosei_name);
    }

    public void event_add_touroku(
            SQLiteDatabase db,
            String ryosei_id,
            String room_name,
            String ryosei_name){
        StringBuilder sb_insert_Parcel = new StringBuilder();
        sb_insert_Parcel.append("insert into parcel_event (" +
                "created_at," +
                "event_type," +
                "parcel_uid," +
                "ryosei_uid," +
                "room_name," +
                "ryosei_name" +
                ") values (");

        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format( dateObj );
        sb_insert_Parcel.append( " \"" + string_register_time +"\",");
        sb_insert_Parcel.append( " \"" + 1 +" \",");
        Cursor cursor = db.rawQuery( "SELECT max(_id) as maxid  FROM parcels",null);
        cursor.moveToFirst();
        String maxid = String.valueOf(cursor.getInt(cursor.getColumnIndex("maxid")));
        sb_insert_Parcel.append( " \"" + maxid +" \",");
        sb_insert_Parcel.append( ryosei_id +",");
        sb_insert_Parcel.append( " \"" + room_name +" \",");
        sb_insert_Parcel.append( " \"" + ryosei_name +"\")");
        String sql_insert_event = sb_insert_Parcel.toString();
        db.execSQL(sql_insert_event);

    }

    public void event_add_uketori(
            SQLiteDatabase db,
            String ryosei_id,
            String room_name,
            String ryosei_name){
        StringBuilder sb_insert_Parcel = new StringBuilder();
        sb_insert_Parcel.append("insert into parcel_event (" +
                "created_at," +
                "event_type," +
                "parcel_uid," +
                "ryosei_uid," +
                "room_name," +
                "ryosei_name" +
                ") values (");

        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format( dateObj );
        sb_insert_Parcel.append( " \"" + string_register_time +"\",");
        sb_insert_Parcel.append( " \"" + 2 +" \",");
        Cursor cursor = db.rawQuery( "SELECT max(_id) as maxid  FROM parcels",null);
        cursor.moveToFirst();
        String maxid = String.valueOf(cursor.getInt(cursor.getColumnIndex("maxid")));
        sb_insert_Parcel.append( " \"" + maxid +" \",");
        sb_insert_Parcel.append( ryosei_id +",");
        sb_insert_Parcel.append( " \"" + room_name +" \",");
        sb_insert_Parcel.append( " \"" + ryosei_name +"\")");
        String sql_insert_event = sb_insert_Parcel.toString();
        db.execSQL(sql_insert_event);

    }

    public void receiveParcels(
            SQLiteDatabase db,
            String owner_id,
            String owner_room,
            String owner_ryosei_name,
            String parcels_uid,
            String release_staff_uid,
            String release_staff_room_name,
            String release_staff_ryosei_name){
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format( dateObj );
        String sql = "UPDATE parcels SET "+
                " release_staff_uid = " + release_staff_uid + " ," +
                " release_staff_room_name = '" + release_staff_room_name + "' ," +
                " release_staff_parcels_name = '" + release_staff_ryosei_name +"' ," +
                " is_released = " + "1" +" ," +
                " release_datetime =" + " \"" + string_register_time +"\"" +
                " WHERE _id =" + parcels_uid;
        db.execSQL(sql);
        nimotsuCountSubber(db, owner_id);
        event_add_uketori(db,owner_id,owner_room,owner_ryosei_name);

    }


    public List<Map<String,String>> nimotsuCountOfRyosei (SQLiteDatabase db, String owner_id){
        //荷物IDとラベル(日時、受け取り事務当、場所）を返す。
        List<Map<String,String>> show_owners_parcels = new ArrayList<>();
        String sql = "SELECT _id, placement, register_datetime," +
                "register_staff_room_name, register_staff_parcels_name " +
                "FROM parcels WHERE is_released = 0 AND owner_uid =" + owner_id;
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Map<String, String> parcels_raw = new HashMap<>();
            int index_id = cursor.getColumnIndex("_id");
            int index_placement = cursor.getColumnIndex("placement");
            int index_register_datetime = cursor.getColumnIndex("register_datetime");
            int index_register_staff_room_name = cursor.getColumnIndex("register_staff_room_name");
            int index_register_staff_parcels_name = cursor.getColumnIndex("register_staff_parcels_name");
            String rabel = "";
            String parcels_id = "";
            parcels_id = String.valueOf(cursor.getInt(index_id));
            rabel += "登録日時　" + cursor.getString(index_register_datetime);
            rabel += " ";
            rabel += "受取事務当　" + cursor.getString(index_register_staff_room_name);
            rabel += " ";
            rabel += cursor.getString(index_register_staff_parcels_name);
            rabel += " ";
            switch (cursor.getInt(index_placement)){
                case 0 :
                    rabel += "普通";
                    break;
                case 1 :
                    rabel += "冷蔵";
                    break;
                case 2 :
                    rabel += "冷凍";
                    break;
                case 3 :
                    rabel += "大型";
                    break;
                case 4 :
                    rabel += "不在票";
                    break;
                case 5 :
                    rabel += "その他";
                    break;
            }
            parcels_raw.put("rabel",rabel);
            parcels_raw.put("parcels_id",parcels_id);
            show_owners_parcels.add(parcels_raw);
        }
        return show_owners_parcels;
    }

    public List<Map<String,String>> nightdutylist (SQLiteDatabase db){
        //荷物IDとラベル(日時、受け取り事務当、場所）を返す。
        List<Map<String,String>> show_owners_parcels = new ArrayList<>();
        String sql = "SELECT _id, placement, register_datetime,lost_datetime," +
                "register_staff_room_name, register_staff_parcels_name,owner_room_name,owner_parcels_name " +
                "FROM parcels WHERE is_released = 0 and is_deleted=0";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Map<String, String> parcels_raw = new HashMap<>();
            int index_id = cursor.getColumnIndex("_id");
            int index_placement = cursor.getColumnIndex("placement");
            int index_lost_datetime = cursor.getColumnIndex("lost_datetime");
            int index_owner_room_name = cursor.getColumnIndex("owner_room_name");
            int index_register_staff_parcels_name = cursor.getColumnIndex("owner_parcels_name");
            String rabel = "";
            String parcels_id = "";
            parcels_id = String.valueOf(cursor.getInt(index_id));
            rabel += "持ち主　" + cursor.getString(index_owner_room_name);
            rabel += " ";
            rabel += cursor.getString(index_register_staff_parcels_name);
            rabel += " ";
            switch (cursor.getInt(index_placement)){
                case 0 :
                    rabel += "普通";
                    break;
                case 1 :
                    rabel += "冷蔵";
                    break;
                case 2 :
                    rabel += "冷凍";
                    break;
                case 3 :
                    rabel += "大型";
                    break;
                case 4 :
                    rabel += "不在票";
                    break;
                case 5 :
                    rabel += "その他";
                    break;
            }
            rabel += " ";
            rabel += "登録日時　" + cursor.getString(index_lost_datetime);
            parcels_raw.put("rabel",rabel);
            parcels_raw.put("parcels_id",parcels_id);
            show_owners_parcels.add(parcels_raw);
        }
        return show_owners_parcels;
    }


    public void nimotsuCountAdder( SQLiteDatabase db,String owner_id){
        int parcels_current_count = 0;
        int parcels_total_count = 0;
        //寮生に荷物カウントを追加する。

        //owner_idの寮生を取得
        String sql = "SELECT parcels_current_count, parcels_total_count FROM ryosei  WHERE _id = "+ owner_id;
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);


        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得。
            int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            parcels_current_count = cursor.getInt(index_parcels_current_count);
                    // カラムのインデックス値を元に実際のデータを取得。
            int index_parcels_total_count = cursor.getColumnIndex("parcels_total_count");
            parcels_total_count = cursor.getInt(index_parcels_total_count);
            // カラムのインデックス値を元に実際のデータを取得。
        }
        sql = "UPDATE ryosei SET parcels_current_count ="+ String.valueOf(parcels_current_count+1)
                +", parcels_total_count =" + String.valueOf(parcels_total_count+1) + " WHERE _id =" + owner_id;
        db.execSQL(sql);
    }

    public void nimotsuCountSubber( SQLiteDatabase db,String owner_id){
        int parcels_current_count = 1;
        //int parcels_total_count = 0;
        //寮生に荷物カウントを追加する。

        //owner_idの寮生を取得
        String sql = "SELECT parcels_current_count, parcels_total_count FROM ryosei  WHERE _id = "+ owner_id;
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);


        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得。
            int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            parcels_current_count = cursor.getInt(index_parcels_current_count);
            // カラムのインデックス値を元に実際のデータを取得。
            int index_parcels_total_count = cursor.getColumnIndex("parcels_total_count");
            //parcels_total_count = cursor.getInt(index_parcels_total_count);
            // カラムのインデックス値を元に実際のデータを取得。
            parcels_current_count --;
        }
        sql = "UPDATE ryosei SET parcels_current_count ="+ String.valueOf(parcels_current_count)
                + " WHERE _id =" + owner_id;
        db.execSQL(sql);
    }

    public void night_check_updater(SQLiteDatabase db, String parcels_uid){
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        String string_register_time = format.format( dateObj );
        String sql = "UPDATE parcels SET "+
                " lost_datetime =" + " \"" + string_register_time +"\"" +
                " WHERE _id =" + parcels_uid;
        db.execSQL(sql);
    }

    public void event_add_night_duty(SQLiteDatabase db, String staffid, String staffroom, String staffname){

    }



    public void delete_event( SQLiteDatabase db,String event_id, String ryosei_id,String parcel_id,String event_type){
        //event idは1 or 2が入る　1が登録のイベントを消し込むとき、2が受取のイベントを消し込むとき

        //room_name, ryosei_name, total_parcels_count, current_parcels_countを取得する
        String sql = "SELECT room_name, ryosei_name, parcels_total_count, parcels_current_count  FROM ryosei where _id="+ryosei_id;
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();
        String room_name = cursor.getString(cursor.getColumnIndex("room_name"));
        String ryosei_name = cursor.getString(cursor.getColumnIndex("ryosei_name"));
        String parcels_total_count = cursor.getString(cursor.getColumnIndex("parcels_total_count"));
        String parcels_current_count = cursor.getString(cursor.getColumnIndex("parcels_current_count"));
        int parcels_TC=Integer.parseInt(parcels_total_count),parcels_CC=Integer.parseInt(parcels_current_count) ;
        if(event_type.equals("1")){
            parcels_TC--;
            parcels_CC--;
        }else{
            parcels_TC++;
            parcels_CC++;
        }
        parcels_total_count=String.valueOf(parcels_TC);
        parcels_current_count=String.valueOf(parcels_CC);

        //created_atを取得する
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        // 日時情報を指定フォーマットの文字列で取得
        String created_at = format.format( dateObj );


                //event tableのupdate、insert
        //ryosei tableのupdate（荷物数カウント）
        //parcelstableのupdate（論理削除）
        //が必要

        //event
        sql = "update parcel_event set is_deleted=1 where _id="+event_id;
        db.execSQL(sql);
        sql="insert into parcel_event(created_at,event_type,parcel_uid,ryosei_uid,room_name,ryosei_name,target_event_uid,is_finished)";
        sql+="values('";
        sql+=created_at+"',3,"+parcel_id+","+ryosei_id+",";
        sql+="'"+room_name +"',";
        sql+="'"+ryosei_name+"',";
        sql+=event_id+",1);";
        db.execSQL(sql);

        //ryosei
        sql="update ryosei set parcels_total_count="+
                    parcels_total_count+
                    ",parcels_current_count="+
                    parcels_current_count
                +" where _id="+ryosei_id;
        db.execSQL(sql);

        //parcels
        sql="update parcels set is_deleted=1 where _id ="+ parcel_id;
        db.execSQL(sql);
    }




    public String select_ryosei_show_json(SQLiteDatabase db){
        //owner_idの寮生を取得
        String sql = "SELECT *  FROM ryosei limit 4";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.getCount()==0)return "";


        String json_str="[\n";

        while(cursor.moveToNext()) {
            json_str += "{\n";
            for(enum_ryosei column:enum_ryosei.values()){
                String col = column.toString();
                String val = cursor.getString(cursor.getColumnIndex(col));
                json_str+=col;
                json_str+=": ";
                if(column.getCode()==1&&val!=null)json_str+="\"";
                json_str+=val;
                if(column.getCode()==1&&val!=null)json_str+="\"";
                json_str+=",\n";
            }
            //末尾からカンマを削除
            json_str=json_str.substring(0,json_str.length()-2);
            json_str+="\n";
            json_str+="},\n";

        }
        json_str=json_str.substring(0,json_str.length()-2);
        json_str+="\n";
        json_str+="]";
        return json_str;
    }

    public String select_parcels_show_json(SQLiteDatabase db){
        //owner_idの寮生を取得
        String sql = "SELECT *  FROM parcels order by _id";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.getCount()==0)return "";

        String json_str="[\n";

        while(cursor.moveToNext()) {
            json_str += "{\n";
            for(enum_parcels column:enum_parcels.values()){
                String col = column.toString();
                String val = cursor.getString(cursor.getColumnIndex(col));
                json_str+=col;
                json_str+=": ";
                if(column.getCode()==1&&val!=null)json_str+="\"";
                json_str+=val;
                if(column.getCode()==1&&val!=null)json_str+="\"";
                json_str+=",\n";
            }
            //末尾からカンマを削除
            json_str=json_str.substring(0,json_str.length()-2);
            json_str+="\n";
            json_str+="},\n";

        }
        json_str=json_str.substring(0,json_str.length()-2);
        json_str+="\n";
        json_str+="]";
        return json_str;
    }
    public String select_event_show_json(SQLiteDatabase db){
        //owner_idの寮生を取得
        String sql = "SELECT *  FROM parcel_event order by _id limit 4";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.getCount()==0)return "";
        String json_str="[\n";

        while(cursor.moveToNext()) {
            json_str += "{\n";
            for(enum_event column:enum_event.values()){
                String col = column.toString();
                String val = cursor.getString(cursor.getColumnIndex(col));
                json_str+=col;
                json_str+=": ";
                if(column.getCode()==1&&val!=null)json_str+="\"";
                json_str+=val;
                if(column.getCode()==1&&val!=null)json_str+="\"";
                json_str+=",\n";
            }
            //末尾からカンマを削除
            json_str=json_str.substring(0,json_str.length()-2);
            json_str+="\n";
            json_str+="},\n";

        }
        json_str=json_str.substring(0,json_str.length()-2);
        json_str+="\n";
        json_str+="]";
        return json_str;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}