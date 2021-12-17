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
        sb_parcels.append(" lost_datetime TEXT DEFAULT 未チェック,");
        sb_parcels.append(" is_returned INTEGER DEFAULT 0,");
        sb_parcels.append(" returned_datetime TEXT,");
        sb_parcels.append(" is_operation_error INTEGER DEFAULT 0,");
        sb_parcels.append(" operation_error_type INTEGER,");
        sb_parcels.append(" note TEXT,");
        sb_parcels.append(" is_deleted INTEGER DEFAULT 0,");
        sb_parcels.append(" sharing_status TEXT");

        //release_agent_uid nvarchar(36)
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
        sb_parcel_event.append(" target_event_uid TEXT,");
        sb_parcel_event.append(" note TEXT,");
        sb_parcel_event.append(" is_finished INTEGER DEFAULT 0,");
        sb_parcel_event.append(" is_deleted INTEGER DEFAULT 0,");
        sb_parcel_event.append(" sharing_status TEXT");
        sb_parcel_event.append(");");
        String sql_parcel_event = sb_parcel_event.toString();
        db.execSQL(sql_parcel_event);

        insert_test_ryosei(db);


    }

    void insert_test_ryosei(SQLiteDatabase db) {
        StringBuilder sb_insert_test_ryosei = new StringBuilder();
        sb_insert_test_ryosei.append("insert into ryosei (uid, ryosei_name,block_id,room_name,sharing_status) values ");
        sb_insert_test_ryosei.append("('");
        sb_insert_test_ryosei.append(UUID.randomUUID().toString());
        sb_insert_test_ryosei.append("','松元優香 ',1,'A100','10'),");
        sb_insert_test_ryosei.append("('");
        sb_insert_test_ryosei.append(UUID.randomUUID().toString());
        sb_insert_test_ryosei.append("','藤谷秀加',1,'A101','10'),");
        sb_insert_test_ryosei.append("('");
        sb_insert_test_ryosei.append(UUID.randomUUID().toString());
        sb_insert_test_ryosei.append("','三好宏美',1,'A101','10'),");
        sb_insert_test_ryosei.append("('");
        sb_insert_test_ryosei.append(UUID.randomUUID().toString());
        sb_insert_test_ryosei.append("','長瀬菜子',1,'A101','10'),");
        sb_insert_test_ryosei.append("('");
        sb_insert_test_ryosei.append(UUID.randomUUID().toString());
        sb_insert_test_ryosei.append("','大嶋代子',1,'A101','10'),");

        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','日比真紗　',1,'A102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松川鼓斗',1,'A102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','川添桜臥',1,'A102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小俣千穂',1,'A102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','布施春子',1,'A103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','島村翼子',1,'A103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','麻生歌子',1,'A103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','今勇三子',1,'A103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','木本和花',1,'A104','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'辻泰雄',1,'A104','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小西安雄',1,'A104','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','下村福郎',1,'A104','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','矢沢輝心',1,'A105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','米田一輝',1,'A105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','遠山直樹',1,'A105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','豊島美和',1,'A105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','佐木茉莉',1,'A106','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'池上遙',1,'A106','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','日野杏理',1,'A106','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西山清治',1,'A106','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小島恵子',1,'A107','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','木本宙子',1,'A107','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','福沢和冴',1,'A107','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','植田義雄',1,'A107','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'平沢花',1,'A108','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小村春吉',1,'A108','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','田畑雅樹',1,'A108','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西田次郎',1,'A108','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','梶原孝子',1,'A109','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','棚橋忠彦',1,'A109','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松田一久',1,'A109','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'渡辺元',1,'A109','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','稲垣彩楓',1,'A110','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小沼文治',1,'A110','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','岩渕達志',1,'A110','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','榊原聖都',1,'A110','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','熊沢奈子',1,'A111','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','原口好夫',1,'A111','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小寺栄蔵',1,'A111','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','塩崎順仁',1,'A111','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','海老正子',2,'A201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','竹村尚司',2,'A201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','山形小菜',2,'A201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西口卓雄',2,'A201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','柏木咲月',2,'A202','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'関口颯',2,'A202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','日下真吉',2,'A202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','岩瀬沙花',2,'A202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宍戸遥佳',2,'A203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大高真悠',2,'A203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','百瀬利勝',2,'A203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','寺沢夕弦',2,'A203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','森井敏昭',2,'A204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','佐伯博司',2,'A204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小平光明',2,'A204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','石倉一義',2,'A204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','首藤美愛',2,'A205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','杉田光彦',2,'A205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','横溝美玖',2,'A205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','村井正元',2,'A205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','三輪恵理',2,'A206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','川島明仁',2,'A206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中井結加',2,'A206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','町田冨子',2,'A206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','沼田紫苑',2,'A207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','平川翔子',2,'A207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','平林睦美',2,'A207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','杉原真悠',2,'A207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','長岡雪乃',2,'A208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','秋葉哲二',2,'A208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','河上幸也',2,'A208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','富永寧音',2,'A208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','磯崎祐希',2,'A209','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','名取佑奈',2,'A209','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','川合英紀',2,'A209','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','益田次男',2,'A209','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','江川正敏',2,'A210','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大谷郁代',2,'A210','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','浅沼瑠奈',2,'A210','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','春日百合',2,'A210','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','嶋田秀吉',2,'A211','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','藤岡正光',2,'A211','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','唐沢守友',2,'A211','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','南田妃奈',2,'A211','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','江成彦郎',3,'A301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','長岡克洋',3,'A301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','榊原一郎',3,'A301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','新川華蓮',3,'A302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宮原恋雪',3,'A302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小久保子',3,'A303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','佐久間美',3,'A303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大川茂男',3,'A303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','益田春佳',3,'A304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','寺崎俊二',3,'A304','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'谷金弥',3,'A304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','三橋直行',3,'A304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小嶋盛夫',3,'A305','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'平本椿',3,'A305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','岡野武治',3,'A305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','神崎彩香',3,'A305','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'岡田亮',3,'A306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西野友彦',3,'A306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','笠原胡桃',3,'A306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','日部珠希',3,'A306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','神崎竜太',3,'A307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','内村理歩',3,'A307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金城則夫',3,'A307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','丸山柚衣',3,'A307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','杉山瑠花',3,'A308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西口敏子',3,'A308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','土田日和',3,'A308','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'早川詩',3,'A308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','池内金造',3,'A309','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','野原正弘',3,'A309','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','矢沢柑奈',3,'A309','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','久保沙香',3,'A309','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小久保子',3,'A310','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大淵和弥',3,'A310','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','白石紗花',3,'A310','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','阪本茂男',3,'A310','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','藤野真菜',3,'A311','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','会田茉凛',3,'A311','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','高橋優',6,'B311','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','鈴木凛',6,'B301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','水戸香織',6,'B302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','齊藤翔',5,'B103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','道上初',5,'B111','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','栗生法間',5,'B202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','矢場東流',5,'B202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','水戸香織',6,'B302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','有地士郎',6,'B310','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','酒井泰斗',6,'B302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','青山佳子',3,'A304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','岡田志歩',3,'A305','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'山下好',4,'A401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','香坂留美',4,'A401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','植田嘉一',4,'A401','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'小沼悠',4,'A401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','赤羽美音',4,'A402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','村松結芽',4,'A402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','伊藤琉奈',4,'A402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','日野豊明',4,'A402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','入江平吉',4,'A403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','神保由姫',4,'A403','10'),");
