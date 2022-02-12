package com.example.top;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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
        sb_parcels.append(" uid TEXT NOT NULL PRIMARY KEY,");
        sb_parcels.append(" owner_uid TEXT,");
        sb_parcels.append(" owner_room_name TEXT,");
        sb_parcels.append(" owner_ryosei_name TEXT,");
        sb_parcels.append(" register_datetime TEXT,");
        sb_parcels.append(" register_staff_uid TEXT,");
        sb_parcels.append(" register_staff_room_name TEXT,");
        sb_parcels.append(" register_staff_ryosei_name TEXT,");
        sb_parcels.append(" placement INTEGER default 0,");
        sb_parcels.append(" fragile INTEGER default 0,");
        sb_parcels.append(" is_released INTEGER DEFAULT 0,");
        sb_parcels.append(" release_agent_uid TEXT,");
        sb_parcels.append(" release_datetime TEXT,");
        sb_parcels.append(" release_staff_uid TEXT,");
        sb_parcels.append(" release_staff_room_name TEXT,");
        sb_parcels.append(" release_staff_ryosei_name TEXT,");
        sb_parcels.append(" checked_count INTEGER DEFAULT 0,");
        sb_parcels.append(" is_lost INTEGER DEFAULT 0,");
        sb_parcels.append(" lost_datetime TEXT,");
        sb_parcels.append(" is_returned INTEGER DEFAULT 0,");
        sb_parcels.append(" returned_datetime TEXT,");
        sb_parcels.append(" is_operation_error INTEGER DEFAULT 0,");
        sb_parcels.append(" operation_error_type INTEGER,");
        sb_parcels.append(" note TEXT,");
        sb_parcels.append(" is_deleted INTEGER DEFAULT 0,");
        sb_parcels.append(" sharing_status INTEGER,");
        sb_parcels.append(" sharing_time TEXT");

        //release_agent_uid nvarchar(36)
        sb_parcels.append(");");
        String sql_parcels = sb_parcels.toString();
        db.execSQL(sql_parcels);

        //ryoseiテーブルの登録
        StringBuilder sb_ryosei = new StringBuilder();
        sb_ryosei.append("CREATE TABLE ryosei(");
        sb_ryosei.append(" uid TEXT not null PRIMARY KEY,");//
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
        sb_ryosei.append(" created_at TEXT,");
        sb_ryosei.append(" updated_at TEXT,");
        sb_ryosei.append(" sharing_status INTEGER,");
        sb_ryosei.append(" sharing_time TEXT");
        sb_ryosei.append("); ");
        String sql_ryosei = sb_ryosei.toString();
        db.execSQL(sql_ryosei);
        //eventテーブルの登録
        StringBuilder sb_parcel_event = new StringBuilder();
        sb_parcel_event.append("CREATE TABLE parcel_event(");
        sb_parcel_event.append(" uid TEXT not null PRIMARY KEY,");
        sb_parcel_event.append(" created_at TEXT,");
        sb_parcel_event.append(" event_type INTEGER,");
        sb_parcel_event.append(" parcel_uid TEXT,");
        sb_parcel_event.append(" ryosei_uid TEXT,");
        sb_parcel_event.append(" room_name TEXT,");
        sb_parcel_event.append(" ryosei_name TEXT,");
        sb_parcel_event.append(" target_event_uid TEXT,");
        sb_parcel_event.append(" note TEXT,");
        sb_parcel_event.append(" is_after_fixed_time INTEGER DEFAULT 0,");
        sb_parcel_event.append(" is_finished INTEGER DEFAULT 0,");
        sb_parcel_event.append(" is_deleted INTEGER DEFAULT 0,");
        sb_parcel_event.append(" sharing_status INTEGER,");
        sb_parcel_event.append(" sharing_time TEXT");
        sb_parcel_event.append(");");
        String sql_parcel_event = sb_parcel_event.toString();
        db.execSQL(sql_parcel_event);


    }


    public void register(
            SQLiteDatabase db,
            String owner_uid,
            String register_staff_uid,
            String register_staff_room_name,
            String register_staff_ryosei_name,
            int placement,
            String note) {
        String uuid = UUID.randomUUID().toString();//parcelsのuid

        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 日時情報を指定フォーマットの文字列で取得
        String register_time = format.format(dateObj);
        //owner_idの寮生を取得
        String sql = "SELECT room_name,ryosei_name, parcels_current_count, parcels_total_count FROM ryosei  WHERE uid = '" + owner_uid + "'";
        Cursor cursor = db.rawQuery(sql, null);


        cursor.moveToNext();
        String owner_room = cursor.getString(cursor.getColumnIndex("room_name"));
        String owner_ryosei_name = cursor.getString(cursor.getColumnIndex("ryosei_name"));
        int current_count = cursor.getInt(cursor.getColumnIndex("parcels_current_count"));
        int total_count = cursor.getInt(cursor.getColumnIndex("parcels_total_count"));
        // カラムのインデックス値を元に実際のデータを取得。

        register_parcels(db, uuid, owner_uid, owner_room, owner_ryosei_name, register_time, register_staff_uid, register_staff_room_name, register_staff_ryosei_name, placement, note);
        register_ryosei(db, owner_uid, current_count, total_count, register_time);//ryoseiテーブルの更新(update)
        register_event(db, register_time, uuid, owner_uid, owner_room, owner_ryosei_name, note);//eventテーブルの更新(update)
    }

    public void register_parcels(
            SQLiteDatabase db,
            String uid,
            String owner_uid,
            String owner_room,
            String owner_ryosei_name,
            String register_time,
            String register_staff_uid,
            String register_staff_room_name,
            String register_staff_ryosei_name,
            int placement,
            String note) {
        StringBuilder sb_insert_Parcel = new StringBuilder();
        sb_insert_Parcel.append("insert into parcels (" +
                "uid,owner_uid,owner_room_name,owner_ryosei_name," +
                "register_datetime," +
                "register_staff_uid,register_staff_room_name,register_staff_ryosei_name,placement,sharing_status" +
                ") values (");
        sb_insert_Parcel.append("'" + uid + "',");
        sb_insert_Parcel.append("'" + owner_uid + "',");
        sb_insert_Parcel.append("'" + owner_room + "',");
        sb_insert_Parcel.append("'" + owner_ryosei_name + "',");
        sb_insert_Parcel.append("'" + register_time + "',");
        sb_insert_Parcel.append("'" + register_staff_uid + "',");
        sb_insert_Parcel.append("'" + register_staff_room_name + "',");
        sb_insert_Parcel.append("'" + register_staff_ryosei_name + "',");
        sb_insert_Parcel.append("'" + placement + "',10) ");


        String sql_insert_test_parcel = sb_insert_Parcel.toString();
        db.execSQL(sql_insert_test_parcel);
        if (note != "") {
            db.execSQL("update parcels set note = '" + note + "' where uid = '" + uid + "';");
        }
    }

    public void register_ryosei(SQLiteDatabase db, String owner_id, int parcels_current_count, int parcels_total_count, String updated_at) {
        //寮生に荷物カウントを追加する.
        String sql = "UPDATE ryosei SET " +
                "parcels_current_count =" + (parcels_current_count + 1) + ", " +
                " parcels_total_count =" + (parcels_total_count + 1) + ", " +
                " updated_at ='" + updated_at + "', " +
                " sharing_status =10 WHERE uid ='" + owner_id + "'";
        db.execSQL(sql);
    }


    public void register_event(
            SQLiteDatabase db,
            String created_at,
            String parcel_uid,
            String ryosei_id,
            String room_name,
            String ryosei_name,
            String note
    ) {
        StringBuilder sb_insert_Parcel = new StringBuilder();

        String uuid = UUID.randomUUID().toString();
        sb_insert_Parcel.append("insert into parcel_event (" +
                "uid," +
                "created_at," +
                "event_type," +
                "parcel_uid," +
                "ryosei_uid," +
                "room_name," +
                "ryosei_name," +
                "note," +
                "sharing_status" +
                ") values (");

        sb_insert_Parcel.append("'" + uuid + "',");
        sb_insert_Parcel.append("'" + created_at + "',");
        sb_insert_Parcel.append(1 + ",");
        sb_insert_Parcel.append("'" + parcel_uid + "',");
        sb_insert_Parcel.append("'" + ryosei_id + "',");
        sb_insert_Parcel.append("'" + room_name + "',");
        sb_insert_Parcel.append("'" + ryosei_name + "',");
        sb_insert_Parcel.append("'" + note + "',");
        sb_insert_Parcel.append(" 10);");

        String sql_insert_event = sb_insert_Parcel.toString();
        db.execSQL(sql_insert_event);

    }


    //受取の関数ポータル。ここからデータベース3つを更新する。agent_uidは、代理受取でない場合は""（空白文字）が来る）
    public void release(
            SQLiteDatabase db,
            String owner_id,
            String parcels_uid,
            String release_staff_uid,
            String release_staff_room_name,
            String release_staff_ryosei_name,
            String agent_uid) {

        //owner_idの寮生を取得
        String sql = "SELECT room_name,ryosei_name, parcels_current_count FROM ryosei  WHERE uid = '" + owner_id + "'";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();
        String owner_room = cursor.getString(cursor.getColumnIndex("room_name"));
        String owner_ryosei_name = cursor.getString(cursor.getColumnIndex("ryosei_name"));
        int parcels_current_count = cursor.getInt(cursor.getColumnIndex("parcels_current_count"));

        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 日時情報を指定フォーマットの文字列で取得
        String release_time = format.format(dateObj);

        release_parcels(db, parcels_uid, release_staff_uid, release_staff_room_name, release_staff_ryosei_name, release_time, agent_uid);
        release_ryosei(db, owner_id, parcels_current_count, release_time);
        release_event(db, release_time, parcels_uid, owner_id, owner_room, owner_ryosei_name);
    }

    public void release_parcels(
            SQLiteDatabase db,
            String parcels_uid,
            String release_staff_uid,
            String release_staff_room_name,
            String release_staff_ryosei_name,
            String release_time,
            String agent_uid) {
        String sql = "UPDATE parcels SET " +
                " release_staff_uid = '" + release_staff_uid + "'," +
                " release_staff_room_name = '" + release_staff_room_name + "' ," +
                " release_staff_ryosei_name = '" + release_staff_ryosei_name + "' ," +
                " is_released = 1," +
                " release_datetime =" + "'" + release_time + "',";
        if (agent_uid != "") sql += "release_agent_uid='" + agent_uid + "',";
        sql += "sharing_status = 10 WHERE uid = '" + parcels_uid + "';";
        db.execSQL(sql);
    }

    public void release_ryosei(SQLiteDatabase db, String owner_id, int parcels_current_count, String updated_at) {
        String sql = "UPDATE ryosei SET " +
                "parcels_current_count =" + (parcels_current_count - 1) + ", " +
                "updated_at = '" + updated_at + "', " +
                "sharing_status =10  " +
                "WHERE uid ='" + owner_id + "'";
        db.execSQL(sql);
    }

    public void release_event(
            SQLiteDatabase db,
            String created_at,
            String parcel_id,
            String ryosei_id,
            String room_name,

            String ryosei_name) {
        //単にinsertするだけではなく、target_event_uidに入るregisterのeventのuidを取得し、
        //さらに取得したregisterのレコードのis_finishedを1にupdateする必要がある

        String sql = "select uid from parcel_event where is_deleted=0 and event_type = 1 and parcel_uid = '" + parcel_id + "'";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        String target_event_uid = cursor.getString(cursor.getColumnIndex("uid"));


        String uuid = UUID.randomUUID().toString();
        StringBuilder sb_insert_Parcel = new StringBuilder();
        sb_insert_Parcel.append("insert into parcel_event (" +
                "uid," +
                "created_at," +
                "event_type," +
                "parcel_uid," +
                "ryosei_uid," +
                "room_name," +
                "ryosei_name," +
                "target_event_uid," +
                "sharing_status" +
                ") values (");
        sb_insert_Parcel.append("'" + uuid + "',");
        sb_insert_Parcel.append("'" + created_at + "',");
        sb_insert_Parcel.append("2,");
        sb_insert_Parcel.append("'" + parcel_id + "',");
        sb_insert_Parcel.append("'" + ryosei_id + "',");
        sb_insert_Parcel.append("'" + room_name + "',");
        sb_insert_Parcel.append("'" + ryosei_name + "',");
        sb_insert_Parcel.append("'" + target_event_uid + "',");
        sb_insert_Parcel.append("10)");
        sql = sb_insert_Parcel.toString();
        db.execSQL(sql);

        sql = "update parcel_event set is_finished=1,sharing_status=10 where uid = '" + target_event_uid + "'";
        db.execSQL(sql);

    }


    public List<Map<String, String>> nimotsuCountOfRyosei(SQLiteDatabase db, String owner_id) {
        //荷物IDとラベル(日時、受け取り事務当、場所）を返す。
        List<Map<String, String>> show_owners_parcels = new ArrayList<>();
        String sql = "SELECT uid, placement, register_datetime," +
                "register_staff_room_name, register_staff_ryosei_name,note, lost_datetime " +
                "FROM parcels WHERE is_released = 0 AND owner_uid ='" + owner_id + "' and is_deleted=0";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Map<String, String> parcels_raw = new HashMap<>();
            int index_note = cursor.getColumnIndex("note");
            String parcels_id = cursor.getString(cursor.getColumnIndex("uid"));
            String lost_datetime = cursor.getString(cursor.getColumnIndex("lost_datetime"));
            String register_datetime = cursor.getString(cursor.getColumnIndex("register_datetime"));
            String register_staff_room = cursor.getString(cursor.getColumnIndex("register_staff_room_name"));
            String register_staff_name = cursor.getString(cursor.getColumnIndex("register_staff_ryosei_name"));

            String placement = "";
            String parcels_attribute = "";
            switch (cursor.getInt(cursor.getColumnIndex("placement"))) {
                case 0:
                    placement += "普通";
                    parcels_attribute = "荷物";
                    break;
                case 1:
                    placement += "冷蔵";
                    parcels_attribute = "冷蔵";
                    break;
                case 2:
                    placement += "冷凍";
                    parcels_attribute = "冷凍";
                    break;
                case 3:
                    placement += "大型";
                    parcels_attribute = "大型";
                    break;
                case 4:
                    placement += "不在票";
                    parcels_attribute = "不在票";
                    break;
                case 5:
                    placement += "その他（memo:" + cursor.getString(index_note) + ")";
                    parcels_attribute = "その他";
                    break;
            }
            String rabel = "";

            rabel += register_datetime.substring(5, 16).replace('-', '/') + "登録   ";
            rabel += "種類：" + placement;
            rabel += "\r\n ";
            rabel += "     　　　　　　     (受取事務当：" + register_staff_room + " " + register_staff_name + ")";


            parcels_raw.put("rabel", rabel);
            String look = parcels_id;
            parcels_raw.put("parcels_id", parcels_id);
            parcels_raw.put("attribute", parcels_attribute);
            parcels_raw.put("lost_datetime", lost_datetime);
            show_owners_parcels.add(parcels_raw);
        }
        return show_owners_parcels;
    }

    public List<Map<String, String>> nightdutylist(SQLiteDatabase db) {
        //荷物IDとラベル(日時、受け取り事務当、場所）を返す。
        List<Map<String, String>> show_owners_parcels = new ArrayList<>();
        String sql = "SELECT uid, placement, register_datetime,lost_datetime," +
                "register_staff_room_name, register_staff_ryosei_name,owner_room_name,owner_ryosei_name " +
                "FROM parcels WHERE is_released = 0 and is_deleted=0  order by owner_room_name asc,owner_ryosei_name asc";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Map<String, String> parcels_raw = new HashMap<>();
            int index_id = cursor.getColumnIndex("uid");
            int index_placement = cursor.getColumnIndex("placement");
            int index_lost_datetime = cursor.getColumnIndex("lost_datetime");
            int index_owner_room_name = cursor.getColumnIndex("owner_room_name");
            int index_register_staff_ryosei_name = cursor.getColumnIndex("owner_ryosei_name");
            String rabel = "";
            String parcels_id = "";
            parcels_id = cursor.getString(index_id);
            rabel += "持ち主　" + cursor.getString(index_owner_room_name);
            rabel += " ";
            rabel += cursor.getString(index_register_staff_ryosei_name);
            rabel += " ";
            switch (cursor.getInt(index_placement)) {
                case 0:
                    rabel += "普通";
                    break;
                case 1:
                    rabel += "冷蔵";
                    break;
                case 2:
                    rabel += "冷凍";
                    break;
                case 3:
                    rabel += "大型";
                    break;
                case 4:
                    rabel += "不在票";
                    break;
                case 5:
                    rabel += "その他";
                    break;
            }
            rabel += " ";
            rabel += "確認日　";
            String date = cursor.getString(index_lost_datetime);
            if (date == null) {
                date = "未チェック";
            }
            rabel += date;
            parcels_raw.put("rabel", rabel);
            parcels_raw.put("parcels_id", parcels_id);
            show_owners_parcels.add(parcels_raw);
        }
        return show_owners_parcels;
    }


    public void night_check_updater(SQLiteDatabase db, String parcels_uid) {
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String string_register_time = format.format(dateObj);
        String sql = "UPDATE parcels SET " +
                " lost_datetime =" + "'" + string_register_time + "'" + ", sharing_status =10 WHERE uid ='" + parcels_uid + "'";
        db.execSQL(sql);
    }

    public void event_add_night_duty(SQLiteDatabase db, String staffid, String staffroom, String staffname) {

    }


    public void delete_event(SQLiteDatabase db, String event_id, String ryosei_id, String parcel_id, String jimuto_id, String event_type) {
        //event idは1 or 2が入る　1が登録のイベントを消し込むとき、2が受取のイベントを消し込むとき

        //room_name, ryosei_name, total_parcels_count, current_parcels_countを取得する
        String sql = "SELECT room_name, ryosei_name, parcels_total_count, parcels_current_count  FROM ryosei where uid='" + ryosei_id + "'";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String room_name = cursor.getString(cursor.getColumnIndex("room_name"));
        String ryosei_name = cursor.getString(cursor.getColumnIndex("ryosei_name"));
        int parcels_total_count = cursor.getInt(cursor.getColumnIndex("parcels_total_count"));
        int parcels_current_count = cursor.getInt(cursor.getColumnIndex("parcels_current_count"));
        if (event_type.equals("1")) {//受取の削除
            parcels_total_count--;
            parcels_current_count--;
        } else {//引渡の削除
            parcels_current_count++;
        }

        //created_atを取得する
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 日時情報を指定フォーマットの文字列で取得
        String created_at = format.format(dateObj);


        //event tableのupdate、insert
        //ryosei tableのupdate（荷物数カウント）
        //parcelstableのupdate（論理削除）
        //が必要

        String new_uid = UUID.randomUUID().toString();
        //event
        //①削除するレコードのis_deletedをupdate
        sql = "update parcel_event set is_deleted=1, sharing_status=10 where uid='" + event_id + "'";
        db.execSQL(sql);
        //②削除というイベント自体のレコードをinsert
        sql = "insert into parcel_event(uid,created_at,event_type,parcel_uid,ryosei_uid,room_name,ryosei_name,target_event_uid,is_finished,sharing_status)";
        sql += "values('";
        sql += new_uid + "','" + created_at + "',3,'" + parcel_id + "','" + ryosei_id + "',";
        sql += "'" + room_name + "',";
        sql += "'" + ryosei_name + "','";
        sql += event_id + "',1,10);";
        db.execSQL(sql);
        //③引渡の削除の場合、is_after_fixed_timeが0ならば、受取のis_finishedを0に戻す
        if (event_type.equals("2")) {
            sql = "select uid, is_after_fixed_time from parcel_event where is_deleted=0 and event_type = 1 and parcel_uid = '" + parcel_id + "'";
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex("is_after_fixed_time")) == 0) {

                String target_event_uid = cursor.getString(cursor.getColumnIndex("uid"));
                sql = "update parcel_event set is_finished=0, sharing_status=10 where uid='" + target_event_uid + "'";
                db.execSQL(sql);
            }
        }
        //ryosei
        sql = "update ryosei set parcels_total_count=" +
                parcels_total_count +
                ",parcels_current_count=" +
                parcels_current_count +
                ",sharing_status= 10 where uid='" + ryosei_id + "'";
        db.execSQL(sql);

        //parcels
        //①受取の削除の場合、is_deleted=1にする
        //②引渡の削除の場合、is_released=0, release_agent_uid,release_datetime, release_staff_uidをnullにする
        //      release_staff_room_nameとrelease_staff_ryosei_nameはnullにせずに残しておく。一回受取されたことがそこからも分かるかもしれない(sql分を短くしたい)
        if (event_type.equals("1")) {
            sql = "update parcels set is_deleted=1, sharing_status=10 where uid ='" + parcel_id + "'";
            db.execSQL(sql);

        } else {
            sql = "update parcels set is_released=0,release_agent_uid=null,release_datetime=null, release_staff_uid=null, sharing_status=10 where uid ='" + parcel_id + "'";
            db.execSQL(sql);
        }
    }

    public ArrayList<String> select_for_sync(SQLiteDatabase db, String table, int length) {
        Cursor cursor = db.rawQuery("SELECT uid FROM " + table + " where sharing_status<30 order by uid Limit " + length, null);
        ArrayList<String> uids = new ArrayList<String>();
        while (cursor.moveToNext()) {
            uids.add(cursor.getString(cursor.getColumnIndex("uid")));
        }
        return uids;
    }

    public String select_for_json(SQLiteDatabase db, String table, ArrayList<String> uids) {
        Cursor cursor;
        String sql;
        String json_str = "[\n";
        for (String uid : uids) {
            sql = "SELECT *  FROM " + table + " where uid='" + uid + "';";
            cursor = db.rawQuery(sql, null);
            cursor.moveToNext();
            json_str += "{\n";
            if (table == "ryosei") {
                for (enum_ryosei column : enum_ryosei.values()) {
                    String col = column.toString();
                    String val = cursor.getString(cursor.getColumnIndex(col));
                    json_str += "\"";
                    json_str += col;
                    json_str += "\": ";
                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += val;
                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += ",\n";
                }
            } else if (table == "parcels") {
                for (enum_parcels column : enum_parcels.values()) {
                    String col = column.toString();
                    String val = cursor.getString(cursor.getColumnIndex(col));
                    json_str += "\"";
                    json_str += col;
                    json_str += "\": ";

                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += val;
                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += ",\n";
                }
            } else if (table == "parcel_event") {
                for (enum_event column : enum_event.values()) {
                    String col = column.toString();
                    String val = cursor.getString(cursor.getColumnIndex(col));
                    json_str += "\"";
                    json_str += col;
                    json_str += "\": ";
                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += val;
                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += ",\n";
                }
            }
            //末尾からカンマを削除
            json_str = json_str.substring(0, json_str.length() - 2);
            json_str += "\n";
            json_str += "},\n";
        }
        if (json_str.length() > 3) {
            json_str = json_str.substring(0, json_str.length() - 2);
        }
        json_str += "\n";
        json_str += "]";
        return json_str;
    }

    public String select_show_json(SQLiteDatabase db, int type, String table) {
        //owner_idの寮生を取得
        String sql;
        //owner_idの寮生を取得
        if (type == 1) {
            sql = "SELECT *  FROM " + table + " order by uid Limit 50";
        } else if (type == 10) {
            sql = "SELECT *  FROM " + table + " where sharing_status = 10 order by uid Limit 5";
        } else if (type == 11) {
            sql = "SELECT *  FROM " + table + " where sharing_status = 11 order by uid Limit 5";
        } else {
            sql = "SELECT *  FROM " + table + " order by uid Limit 10";
        }
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        //if (cursor.getCount() == 0) return "";


        String json_str = "[\n";

        while (cursor.moveToNext()) {
            json_str += "{\n";
            if (table == "ryosei") {
                for (enum_ryosei column : enum_ryosei.values()) {
                    String col = column.toString();
                    String val = cursor.getString(cursor.getColumnIndex(col));
                    json_str += "\"";
                    json_str += col;
                    json_str += "\": ";
                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += val;
                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += ",\n";
                }
            } else if (table == "parcels") {
                for (enum_parcels column : enum_parcels.values()) {
                    String col = column.toString();
                    String val = cursor.getString(cursor.getColumnIndex(col));
                    json_str += "\"";
                    json_str += col;
                    json_str += "\": ";

                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += val;
                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += ",\n";
                }
            } else if (table == "parcel_event") {
                for (enum_event column : enum_event.values()) {
                    String col = column.toString();
                    String val = cursor.getString(cursor.getColumnIndex(col));
                    json_str += "\"";
                    json_str += col;
                    json_str += "\": ";
                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += val;
                    if (column.getCode() == 1 && val != null) json_str += "\"";
                    json_str += ",\n";
                }
            }
            //末尾からカンマを削除
            json_str = json_str.substring(0, json_str.length() - 2);
            json_str += "\n";
            json_str += "},\n";

        }
        if (json_str.length() > 3) {
            json_str = json_str.substring(0, json_str.length() - 2);
        }
        json_str += "\n";
        json_str += "]";
        return json_str;
    }

    public void update_sharing_status_for_success(SQLiteDatabase db, String table, ArrayList<String> uids) {
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = format.format(dateObj);
        String sql;
        for (String uid : uids) {
            sql = "update " + table + " set sharing_status = 30, sharing_time='" + now + "' where uid = '" + uid + "';";
            db.execSQL(sql);
        }
    }

    public String jimuto_at_oncreate(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select room_name,ryosei_name from parcel_event where event_type=10 order by created_at desc limit 1", null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) return "登録してください";
        return cursor.getString(cursor.getColumnIndex("room_name")) + " " + cursor.getString(cursor.getColumnIndex("ryosei_name"));
    }
    public String jimuto_id_at_oncreate(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select ryosei_uid from parcel_event where event_type=10 order by created_at desc limit 1", null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) return null;
        return cursor.getString(cursor.getColumnIndex("ryosei_uid"));
    }


    public String returnStatus(SQLiteDatabase db, String table, String uid) {
        Cursor cursor = db.rawQuery("SELECT sharing_status FROM " + table + " WHERE uid = " + "'" + uid + "'", null);
        String result_sharing_status = null;

        try {
            while (cursor.moveToNext()) {
                // カラムのインデックス値を元に実際のデータを取得。
                result_sharing_status = cursor.getString(cursor.getColumnIndex("sharing_status"));
            }
        } finally {
            cursor.close();
        }

        return result_sharing_status;
    }
