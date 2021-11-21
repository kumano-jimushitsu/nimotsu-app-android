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
        sb_parcels.append(" uid TEXT PRIMARY KEY,");
        sb_parcels.append(" owner_uid TEXT,");
        sb_parcels.append(" owner_room_name TEXT,");
        sb_parcels.append(" owner_parcels_name TEXT,");
        sb_parcels.append(" register_datetime TEXT,");
        sb_parcels.append(" register_staff_uid TEXT,");
        sb_parcels.append(" register_staff_room_name TEXT,");
        sb_parcels.append(" register_staff_parcels_name TEXT,");
        sb_parcels.append(" placement INTEGER default 0,");
        sb_parcels.append(" fragile INTEGER default 0,");
        sb_parcels.append(" is_released INTEGER DEFAULT 0,");
        sb_parcels.append(" release_agent_uid TEXT,");
        sb_parcels.append(" release_datetime TEXT,");
        sb_parcels.append(" release_staff_uid TEXT,");
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
        sb_parcels.append(" sharing_status TEXT");
        sb_parcels.append(");");
        String sql_parcels = sb_parcels.toString();
        db.execSQL(sql_parcels);

        //ryoseiテーブルの登録
        StringBuilder sb_ryosei = new StringBuilder();
        sb_ryosei.append("CREATE TABLE ryosei(");
        sb_ryosei.append(" uid TEXT PRIMARY KEY,");//
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
        sb_ryosei.append(" sharing_status TEXT");
        sb_ryosei.append("); ");
        String sql_ryosei = sb_ryosei.toString();
        db.execSQL(sql_ryosei);

        //eventテーブルの登録
        StringBuilder sb_parcel_event = new StringBuilder();
        sb_parcel_event.append("CREATE TABLE parcel_event(");
        sb_parcel_event.append(" uid TEXT PRIMARY KEY,");
        sb_parcel_event.append(" created_at TEXT,");
        sb_parcel_event.append(" event_type INTEGER,");
        sb_parcel_event.append(" parcel_uid TEXT,");
        sb_parcel_event.append(" ryosei_uid TEXT,");
        sb_parcel_event.append(" room_name TEXT,");
        sb_parcel_event.append(" ryosei_name TEXT,");
        sb_parcel_event.append("target_event_uid TEXT,");
        sb_parcel_event.append(" note TEXT,");
        sb_parcel_event.append(" is_finished INTEGER DEFAULT 0,");
        sb_parcel_event.append(" is_deleted INTEGER DEFAULT 0,");
        sb_parcel_event.append(" sharing_status TEXT");
        sb_parcel_event.append(");");
        String sql_parcel_event = sb_parcel_event.toString();
        db.execSQL(sql_parcel_event);

        insert_test_ryosei(db);


    }
    void insert_test_ryosei(SQLiteDatabase db){
        StringBuilder sb_insert_test_ryosei = new StringBuilder();
        sb_insert_test_ryosei.append("insert into ryosei (uid, ryosei_name,block_id,room_name,sharing_status) values ");
        sb_insert_test_ryosei.append("('");
        sb_insert_test_ryosei.append(UUID.randomUUID().toString());
        sb_insert_test_ryosei.append("','松元優香 ',1,'A100','10'),");
        sb_insert_test_ryosei.append("('");
        sb_insert_test_ryosei.append(UUID.randomUUID().toString());
        sb_insert_test_ryosei.append("','藤谷秀加	',1,'A101','10'),");
        sb_insert_test_ryosei.append("('");
        sb_insert_test_ryosei.append(UUID.randomUUID().toString());
        sb_insert_test_ryosei.append("','三好宏美	',1,'A101','10'),");
        sb_insert_test_ryosei.append("('");
        sb_insert_test_ryosei.append(UUID.randomUUID().toString());
        sb_insert_test_ryosei.append("','長瀬菜子	',1,'A101','10'),");
        sb_insert_test_ryosei.append("('");
        sb_insert_test_ryosei.append(UUID.randomUUID().toString());
        sb_insert_test_ryosei.append("','大嶋代子	',1,'A101','10');");
        /*
        sb_insert_test_ryosei.append("('日比真紗　',1,'A102'),");
        sb_insert_test_ryosei.append("('松川鼓斗	',1,'A102'),");
        sb_insert_test_ryosei.append("('川添桜臥	',1,'A102'),");
        sb_insert_test_ryosei.append("('小俣千穂	',1,'A102'),");
        sb_insert_test_ryosei.append("('布施春	',1,'A103'),");
        sb_insert_test_ryosei.append("('島村翼	',1,'A103'),");
        sb_insert_test_ryosei.append("('麻生歌子	',1,'A103'),");
        sb_insert_test_ryosei.append("('今勇三	',1,'A103'),");
        sb_insert_test_ryosei.append("('木本和花	',1,'A104'),");
        sb_insert_test_ryosei.append("('辻泰雄	',1,'A104'),");
        sb_insert_test_ryosei.append("('小西安雄	',1,'A104'),");
        sb_insert_test_ryosei.append("('下村福郎	',1,'A104'),");
        sb_insert_test_ryosei.append("('矢沢輝心	',1,'A105'),");
        sb_insert_test_ryosei.append("('米田一輝	',1,'A105'),");
        sb_insert_test_ryosei.append("('遠山直樹	',1,'A105'),");
        sb_insert_test_ryosei.append("('豊島美和	',1,'A105'),");
        sb_insert_test_ryosei.append("('佐木茉莉	',1,'A106'),");
        sb_insert_test_ryosei.append("('池上遙	',1,'A106'),");
        sb_insert_test_ryosei.append("('日野杏理	',1,'A106'),");
        sb_insert_test_ryosei.append("('西山清治	',1,'A106'),");
        sb_insert_test_ryosei.append("('小島恵子	',1,'A107'),");
        sb_insert_test_ryosei.append("('木本宙子	',1,'A107'),");
        sb_insert_test_ryosei.append("('福沢和冴	',1,'A107'),");
        sb_insert_test_ryosei.append("('植田義雄	',1,'A107'),");
        sb_insert_test_ryosei.append("('平沢花	',1,'A108'),");
        sb_insert_test_ryosei.append("('小村春吉	',1,'A108'),");
        sb_insert_test_ryosei.append("('田畑雅樹	',1,'A108'),");
        sb_insert_test_ryosei.append("('西田健次郎',1,'A108'),");
        sb_insert_test_ryosei.append("('梶原孝子	',1,'A109'),");
        sb_insert_test_ryosei.append("('棚橋忠彦	',1,'A109'),");
        sb_insert_test_ryosei.append("('松田一久	',1,'A109'),");
        sb_insert_test_ryosei.append("('渡辺元	',1,'A109'),");
        sb_insert_test_ryosei.append("('稲垣彩楓	',1,'A110'),");
        sb_insert_test_ryosei.append("('小沼文治	',1,'A110'),");
        sb_insert_test_ryosei.append("('岩渕達志	',1,'A110'),");
        sb_insert_test_ryosei.append("('榊原聖都	',1,'A110'),");
        sb_insert_test_ryosei.append("('熊沢奈子	',1,'A111'),");
        sb_insert_test_ryosei.append("('原口好夫	',1,'A111'),");
        sb_insert_test_ryosei.append("('小寺栄蔵	',1,'A111'),");
        sb_insert_test_ryosei.append("('塩崎順仁	',1,'A111'),");
        sb_insert_test_ryosei.append("('海老正子	',2,'A201'),");
        sb_insert_test_ryosei.append("('竹村尚司	',2,'A201'),");
        sb_insert_test_ryosei.append("('山形小菜	',2,'A201'),");
        sb_insert_test_ryosei.append("('西口卓雄	',2,'A201'),");
        sb_insert_test_ryosei.append("('柏木咲月	',2,'A202'),");
        sb_insert_test_ryosei.append("('関口颯	',2,'A202'),");
        sb_insert_test_ryosei.append("('日下真吉	',2,'A202'),");
        sb_insert_test_ryosei.append("('岩瀬沙花	',2,'A202'),");
        sb_insert_test_ryosei.append("('宍戸遥佳	',2,'A203'),");
        sb_insert_test_ryosei.append("('大高真悠	',2,'A203'),");
        sb_insert_test_ryosei.append("('百瀬利勝	',2,'A203'),");
        sb_insert_test_ryosei.append("('寺沢夕弦	',2,'A203'),");
        sb_insert_test_ryosei.append("('森井敏昭	',2,'A204'),");
        sb_insert_test_ryosei.append("('佐伯博司	',2,'A204'),");
        sb_insert_test_ryosei.append("('小平光明	',2,'A204'),");
        sb_insert_test_ryosei.append("('石倉一義	',2,'A204'),");
        sb_insert_test_ryosei.append("('首藤美愛	',2,'A205'),");
        sb_insert_test_ryosei.append("('杉田光彦	',2,'A205'),");
        sb_insert_test_ryosei.append("('横溝美玖	',2,'A205'),");
        sb_insert_test_ryosei.append("('村井正元	',2,'A205'),");
        sb_insert_test_ryosei.append("('三輪恵理	',2,'A206'),");
        sb_insert_test_ryosei.append("('川島明仁	',2,'A206'),");
        sb_insert_test_ryosei.append("('中井結加	',2,'A206'),");
        sb_insert_test_ryosei.append("('町田冨子	',2,'A206'),");
        sb_insert_test_ryosei.append("('沼田紫苑	',2,'A207'),");
        sb_insert_test_ryosei.append("('平川翔子	',2,'A207'),");
        sb_insert_test_ryosei.append("('平林睦美	',2,'A207'),");
        sb_insert_test_ryosei.append("('杉原真悠	',2,'A207'),");
        sb_insert_test_ryosei.append("('長岡雪乃	',2,'A208'),");
        sb_insert_test_ryosei.append("('秋葉哲二	',2,'A208'),");
        sb_insert_test_ryosei.append("('河上幸也	',2,'A208'),");
        sb_insert_test_ryosei.append("('富永寧音	',2,'A208'),");
        sb_insert_test_ryosei.append("('磯崎祐希	',2,'A209'),");
        sb_insert_test_ryosei.append("('名取佑奈	',2,'A209'),");
        sb_insert_test_ryosei.append("('川合英紀	',2,'A209'),");
        sb_insert_test_ryosei.append("('益田次男	',2,'A209'),");
        sb_insert_test_ryosei.append("('江川正敏	',2,'A210'),");
        sb_insert_test_ryosei.append("('大谷郁代	',2,'A210'),");
        sb_insert_test_ryosei.append("('浅沼瑠奈	',2,'A210'),");
        sb_insert_test_ryosei.append("('春日百合	',2,'A210'),");
        sb_insert_test_ryosei.append("('嶋田秀吉	',2,'A211'),");
        sb_insert_test_ryosei.append("('藤岡正光	',2,'A211'),");
        sb_insert_test_ryosei.append("('唐沢守友	',2,'A211'),");
        sb_insert_test_ryosei.append("('南田妃奈	',2,'A211'),");
        sb_insert_test_ryosei.append("('江成彦郎	',3,'A301'),");
        sb_insert_test_ryosei.append("('長岡克洋	',3,'A301'),");
        sb_insert_test_ryosei.append("('榊原一郎	',3,'A301'),");
        sb_insert_test_ryosei.append("('向後拓真	',3,'A301'),");
        sb_insert_test_ryosei.append("('鈴木哲史	',3,'A302'),");
        sb_insert_test_ryosei.append("('新川華蓮	',3,'A302'),");
        sb_insert_test_ryosei.append("('宮原恋雪	',3,'A302'),");
        sb_insert_test_ryosei.append("('福永航	',3,'A302'),");
        sb_insert_test_ryosei.append("('小久保子	',3,'A303'),");
        sb_insert_test_ryosei.append("('佐久間美	',3,'A303'),");
        sb_insert_test_ryosei.append("('久野託望	',3,'A303'),");
        sb_insert_test_ryosei.append("('大川茂男	',3,'A303'),");
        sb_insert_test_ryosei.append("('益田春佳	',3,'A304'),");
        sb_insert_test_ryosei.append("('寺崎俊二	',3,'A304'),");
        sb_insert_test_ryosei.append("('谷金弥	',3,'A304'),");
        sb_insert_test_ryosei.append("('三橋直行	',3,'A304'),");
        sb_insert_test_ryosei.append("('小嶋盛夫	',3,'A305'),");
        sb_insert_test_ryosei.append("('平本椿	',3,'A305'),");
        sb_insert_test_ryosei.append("('岡野武治	',3,'A305'),");
        sb_insert_test_ryosei.append("('神崎彩香	',3,'A305'),");
        sb_insert_test_ryosei.append("('岡田亮	',3,'A306'),");
        sb_insert_test_ryosei.append("('西野友彦	',3,'A306'),");
        sb_insert_test_ryosei.append("('笠原胡桃	',3,'A306'),");
        sb_insert_test_ryosei.append("('日部珠希	',3,'A306'),");
        sb_insert_test_ryosei.append("('神崎竜太	',3,'A307'),");
        sb_insert_test_ryosei.append("('内村理歩	',3,'A307'),");
        sb_insert_test_ryosei.append("('金城則夫	',3,'A307'),");
        sb_insert_test_ryosei.append("('丸山柚衣	',3,'A307'),");
        sb_insert_test_ryosei.append("('杉山瑠花	',3,'A308'),");
        sb_insert_test_ryosei.append("('西口敏子	',3,'A308'),");
        sb_insert_test_ryosei.append("('土田日和	',3,'A308'),");
        sb_insert_test_ryosei.append("('早川詩	',3,'A308'),");
        sb_insert_test_ryosei.append("('池内金造	',3,'A309'),");
        sb_insert_test_ryosei.append("('野原正弘	',3,'A309'),");
        sb_insert_test_ryosei.append("('矢沢柑奈	',3,'A309'),");
        sb_insert_test_ryosei.append("('久保沙香	',3,'A309'),");
        sb_insert_test_ryosei.append("('小久保子	',3,'A310'),");
        sb_insert_test_ryosei.append("('大淵和弥	',3,'A310'),");
        sb_insert_test_ryosei.append("('白石紗花	',3,'A310'),");
        sb_insert_test_ryosei.append("('阪本茂男	',3,'A310'),");
        sb_insert_test_ryosei.append("('藤野真菜	',3,'A311'),");
        sb_insert_test_ryosei.append("('会田茉凛	',3,'A311'),");
        sb_insert_test_ryosei.append("('青山佳子	',3,'A311'),");
        sb_insert_test_ryosei.append("('岡田志歩	',3,'A311'),");
        sb_insert_test_ryosei.append("('山下好	',4,'A401'),");
        sb_insert_test_ryosei.append("('香坂留美	',4,'A401'),");
        sb_insert_test_ryosei.append("('植田嘉一	',4,'A401'),");
        sb_insert_test_ryosei.append("('小沼悠	',4,'A401'),");
        sb_insert_test_ryosei.append("('赤羽美音	',4,'A402'),");
        sb_insert_test_ryosei.append("('村松結芽	',4,'A402'),");
        sb_insert_test_ryosei.append("('伊藤琉奈	',4,'A402'),");
        sb_insert_test_ryosei.append("('日野豊明	',4,'A402'),");
        sb_insert_test_ryosei.append("('入江平吉	',4,'A403'),");
        sb_insert_test_ryosei.append("('神保由姫	',4,'A403'),");
        sb_insert_test_ryosei.append("('竹下伸	',4,'A403'),");
        sb_insert_test_ryosei.append("('大友敏子	',4,'A403'),");
        sb_insert_test_ryosei.append("('上柿大	',10,'図書室'),");
        sb_insert_test_ryosei.append("('花岡洋二	',10,'図書室'),");
        sb_insert_test_ryosei.append("('神山柚希	',10,'図書室'),");
        sb_insert_test_ryosei.append("('大平伸子	',10,'図書室'),");
        sb_insert_test_ryosei.append("('矢島恵理	',10,'図書室'),");
        sb_insert_test_ryosei.append("('宮地三男	',10,'図書室'),");
        sb_insert_test_ryosei.append("('永瀬実緒	',10,'旧印刷室'),");
        sb_insert_test_ryosei.append("('若林千代	',10,'旧印刷室'),");
        sb_insert_test_ryosei.append("('亀山英二	',10,'旧印刷室'),");
        sb_insert_test_ryosei.append("('坂本年子	',10,'旧印刷室'),");
        sb_insert_test_ryosei.append("('井手歩実	',10,'旧会議室'),");
        sb_insert_test_ryosei.append("('池谷京子	',10,'旧会議室'),");
        sb_insert_test_ryosei.append("('小村健蔵	',10,'旧会議室'),");
        sb_insert_test_ryosei.append("('安部太郎	',10,'旧会議室'),");
        sb_insert_test_ryosei.append("('古市佐子	',10,'旧会議室');");
        */
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
                "uid,owner_uid,owner_room_name,owner_parcels_name," +
                "register_datetime," +
                "register_staff_uid,register_staff_room_name,register_staff_parcels_name,placement,sharing_status" +
                ") values ('");
        sb_insert_Parcel.append( UUID.randomUUID().toString() +"','");
        sb_insert_Parcel.append( owner_uid +"',");
        sb_insert_Parcel.append( " \"" + owner_room +" \",");
        sb_insert_Parcel.append( " \"" + owner_ryosei_name +"\",");
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format( dateObj );
        sb_insert_Parcel.append( " \"" + string_register_time +"\",'");
        sb_insert_Parcel.append( register_staff_uid +"',");
        sb_insert_Parcel.append( " \"" + register_staff_room_name +"\",");
        sb_insert_Parcel.append( " \"" + register_staff_ryosei_name +"\",");
        sb_insert_Parcel.append( " \"" + placement +"\",'10')");

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
                "ryosei_name," +
                "sharing_status" +
                ") values (");

        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format( dateObj );
        sb_insert_Parcel.append( " \"" + string_register_time +"\",");
        sb_insert_Parcel.append( " \"" + 1 +" \",");
        Cursor cursor = db.rawQuery( "SELECT uid FROM parcels ORDER BY register_datetime DESC LIMIT 1",null);
        cursor.moveToFirst();
        String uid_str = String.valueOf(cursor.getInt(cursor.getColumnIndex("uid")));
        sb_insert_Parcel.append( " \"" + uid_str +" \",'");
        sb_insert_Parcel.append( ryosei_id +"',");
        sb_insert_Parcel.append( " \"" + room_name +" \",");
        sb_insert_Parcel.append( " \"" + ryosei_name +"\",'10')");
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
                "ryosei_name," +
                "sharing_status" +
                ") values (");

        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format( dateObj );
        sb_insert_Parcel.append( " \"" + string_register_time +"\",");
        sb_insert_Parcel.append( " \"" + 2 +" \",");
        Cursor cursor = db.rawQuery( "SELECT uid FROM parcels ORDER BY release_datetime DESC LIMIT 1",null);
        cursor.moveToFirst();
        String uid_str = String.valueOf(cursor.getInt(cursor.getColumnIndex("uid")));
        sb_insert_Parcel.append( " \"" + uid_str +" \",'");
        sb_insert_Parcel.append( ryosei_id +"',");
        sb_insert_Parcel.append( " \"" + room_name +" \",");
        sb_insert_Parcel.append( " \"" + ryosei_name +"\",'10')");
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
                " release_staff_uid = '" + release_staff_uid + "'," +
                " release_staff_room_name = '" + release_staff_room_name + "' ," +
                " release_staff_parcels_name = '" + release_staff_ryosei_name +"' ," +
                " is_released = " + "1" +" ," +
                " release_datetime =" + " \"" + string_register_time +"\"," +
                " sharing_status =" + "'10'" +
                " WHERE uid =" + parcels_uid;
        db.execSQL(sql);
        nimotsuCountSubber(db, owner_id);
        event_add_uketori(db,owner_id,owner_room,owner_ryosei_name);

    }


    public List<Map<String,String>> nimotsuCountOfRyosei (SQLiteDatabase db, String owner_id){
        //荷物IDとラベル(日時、受け取り事務当、場所）を返す。
        List<Map<String,String>> show_owners_parcels = new ArrayList<>();
        String sql = "SELECT uid, placement, register_datetime," +
                "register_staff_room_name, register_staff_parcels_name " +
                "FROM parcels WHERE is_released = 0 AND owner_uid ='" + owner_id + "'";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Map<String, String> parcels_raw = new HashMap<>();
            int index_id = cursor.getColumnIndex("uid");
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
        String sql = "SELECT uid, placement, register_datetime,lost_datetime," +
                "register_staff_room_name, register_staff_parcels_name,owner_room_name,owner_parcels_name " +
                "FROM parcels WHERE is_released = 0 and is_deleted=0  order by owner_room_name asc";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Map<String, String> parcels_raw = new HashMap<>();
            int index_id = cursor.getColumnIndex("uid");
            int index_placement = cursor.getColumnIndex("placement");
            int index_lost_datetime = cursor.getColumnIndex("lost_datetime");
            int index_owner_room_name = cursor.getColumnIndex("owner_room_name");
            int index_register_staff_parcels_name = cursor.getColumnIndex("owner_parcels_name");
            String rabel = "";
            String parcels_id = "";
            parcels_id = cursor.getString(index_id);
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
            rabel += "確認日　"     ;
            String date = cursor.getString(index_lost_datetime);
            if(date == null){
                date = "未チェック";
            }
            rabel += date;
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
        String sql = "SELECT parcels_current_count, parcels_total_count FROM ryosei  WHERE uid = '"+ owner_id + "'";
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
                +", parcels_total_count =" + String.valueOf(parcels_total_count+1) + ", sharing_status =" + "'10'" + " WHERE uid ='" + owner_id + "'";
        db.execSQL(sql);
    }

    public void nimotsuCountSubber( SQLiteDatabase db,String owner_id){
        int parcels_current_count = 1;
        //int parcels_total_count = 0;
        //寮生に荷物カウントを追加する。

        //owner_idの寮生を取得
        String sql = "SELECT parcels_current_count, parcels_total_count FROM ryosei  WHERE uid = '"+ owner_id + "'";
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
        sql = "UPDATE ryosei SET parcels_current_count ="+ String.valueOf(parcels_current_count) + ", sharing_status =" + "'10'"
                + " WHERE uid ='" + owner_id + "'";
        db.execSQL(sql);
    }

    public void night_check_updater(SQLiteDatabase db, String parcels_uid){
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        String string_register_time = format.format( dateObj );
        String sql = "UPDATE parcels SET "+
                " lost_datetime =" + " \"" + string_register_time +"\"" + ", sharing_status =" + "'10'" +
                " WHERE uid ='" + parcels_uid + "'";
        db.execSQL(sql);
    }

    public void event_add_night_duty(SQLiteDatabase db, String staffid, String staffroom, String staffname){

    }



    public void delete_event( SQLiteDatabase db,String event_id, String ryosei_id,String parcel_id,String event_type){
        //event idは1 or 2が入る　1が登録のイベントを消し込むとき、2が受取のイベントを消し込むとき

        //room_name, ryosei_name, total_parcels_count, current_parcels_countを取得する
        String sql = "SELECT room_name, ryosei_name, parcels_total_count, parcels_current_count  FROM ryosei where uid='"+ ryosei_id + "'";
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
        sql = "update parcel_event set is_deleted=1, sharing_status=10 where uid='"+ event_id + "'";
        db.execSQL(sql);
        sql="insert into parcel_event(created_at,event_type,parcel_uid,ryosei_uid,room_name,ryosei_name,target_event_uid,is_finished,sharing_status)";
        sql+="values('";
        sql+=created_at+"',3,'"+parcel_id+"','"+ryosei_id+"',";
        sql+="'"+room_name +"',";
        sql+="'"+ryosei_name+"','";
        sql+=event_id+"',1,'10');";
        db.execSQL(sql);

        //ryosei
        sql="update ryosei set parcels_total_count="+
                    parcels_total_count+
                    ",parcels_current_count="+
                    parcels_current_count +
                ",sharing_status="+
                "10"
                +" where uid='"+ryosei_id+"'";
        db.execSQL(sql);

        //parcels
        sql="update parcels set is_deleted=1, sharing_status='10' where uid ='"+ parcel_id + "'";
        db.execSQL(sql);
    }




    public String select_ryosei_show_json(SQLiteDatabase db){
        //owner_idの寮生を取得
        String sql = "SELECT *  FROM ryosei";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.getCount()==0)return "";


        String json_str="[\n";

        while(cursor.moveToNext()) {
            json_str += "{\n";
            for(enum_ryosei column:enum_ryosei.values()){
                String col = column.toString();
                String val = cursor.getString(cursor.getColumnIndex(col));
                json_str+="\"";
                json_str+=col;
                json_str+="\": ";
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
        String sql = "SELECT *  FROM parcels order by uid";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.getCount()==0)return "";

        String json_str="[\n";

        while(cursor.moveToNext()) {
            json_str += "{\n";
            for(enum_parcels column:enum_parcels.values()){
                String col = column.toString();
                String val = cursor.getString(cursor.getColumnIndex(col));
                json_str+="\"";
                json_str+=col;
                json_str+="\": ";

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
        String sql = "SELECT *  FROM parcel_event order by uid limit 4";
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