//        sb_insert_test_ryosei.append('"("+UUID.randomUUID().toString()',+"'竹下伸',4,'A403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大友敏子',4,'A403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','花岡洋二',10,'図書室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','神山柚希',10,'図書室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大平伸子',10,'図書室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','矢島恵理',10,'図書室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宮地三男',10,'図書室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','永瀬実緒',10,'旧印刷室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','若林千代',10,'旧印刷室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','亀山英二',10,'旧印刷室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','坂本年子',10,'旧印刷室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','井手歩実',10,'旧会議室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','池谷京子',10,'旧会議室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小村健蔵',10,'旧会議室','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','安部太郎',10,'旧会議室','10'),");sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','田島沢子',	8	,'C101','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大村義勝',	8	,'C102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小松秋徳',	8	,'C103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','後藤光保',	8	,'C104','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','佐久間裕康',	8	,'C105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','下村金義',	8	,'C106','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','本間優香',	8	,'C107','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','嶋田勝夫',	8	,'C108','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','沢田運吉',	8	,'C109','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大槻理佐',	8	,'C101','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','黒川真琴',	8	,'C102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','田上知恵',	8	,'C103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','栗原軍市',	8	,'C104','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','庄司直美',	8	,'C105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','池上時代',	8	,'C106','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','古谷征輝',	8	,'C107','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','荒木恒宏',	8	,'C108','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','児玉正次',	8	,'C109','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','谷川敏継',	8	,'C101','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小川絵梨',	8	,'C102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','広瀬絢子',	8	,'C103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','三谷政治',	8	,'C104','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','塚田利浩',	8	,'C105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','阿部理香',	8	,'C106','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','志村里江子',	8	,'C107','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','比嘉長生',	8	,'C108','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大谷成夫',	8	,'C109','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','土田登喜雄',	8	,'C201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金城留美子',	8	,'C202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','太田つばさ',	8	,'C203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','古賀明博',	8	,'C204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','寺田元信',	8	,'C205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','吉本田鶴子',	8	,'C206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','河村裕実',	8	,'C207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','上野今朝雄',	8	,'C208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','谷本玲一',	8	,'C201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中野利次',	8	,'C202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','茂木雅則',	8	,'C203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','岡田時司',	8	,'C204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','石崎秀司',	8	,'C205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','谷沢子',	8	,'C206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','野中江美',	8	,'C207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','藤村洋子',	8	,'C208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','高瀬麗奈',	8	,'C202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','安田隆吉',	8	,'C203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','谷川信広',	8	,'C204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','嶋田和帰子',	8	,'C205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','三好美砂',	8	,'C206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','飯田弘人',	8	,'C207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','川端道和',	8	,'C208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','須藤好博',	8	,'C201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','川村るり子',	8	,'C202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','浅野亮子',	8	,'C203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','内海智美',	8	,'C204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','上村弘春',	8	,'C205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','竹田征二郎',	8	,'C206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宮田軍市',	8	,'C207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中沢雅近',	8	,'C208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中原房男',	8	,'C201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金子千恵',	8	,'C202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','三好理佐',	8	,'C203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中村栄之介',	8	,'C204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西沢清重',	8	,'C205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松永千津',	8	,'C206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','富永紀子',	8	,'C207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','根本益三',	8	,'C208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','神谷恵八郎',	8	,'C101','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','榎本邦煕',	8	,'C102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','栗田好行',	8	,'C103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','米田道正',	8	,'C104','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金田和帰子',	8	,'C105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','庄司保平',	8	,'C106','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小原敏継',	8	,'C107','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','古田邦美',	8	,'C108','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','臼井道貴',	8	,'C109','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','岡部輝志郎',	8	,'C101','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','岸本祐次郎',	8	,'C102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','手塚純子',	8	,'C103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','栗原時代',	9	,'C301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','上原完一',	9	,'C302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','神田裕行',	9	,'C303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','竹田茜',	9	,'C304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','志村周',	9	,'C305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大谷雅',	9	,'C306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','荒木博男',	9	,'C307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','川村昌光',	9	,'C308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金田憲志',	9	,'C301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金城三男',	9	,'C302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松野悠子',	9	,'C303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','新谷克',	9	,'C304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','吉野孝秀',	9	,'C305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','菊池時司',	9	,'C306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宮内静市',	9	,'C307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中井理香',	9	,'C308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','安井博男',	9	,'C301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','藤井由枝',	9	,'C302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','浅井直紀',	9	,'C303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','今井成男',	9	,'C304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','菅野彩',	9	,'C305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','若林朋美',	9	,'C306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','山村宏幸',	9	,'C307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','服部哲子',	9	,'C308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','本多良司',	9	,'C301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','鶴田康伸',	9	,'C302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','阿部勝司',	9	,'C303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','藤沢正輝',	9	,'C304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','川上美波留',	9	,'C305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','吉井良弥',	9	,'C306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大森れいや',	9	,'C307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','島田英博',	9	,'C308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','熊谷角太郎',	9	,'C301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','桑原平作',	9	,'C302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','橋本泰央',	9	,'C303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','丹羽朋',	9	,'C304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','岡崎靖浩',	9	,'C305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','広田希美',	9	,'C306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','牧野次生',	9	,'C307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','黒木紀世実',	9	,'C308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宇野美里',	9	,'C301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','新谷秀志',	9	,'C302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','野口隆吉',	9	,'C303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金沢直広',	9	,'C304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','河原由雄',	9	,'C305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宮川真希',	9	,'C306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大村隆次',	9	,'C307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','服部恵治',	9	,'C308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大橋初美',	9	,'C401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','湯浅教夫',	9	,'C402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','土屋八洲子',	9	,'C403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','深沢豊秋',	9	,'C404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大竹英充',	9	,'C405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','鎌田孝敏',	9	,'C406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','塚田玲子',	9	,'C407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','山田弘春',	9	,'C408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小松直志',	9	,'C401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','倉田有紀子',	9	,'C402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','森川忠秋',	9	,'C403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','丹羽玲一',	9	,'C404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','竹中直秋',	9	,'C405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','長田留三',	9	,'C406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','田上澄夫',	9	,'C407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','神田富良',	9	,'C408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','田原信広',	9	,'C401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','田上佳久',	9	,'C402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','木下英充',	9	,'C403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西田敬',	9	,'C404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金城玲',	9	,'C405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','今野美津江',	9	,'C406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','甲斐季孝',	9	,'C407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西野勝許',	9	,'C408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','内山俊憲',	9	,'C401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大内有紀子',	9	,'C402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','徳田雅',	9	,'C403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','斉藤敬正',	9	,'C404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','泉十三男',	9	,'C405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松野常一',	9	,'C406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','戸田周',	9	,'C407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','谷川晶紀',	9	,'C408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','藤原尚子',	7	,'B401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西岡亜里香',	7	,'B402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','堀田慶太郎',	7	,'B403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中井紀世実',	7	,'B404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','浅野公生',	7	,'B405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','柳田理絵',	7	,'B406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','川村彦好',	7	,'B407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','神田勝英',	7	,'B408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','鎌田義幸',	7	,'B409','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','平松稔郎',	7	,'B410','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西村晶紀',	7	,'B411','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','山中好和',	7	,'B412','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','岩瀬詔勝',	7	,'B401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','栗田好博',	7	,'B402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','滝沢章子',	7	,'B403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','谷本幸美',	7	,'B404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宮原麻',	7	,'B405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','角田常次',	7	,'B406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','篠原奈緒美',	7	,'B407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小山江美',	7	,'B408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金子登喜雄',	7	,'B409','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','片山茂信',	7	,'B410','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','長田裕紀',	7	,'B411','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','神田あさ美',	7	,'B412','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','富田政年',	7	,'B401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','菊地真由美',	7	,'B402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','須田麻里子',	7	,'B403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','平松光範',	7	,'B404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','園田威佐夫',	7	,'B405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小田弘義',	7	,'B406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小野朋絵',	7	,'B407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','相沢政広',	7	,'B408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','川野敬志',	7	,'B409','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松永夕子',	7	,'B410','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','福本十三男',	7	,'B411','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','菅茂俊',	7	,'B412','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','今井亀男',	7	,'B401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','三木成美',	7	,'B402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','水野雅',	7	,'B403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','山岡宏侑',	7	,'B404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','新田重行',	7	,'B405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中尾秀司',	7	,'B406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','井上菊江',	7	,'B407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','須藤洋和',	7	,'B408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','浜口愛',	7	,'B409','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','杉浦亀男',	7	,'B410','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小泉秀志',	7	,'B411','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','内海達太郎',	7	,'B412','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','森川初',	6	,'B301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小野嘉邦',	6	,'B302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','菅鉄次',	6	,'B303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中田由記彦',	6	,'B304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','阿部和帰子',	6	,'B305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','柳沢記代',	6	,'B306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宮川玲子',	6	,'B307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','吉原戴三',	6	,'B308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金城久哉',	6	,'B309','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','角田是則',	6	,'B310','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','新田迪子',	6	,'B311','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中沢秀次郎',	6	,'B301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','岩井貴朗',	6	,'B302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','天野有香',	6	,'B303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','村松有香',	6	,'B304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西本秀司',	6	,'B305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','荻野次男',	6	,'B306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','三上澄',	6	,'B307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','山根重行',	6	,'B308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大川恵',	6	,'B309','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','石黒孝彦',	6	,'B310','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大内忠和',	6	,'B311','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','河野康伸',	6	,'B301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','比嘉信春',	6	,'B302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','辻保光',	6	,'B303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小柳奈緒',	6	,'B304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中野利次',	6	,'B305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','湯浅平作',	6	,'B306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','山内克明',	6	,'B307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','山本登喜雄',	6	,'B308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','坂本聖誉',	6	,'B309','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松崎尭道',	6	,'B310','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','出口世弥',	6	,'B311','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','北島善英',	6	,'B301','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','石井謹二',	6	,'B302','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','丹羽元臣',	6	,'B303','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大石興子',	6	,'B304','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','吉野百世',	6	,'B305','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','塚田奈緒子',	6	,'B306','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','川崎秀一',	6	,'B307','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','上野多紀子',	6	,'B308','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','森睦男',	6	,'B309','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小原都義',	6	,'B310','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','藤野澄夫',	6	,'B311','10'),");

        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','古賀昌昭',5,'B201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','筒井幸博',5,'B202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','村山澄',5,'B203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','瀬戸真知子',5,'B204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','永野輝政',5,'B205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','栗原美伸',5,'B206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','土井克三',5,'B207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','中田絵梨',5,'B208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','佐々木禎二',5,'B209','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大崎美波留',5,'B210','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','児玉理香',5,'B211','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金城信人',5,'B201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松尾益已',5,'B202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','千田優紀',5,'B203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','坂田隆昭',5,'B204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','藤原るり子',5,'B205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大崎知実',5,'B206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','杉浦耕介',5,'B207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大橋道和',5,'B208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','本間真佐文',5,'B209','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','高橋公生',5,'B210','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','竹内勝英',5,'B211','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大西健生',5,'B201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','滝沢周',5,'B202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','畑中聖誉',5,'B203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金沢千恵',5,'B204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','吉岡波子',5,'B205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','比嘉真希',5,'B206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','榊原愛',5,'B207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','成田準司',5,'B208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宮内華代',5,'B209','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','武藤英則',5,'B210','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','下田沢子',5,'B211','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','栗田眞八',5,'B201','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','斉藤基',5,'B202','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宇野長一郎',5,'B203','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','石原文典',5,'B204','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松島永子',5,'B205','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','平松公生',5,'B206','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','北島永子',5,'B207','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大崎理佐',5,'B208','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','奥野有',5,'B209','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大塚善英',5,'B210','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','須田英充',5,'B211','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','村田恵志',5,'B101','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西川なつみ',5,'B102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','新谷江美',5,'B103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','吉村秋美',5,'B104','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宮原絢子',5,'B105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','新谷平作',5,'B106','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','秋田義幸',5,'B101','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','杉田昭良',5,'B102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','熊谷菊治',5,'B103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','高山幸美',5,'B104','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','矢野幸郎',5,'B105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','山口是則',5,'B106','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','田中朋絵',5,'B101','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','広瀬靖雄',5,'B102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','藤本利次',5,'B103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','緒方勇美',5,'B104','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金城恵志',5,'B105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','田上勝許',5,'B106','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大沢貞郎',5,'B101','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','永井健生',5,'B102','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','森下聖弘',5,'B103','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','三好知美',5,'B104','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','関口永子',5,'B105','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','白石理佐',5,'B106','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大森沢子',4,'A401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松浦安民',4,'A402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','北川完一',4,'A403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','宮内佳紀',4,'A404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','根岸道彦',4,'A405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松崎順子',4,'A406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','田口信',4,'A407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','野口辰己',4,'A408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','坂口芳弘',4,'A409','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','庄司るり子',4,'A410','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','手塚暉一郎',4,'A411','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金子訓',4,'A401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','藤井弘春',4,'A402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','松井明博',4,'A403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','福岡美智',4,'A404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','野崎茜',4,'A405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','三好由記彦',4,'A406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','沼田恒宏',4,'A407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','福永美里',4,'A408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小松敏継',4,'A409','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','長田鋼三郎',4,'A410','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','金井真紀子',4,'A411','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','村上伸江',4,'A401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小池秋徳',4,'A402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','上野勇美',4,'A403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','谷顕太郎',4,'A404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','今野房男',4,'A405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','小柳麗奈',4,'A406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','長岡重文',4,'A407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西岡俊郎',4,'A408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','三谷梨恵子',4,'A409','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','野沢慎治',4,'A410','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大崎志奈',4,'A411','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','西沢知美',4,'A401','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','白井武士',4,'A402','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','木村亜里香',4,'A403','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','成田千勢子',4,'A404','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','栗原優子',4,'A405','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','熊谷敏継',4,'A406','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','秋田礼子',4,'A407','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大川美海',4,'A408','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','藤井都義',4,'A409','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','水谷ハルノ',4,'A410','10'),");
        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','大原厚成',4,'A411','10'),");

        sb_insert_test_ryosei.append("('" + UUID.randomUUID().toString() + "','古市佐子',10,'旧会議室','10');");

        String sql_insert_test_ryosei = sb_insert_test_ryosei.toString();
        db.execSQL(sql_insert_test_ryosei);
    }


    public void addParcel(
            SQLiteDatabase db,
            String owner_uid,
            String owner_room,
            String owner_ryosei_name,
            String register_staff_uid,
            String register_staff_room_name,
            String register_staff_ryosei_name,
            int placement) {
        String uuid = UUID.randomUUID().toString();
        StringBuilder sb_insert_Parcel = new StringBuilder();
        sb_insert_Parcel.append("insert into parcels (" +
                "uid,owner_uid,owner_room_name,owner_ryosei_name," +
                "register_datetime," +
                "register_staff_uid,register_staff_room_name,register_staff_ryosei_name,placement,sharing_status" +
                ") values ('");
        sb_insert_Parcel.append(uuid + "','");
        sb_insert_Parcel.append(owner_uid + "',");
        sb_insert_Parcel.append(" \"" + owner_room + " \",");
        sb_insert_Parcel.append(" \"" + owner_ryosei_name + "\",");
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format(dateObj);
        sb_insert_Parcel.append(" \"" + string_register_time + "\",'");
        sb_insert_Parcel.append(register_staff_uid + "',");
        sb_insert_Parcel.append(" \"" + register_staff_room_name + "\",");
        sb_insert_Parcel.append(" \"" + register_staff_ryosei_name + "\",");
        sb_insert_Parcel.append(" \"" + placement + "\",'10') ");

        String sql_insert_test_parcel = sb_insert_Parcel.toString();
        db.execSQL(sql_insert_test_parcel);


        nimotsuCountAdder(db, owner_uid);
        event_add_touroku(db, owner_uid, owner_room, owner_ryosei_name);
    }

    public void addParcelOthers(
            SQLiteDatabase db,
            String owner_uid,
            String owner_room,
            String owner_ryosei_name,
            String register_staff_uid,
            String register_staff_room_name,
            String register_staff_ryosei_name,
            int placement,
            String others_detail) {
        String uuid = UUID.randomUUID().toString();
        StringBuilder sb_insert_Parcel = new StringBuilder();
        sb_insert_Parcel.append("insert into parcels (" +
                "uid,owner_uid,owner_room_name,owner_ryosei_name," +
                "register_datetime," +
                "register_staff_uid,register_staff_room_name,register_staff_ryosei_name,placement,sharing_status,note" +
                ") values ('");
        sb_insert_Parcel.append(uuid + "','");
        sb_insert_Parcel.append(owner_uid + "',");
        sb_insert_Parcel.append(" \"" + owner_room + " \",");
        sb_insert_Parcel.append(" \"" + owner_ryosei_name + "\",");
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format(dateObj);
        sb_insert_Parcel.append(" \"" + string_register_time + "\",'");
        sb_insert_Parcel.append(register_staff_uid + "',");
        sb_insert_Parcel.append(" \"" + register_staff_room_name + "\",");
        sb_insert_Parcel.append(" \"" + register_staff_ryosei_name + "\",");
        sb_insert_Parcel.append(" \"" + placement + "\",");
        sb_insert_Parcel.append(" \"" + "10" + "\",");
        sb_insert_Parcel.append(" \"" + others_detail + "\")");

        String sql_insert_test_parcel = sb_insert_Parcel.toString();
        db.execSQL(sql_insert_test_parcel);

        nimotsuCountAdder(db, owner_uid);
        event_add_touroku(db, owner_uid, owner_room, owner_ryosei_name);
    }

    public void event_add_touroku(
            SQLiteDatabase db,
            String ryosei_id,
            String room_name,
            String ryosei_name) {
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
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format(dateObj);
        sb_insert_Parcel.append(" \"" + string_register_time + "\",");
        sb_insert_Parcel.append(" \"" + 1 + " \",");
        Cursor cursor = db.rawQuery("SELECT uid FROM parcels ORDER BY register_datetime DESC LIMIT 1", null);
        cursor.moveToFirst();
        String uid_str = String.valueOf(cursor.getInt(cursor.getColumnIndex("uid")));
        sb_insert_Parcel.append(" \"" + uid_str + " \",'");
        sb_insert_Parcel.append(ryosei_id + "',");
        sb_insert_Parcel.append(" \"" + room_name + " \",");
        sb_insert_Parcel.append(" \"" + ryosei_name + "\",'10')");
        String sql_insert_event = sb_insert_Parcel.toString();
        db.execSQL(sql_insert_event);

    }

    public void event_add_uketori(
            SQLiteDatabase db,
            String ryosei_id,
            String room_name,
            String ryosei_name) {
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
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format(dateObj);
        sb_insert_Parcel.append(" \"" + string_register_time + "\",");
        sb_insert_Parcel.append(" \"" + 2 + " \",");
        Cursor cursor = db.rawQuery("SELECT uid FROM parcels ORDER BY release_datetime DESC LIMIT 1", null);
        cursor.moveToFirst();
        String uid_str = String.valueOf(cursor.getInt(cursor.getColumnIndex("uid")));
        sb_insert_Parcel.append(" \"" + uid_str + " \",'");
        sb_insert_Parcel.append(ryosei_id + "',");
        sb_insert_Parcel.append(" \"" + room_name + " \",");
        sb_insert_Parcel.append(" \"" + ryosei_name + "\",'10')");
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
            String release_staff_ryosei_name) {
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format(dateObj);
        String oldSharingStatus = returnStatus(db, "parcels", parcels_uid);
        String newSharingStatus = updateSharingStatus(oldSharingStatus);
        String sql = "UPDATE parcels SET " +
                " release_staff_uid = '" + release_staff_uid + "'," +
                " release_staff_room_name = '" + release_staff_room_name + "' ," +
                " release_staff_ryosei_name = '" + release_staff_ryosei_name + "' ," +
                " is_released = " + "1" + " ," +
                " release_datetime =" + " \"" + string_register_time + "\"," +
                "sharing_status =" + newSharingStatus + " WHERE uid = '" + parcels_uid + "'";
        db.execSQL(sql);
        nimotsuCountSubber(db, owner_id);
        event_add_uketori(db, owner_id, owner_room, owner_ryosei_name);
    }

    private String updateSharingStatus(String oldSharingStatus) {
        switch (oldSharingStatus) {
            case "10":
                return "10";
            case "11":
                return "11";
            case "30":
                return "11";
            default:
                return "0";
        }
    }

    public void receiveParcelsProxy(
            SQLiteDatabase db,
            String owner_id,
            String owner_room,
            String owner_ryosei_name,
            String parcels_uid,
            String release_staff_uid,
            String release_staff_room_name,
            String release_staff_ryosei_name,
            String proxy_id) {
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        // 日時情報を指定フォーマットの文字列で取得
        String string_register_time = format.format(dateObj);
        String sql = "UPDATE parcels SET " +
                " release_staff_uid = '" + release_staff_uid + "'," +
                " release_staff_room_name = '" + release_staff_room_name + "' ," +
                " release_staff_ryosei_name = '" + release_staff_ryosei_name + "' ," +
                " is_released = " + "1" + " ," +
                " release_datetime =" + " \"" + string_register_time + "\","+
                " release_agent_uid ='" + proxy_id +"',"
                ;
        switch (returnStatus(db, "parcels", parcels_uid)) {
            case "10":
                sql = sql + " sharing_status =" + "'10'" + " WHERE uid ='" + parcels_uid +"'";
                break;
            case "11":
                sql = sql + " sharing_status =" + "'11'" + " WHERE uid ='" + parcels_uid+"'";
                break;
            case "30":
                sql = sql + " sharing_status =" + "'11'" + " WHERE uid ='" + parcels_uid+"'";
                break;
        }
        db.execSQL(sql);
        nimotsuCountSubber(db, owner_id);
        event_add_uketori(db, owner_id, owner_room, owner_ryosei_name);

    }


    public List<Map<String, String>> nimotsuCountOfRyosei(SQLiteDatabase db, String owner_id) {
        //荷物IDとラベル(日時、受け取り事務当、場所）を返す。
        List<Map<String, String>> show_owners_parcels = new ArrayList<>();
        String sql = "SELECT uid, placement, register_datetime," +
                "register_staff_room_name, register_staff_ryosei_name,note, lost_datetime " +
                "FROM parcels WHERE is_released = 0 AND owner_uid ='" + owner_id + "'";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Map<String, String> parcels_raw = new HashMap<>();
            int index_id = cursor.getColumnIndex("uid");
            int index_placement = cursor.getColumnIndex("placement");
            int index_register_datetime = cursor.getColumnIndex("register_datetime");
            int index_register_staff_room_name = cursor.getColumnIndex("register_staff_room_name");
            int index_register_staff_ryosei_name = cursor.getColumnIndex("register_staff_ryosei_name");
            int index_lost_time = cursor.getColumnIndex("lost_datetime");
            int index_note = cursor.getColumnIndex("note");
            String rabel = "";
            String parcels_id = "";
            String lost_datetime= "";
            String parcels_attribute = "";
            parcels_id = (cursor.getString(index_id));
            rabel += "登録日時　" + cursor.getString(index_register_datetime);
            rabel += " ";
            rabel += "受取事務当　" + cursor.getString(index_register_staff_room_name);
            rabel += " ";
            rabel += cursor.getString(index_register_staff_ryosei_name);
            rabel += " ";
            lost_datetime = cursor.getString(index_lost_time);
            switch (cursor.getInt(index_placement)) {
                case 0:
                    rabel += "普通";
                    parcels_attribute = "荷物";
                    break;
                case 1:
                    rabel += "冷蔵";
                    parcels_attribute = "冷蔵";
                    break;
                case 2:
                    rabel += "冷凍";
                    parcels_attribute = "冷凍";
                    break;
                case 3:
                    rabel += "大型";
                    parcels_attribute = "大型";
                    break;
                case 4:
                    rabel += "不在票";
                    parcels_attribute = "不在票";
                    break;
                case 5:
                    rabel += cursor.getString(index_note);
                    parcels_attribute = "その他";
                    break;
            }
            parcels_raw.put("rabel", rabel);
            String look = parcels_id;
            parcels_raw.put("parcels_id", parcels_id);
            parcels_raw.put("attribute",parcels_attribute);
            parcels_raw.put("lost_datetime",lost_datetime);
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


    public void nimotsuCountAdder(SQLiteDatabase db, String owner_id) {
        int parcels_current_count = 0;
        int parcels_total_count = 0;
        //寮生に荷物カウントを追加する。

        //owner_idの寮生を取得
        String sql = "SELECT parcels_current_count, parcels_total_count FROM ryosei  WHERE uid = '" + owner_id + "'";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);


        while (cursor.moveToNext()) {
            // カラムのインデックス値を取得。
            int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            parcels_current_count = cursor.getInt(index_parcels_current_count);
            // カラムのインデックス値を元に実際のデータを取得。
            int index_parcels_total_count = cursor.getColumnIndex("parcels_total_count");
            parcels_total_count = cursor.getInt(index_parcels_total_count);
            // カラムのインデックス値を元に実際のデータを取得。
        }
        sql = "UPDATE ryosei SET parcels_current_count =" + String.valueOf(parcels_current_count + 1)
                + ", parcels_total_count =" + String.valueOf(parcels_total_count + 1) + ", sharing_status =" + "'10'" + " WHERE uid ='" + owner_id + "'";
        db.execSQL(sql);
    }

    public void nimotsuCountSubber(SQLiteDatabase db, String owner_id) {
        int parcels_current_count = 1;
        //int parcels_total_count = 0;
        //寮生に荷物カウントを追加する。

        //owner_idの寮生を取得
        String sql = "SELECT parcels_current_count, parcels_total_count FROM ryosei  WHERE uid = '" + owner_id + "'";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);


        while (cursor.moveToNext()) {
            // カラムのインデックス値を取得。
            int index_parcels_current_count = cursor.getColumnIndex("parcels_current_count");
            parcels_current_count = cursor.getInt(index_parcels_current_count);
            // カラムのインデックス値を元に実際のデータを取得。
            int index_parcels_total_count = cursor.getColumnIndex("parcels_total_count");
            //parcels_total_count = cursor.getInt(index_parcels_total_count);
            // カラムのインデックス値を元に実際のデータを取得。
            parcels_current_count--;
        }
        sql = "UPDATE ryosei SET parcels_current_count =" + String.valueOf(parcels_current_count) + ", sharing_status =" + "'10'"
                + " WHERE uid ='" + owner_id + "'";
        db.execSQL(sql);
    }

    public void night_check_updater(SQLiteDatabase db, String parcels_uid) {
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        String string_register_time = format.format(dateObj);
        String sql = "UPDATE parcels SET " +
                " lost_datetime =" + " \"" + string_register_time + "\"" + ", sharing_status =" + "'10'" +
                " WHERE uid ='" + parcels_uid + "'";
        db.execSQL(sql);
    }

    public void event_add_night_duty(SQLiteDatabase db, String staffid, String staffroom, String staffname) {

    }


    public void delete_event(SQLiteDatabase db, String event_id, String ryosei_id, String parcel_id, String event_type) {
        //event idは1 or 2が入る　1が登録のイベントを消し込むとき、2が受取のイベントを消し込むとき

        //room_name, ryosei_name, total_parcels_count, current_parcels_countを取得する
        String sql = "SELECT room_name, ryosei_name, parcels_total_count, parcels_current_count  FROM ryosei where uid='" + ryosei_id + "'";
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();
        String room_name = cursor.getString(cursor.getColumnIndex("room_name"));
        String ryosei_name = cursor.getString(cursor.getColumnIndex("ryosei_name"));
        String parcels_total_count = cursor.getString(cursor.getColumnIndex("parcels_total_count"));
        String parcels_current_count = cursor.getString(cursor.getColumnIndex("parcels_current_count"));
        int parcels_TC = Integer.parseInt(parcels_total_count), parcels_CC = Integer.parseInt(parcels_current_count);
        if (event_type.equals("1")) {
            parcels_TC--;
            parcels_CC--;
        } else {
            parcels_TC++;
            parcels_CC++;
        }
        parcels_total_count = String.valueOf(parcels_TC);
        parcels_current_count = String.valueOf(parcels_CC);

        //created_atを取得する
        // 現在日時情報で初期化されたインスタンスの生成
        Date dateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        // 日時情報を指定フォーマットの文字列で取得
        String created_at = format.format(dateObj);


        //event tableのupdate、insert
        //ryosei tableのupdate（荷物数カウント）
        //parcelstableのupdate（論理削除）
        //が必要

        //event
        sql = "update parcel_event set is_deleted=1, sharing_status=10 where uid='" + event_id + "'";
        db.execSQL(sql);
        sql = "insert into parcel_event(created_at,event_type,parcel_uid,ryosei_uid,room_name,ryosei_name,target_event_uid,is_finished,sharing_status)";
        sql += "values('";
        sql += created_at + "',3,'" + parcel_id + "','" + ryosei_id + "',";
        sql += "'" + room_name + "',";
        sql += "'" + ryosei_name + "','";
        sql += event_id + "',1,'10');";
        db.execSQL(sql);

        //ryosei
        sql = "update ryosei set parcels_total_count=" +
                parcels_total_count +
                ",parcels_current_count=" +
                parcels_current_count +
                ",sharing_status=" +
                "10"
                + " where uid='" + ryosei_id + "'";
        db.execSQL(sql);

        //parcels
        sql = "update parcels set is_deleted=1, sharing_status='10' where uid ='" + parcel_id + "'";
        db.execSQL(sql);
    }


    public String select_ryosei_show_json(SQLiteDatabase db, int type) {
        //owner_idの寮生を取得
        String sql;
        //owner_idの寮生を取得
        if (type == 1) {
            sql = "SELECT *  FROM ryosei order by uid";
        } else if (type == 10) {
            sql = "SELECT *  FROM ryosei where sharing_status = '10'";
        } else if (type == 11) {
            sql = "SELECT *  FROM ryosei where sharing_status = '11'";
        } else {
            sql = "SELECT *  FROM ryosei order by uid";
        }
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) return "";


        String json_str = "[\n";

        while (cursor.moveToNext()) {
            json_str += "{\n";
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
            //末尾からカンマを削除
            json_str = json_str.substring(0, json_str.length() - 2);
            json_str += "\n";
            json_str += "},\n";

        }
        json_str = json_str.substring(0, json_str.length() - 2);
        json_str += "\n";
        json_str += "]";
        return json_str;
    }

    public String select_parcels_show_json(SQLiteDatabase db, int type) {
        String sql;
        //owner_idの寮生を取得
        if (type == 1) {
            sql = "SELECT *  FROM parcels order by uid";
        } else if (type == 10) {
            sql = "SELECT *  FROM parcels where sharing_status = '10'";
        } else if (type == 11) {
            sql = "SELECT *  FROM parcels where sharing_status = '11'";
        } else {
            sql = "SELECT *  FROM parcels order by uid";
        }
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) return "";

        String json_str = "[\n";

        while (cursor.moveToNext()) {
            json_str += "{\n";
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
            //末尾からカンマを削除
            json_str = json_str.substring(0, json_str.length() - 2);
            json_str += "\n";
            json_str += "},\n";

        }
        json_str = json_str.substring(0, json_str.length() - 2);
        json_str += "\n";
        json_str += "]";
        return json_str;
    }

    public String select_event_show_json(SQLiteDatabase db, int type) {
        //owner_idの寮生を取得
        String sql;
        //owner_idの寮生を取得
        if (type == 1) {
            sql = "SELECT *  FROM parcel_event order by uid";
        } else if (type == 10) {
            sql = "SELECT *  FROM parcel_event where sharing_status = '10'";
        } else if (type == 11) {
            sql = "SELECT *  FROM parcel_event where sharing_status = '11'";
        } else {
            sql = "SELECT *  FROM parcel_event order by uid";
        }
        // SQLの実行。
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) return "";
        String json_str = "[\n";

        while (cursor.moveToNext()) {
            json_str += "{\n";
            for (enum_event column : enum_event.values()) {
                String col = column.toString();
                String val = cursor.getString(cursor.getColumnIndex(col));
                json_str += col;
                json_str += ": ";
                if (column.getCode() == 1 && val != null) json_str += "\"";
                json_str += val;
                if (column.getCode() == 1 && val != null) json_str += "\"";
                json_str += ",\n";
            }
            //末尾からカンマを削除
            json_str = json_str.substring(0, json_str.length() - 2);
            json_str += "\n";
            json_str += "},\n";

        }
        json_str = json_str.substring(0, json_str.length() - 2);
        json_str += "\n";
        json_str += "]";
        return json_str;
    }

    public String returnStatus(SQLiteDatabase db, String table, String uid) {
        Cursor cursor = db.rawQuery("SELECT sharing_status FROM " + table + " WHERE uid = " + "'" + uid + "'", null);
        String result_sharing_status = null;
        int result_sharing_status_index;

        try {
            while (cursor.moveToNext()) {
                // カラムのインデックス値を取得。
                result_sharing_status_index = cursor.getColumnIndex("sharing_status");
                // カラムのインデックス値を元に実際のデータを取得。
                result_sharing_status = cursor.getString(result_sharing_status_index);
            }
        } finally {
            cursor.close();
        }

        return result_sharing_status;
    }

    public void update_sharingstatus(SQLiteDatabase db) {
        String sql = "update parcels set sharing_status = '30' where sharing_status = '20' or sharing_status = '21' or sharing_status = '10' or sharing_status = '11'";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}