/*
    public void updateSharingStatus(SQLiteDatabase db, String method,String table) {
        String sql = "update "+table+" set sharing_status = '30' where sharing_status = ";
        switch (method) {
            case "create":
                sql = sql + "'" + 10 + "'";
                break;
            case "update":
                sql = sql + "'" + 11 + "'";
        }
        db.execSQL(sql);
    }

 */
/*
    public void updateSharingStatusFromPC(SQLiteDatabase db, String method,String table) {
        String sql = "update "+table+" set sharing_status = '30' where sharing_status = ";
        switch (method) {
            case "create":
                sql = sql + "'" + 20 + "'";
                break;
            case "update":
                sql = sql + "'" + 21 + "'";
        }
        db.execSQL(sql);
    }

 */

    public void jimuto_change_event(SQLiteDatabase db, String ryosei_id) {
        //先にryosei_idから部屋番号と氏名を取得
        Cursor cursor = db.rawQuery("SELECT room_name,ryosei_name FROM ryosei where uid='" + ryosei_id + "';", null);
        String room_name;
        String ryosei_name;
        try {
            cursor.moveToNext();
            // カラムのインデックス値を取得。
            // カラムのインデックス値を元に実際のデータを取得。
            room_name = cursor.getString(cursor.getColumnIndex("room_name"));
            ryosei_name = cursor.getString(cursor.getColumnIndex("ryosei_name"));


            //
            // 現在日時情報で初期化されたインスタンスの生成
            Date dateObj = new Date();
            String event_id = UUID.randomUUID().toString();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String created_at = format.format(dateObj);
            String sql = "insert into parcel_event(uid,created_at,event_type,ryosei_uid,room_name,ryosei_name,target_event_uid,is_finished,sharing_status)";
            sql += "values('";
            sql += event_id + "','" + created_at + "',10,'" + ryosei_id + "',";
            sql += "'" + room_name + "',";
            sql += "'" + ryosei_name + "','";
            sql += event_id + "',1,10);";
            db.execSQL(sql);
        } finally {
            cursor.close();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}



