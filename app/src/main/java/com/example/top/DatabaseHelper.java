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
        sb_parcels.append(" a TEXT,");
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
        sb_parcel_event.append(" is_after_fixed_time INTEGER DEFAULT 0,");
        sb_parcel_event.append(" is_finished INTEGER DEFAULT 0,");
        sb_parcel_event.append(" is_deleted INTEGER DEFAULT 0,");
        sb_parcel_event.append(" sharing_status TEXT");
        sb_parcel_event.append(");");
        String sql_parcel_event = sb_parcel_event.toString();
        db.execSQL(sql_parcel_event);

        insert_test_ryoseitaikai(db);


    }
    void insert_test_ryoseitaikai(SQLiteDatabase db){
        StringBuilder sb_insert_test_ryosei = new StringBuilder();
        sb_insert_test_ryosei.append("insert into ryosei(uid,room_name,ryosei_name,ryosei_name_kana,block_id,sharing_status) values");
        sb_insert_test_ryosei.append("('C66040CB-CF55-4824-AA20-0E06B38A6D0F','A101','中川雄太','なかがわゆうた',1,'10'),");
        sb_insert_test_ryosei.append("('38E3ABBB-CEAA-4673-AE12-0A8889255329','A101','西垣健吾','にしがきけんご',1,'10'),");
        sb_insert_test_ryosei.append("('C063BD88-6DB1-4E42-842A-0CFA00D8BFB1','A101','顧平原','ぐぴんゆあん',1,'10'),");
        sb_insert_test_ryosei.append("('20035AD1-0CEF-4654-92B0-1C4D5CC4AE93','A101','内海武尊','うつみたける',1,'10'),");
        /*
        sb_insert_test_ryosei.append("('F718E342-4B61-4353-A6A0-0A6A29F2A418','A102','道田蒼人','みちだそうと',1,'10'),");
        sb_insert_test_ryosei.append("('E85F9C11-3ACA-45C3-95FD-ED3C21A0AC81','A102','小口裕斗','おぐちゆうと',1,'10'),");
        sb_insert_test_ryosei.append("('FF47C09E-FAC1-481D-909C-1589B1F8B70C','A102','大久保温人','おおくぼあつと',1,'10'),");
        sb_insert_test_ryosei.append("('C7984408-E175-4903-8A03-15C9F74A519A','A103','高瀬海人','たかせかいと',1,'10'),");
        sb_insert_test_ryosei.append("('9FCA4DD3-F897-44CF-A032-46683F60547B','A103','津村颯人','つむらはやと',1,'10'),");
        sb_insert_test_ryosei.append("('3AEA0AE7-2222-4805-8D93-E19C597EC52C','A103','伊藤拓馬','いとうたくま',1,'10'),");
        sb_insert_test_ryosei.append("('83CA32E7-93DB-4AED-A9CE-46B17223C32E','A103','新谷雄太郎','しんたにゆうたろう',1,'10'),");
        sb_insert_test_ryosei.append("('351581E4-2894-494F-AD84-25C2A87102C5','A104','劉鵬','りゅうほう',1,'10'),");
        sb_insert_test_ryosei.append("('C2F24AD9-3C32-48DF-8693-0B1C9AE48BC4','A104','藤原淳','ふじはらあつし',1,'10'),");
        sb_insert_test_ryosei.append("('98D4F62B-3F10-4F13-9ADF-B9E069DC11C5','A104','大畑翔平','おおはたしょうへい',1,'10'),");
        sb_insert_test_ryosei.append("('125DD1C3-02B9-4464-858F-096C7DEA858D','A105','堀川雅直','ほりかわまさなお',1,'10'),");
        sb_insert_test_ryosei.append("('31942C5E-DE4D-4426-A821-45193E6BBC1B','A105','クリナン・ルークブライス','クリナン・ルークブライス',1,'10'),");
        sb_insert_test_ryosei.append("('2FA8371A-3103-432C-B3AA-8D80E9C043A7','A105','ヘラトパティラナハラゲパシンドゥ','ヘラトパティラナハラゲパシンドゥ',1,'10'),");
        sb_insert_test_ryosei.append("('4685C8CB-BF2A-491A-AC8E-E79A5322C0D4','A105','包昊南','ばおはおなん',1,'10'),");
        sb_insert_test_ryosei.append("('726FA8AA-5104-4B19-8452-7260C0D91BED','A106','佐藤龍之介','さとうりゅうのすけ',1,'10'),");
        sb_insert_test_ryosei.append("('6362C936-90C8-47E6-99A4-42EAA8B8EAF0','A106','浅野涼太','あさのりょうた',1,'10'),");
        sb_insert_test_ryosei.append("('72E5921C-0CD9-4FAB-89AB-D1D925ECF938','A106','米田豊','まいたゆたか',1,'10'),");
        sb_insert_test_ryosei.append("('9143E8EE-ACAF-4618-A09E-AB3E3DA4ABC8','A107','赤池良太','あかいけりょうた',1,'10'),");
        sb_insert_test_ryosei.append("('8A077637-E582-4B3F-903F-33E3E86DCC4F','A107','松下哲雄','まつしたてつお',1,'10'),");
        sb_insert_test_ryosei.append("('8D916CEA-735C-47AC-AFEE-8E6727EBD083','A107','横山ルイ','よこやまるい',1,'10'),");
        sb_insert_test_ryosei.append("('593CF0F5-AB00-4F57-B3C2-07BAAAA5B310','A108','浅野達彦','あさのたつひこ',1,'10'),");
        sb_insert_test_ryosei.append("('D0DA99CC-E169-475C-9AC9-94EBAF21BD8A','A108','堀岡勇杜','ほりおかゆうと',1,'10'),");
        sb_insert_test_ryosei.append("('8DEF7146-ADAD-4243-B844-BD1B983E5E53','A108','湯本希','ゆもとのぞむ',1,'10'),");
        sb_insert_test_ryosei.append("('2DF9D0B6-1A74-4B55-8F9D-86853D008B1F','A109','石黒翔','いしぐろしょう',1,'10'),");
        sb_insert_test_ryosei.append("('340157A0-E8F7-4069-8C31-C43B9782D70D','A109','福富彬','ふくとみあきら',1,'10'),");
        sb_insert_test_ryosei.append("('BD31FAE8-4662-4AD3-9570-240E146B1BF8','A109','ムハンマドエハブ','むはんまどえへぶ',1,'10'),");
        sb_insert_test_ryosei.append("('E52E282B-1B6C-4A36-ABAF-9FF2AA0F3351','A109','和田郁史','わだたかふみ',1,'10'),");
        sb_insert_test_ryosei.append("('AAF0969A-64B3-4C54-8395-2CCA475D6D2E','A110','米沢光','よねざわひかる',1,'10'),");
        sb_insert_test_ryosei.append("('12446F2E-5BC4-45A4-8D3D-356054B393B4','A110','前畑武史','まえはたてるや',1,'10'),");
        sb_insert_test_ryosei.append("('4745F69E-D74C-4B61-B052-B47B8E6D28B5','A111','高木悠','たかぎゆう',1,'10'),");
        sb_insert_test_ryosei.append("('2E3A75F9-03E1-4456-9E17-E2FA64F133EA','A111','笹山隆人','ささやまりゅうと',1,'10'),");
        sb_insert_test_ryosei.append("('B4D9E666-CBE1-4C6B-B642-7065028A1FB5','A111','浅井怜斗','あさいれいと',1,'10'),");
        sb_insert_test_ryosei.append("('C6C6713C-36AA-4D24-B7A4-C713FF60927E','A111','北岸政樹','きたぎしまさき',1,'10'),");
        sb_insert_test_ryosei.append("('D5E5B941-CE2C-4E97-AB66-981C814DE2E5','旧印刷室','天野祐','あまのゆう',10,'10'),");
        sb_insert_test_ryosei.append("('F51C5157-DC5B-41EC-8BE2-B83623305B4C','旧印刷室','風間小次郎','かざまこじろう',10,'10'),");
        sb_insert_test_ryosei.append("('71AD12C2-6D52-4A08-AE13-9B69E16D72A9','旧印刷室','小嶋太志','こじまたいし',10,'10'),");
        sb_insert_test_ryosei.append("('B3C466D0-AC65-46F8-BAB5-A465ABB65215','旧印刷室','松下佳祐','まつしたけいすけ',10,'10'),");
        sb_insert_test_ryosei.append("('5F02C743-A429-4490-AAB8-9B7F2D1AD621','旧印刷室','阿保龍星','あぼりゅうせい',10,'10'),");
        sb_insert_test_ryosei.append("('EFBD3803-DFA2-483C-8B39-8F5259E4C6AE','旧SC室','岡本晴気','おかもとはるき',10,'10'),");
        sb_insert_test_ryosei.append("('0EF8B156-C733-4E82-8600-B2535AA37A2C','旧SC室','三宅優希','みやけゆうき',10,'10'),");
        sb_insert_test_ryosei.append("('7A74D11D-6303-439E-84D5-29E4042716E3','旧SC室','立入勲','たちいりいさお',10,'10'),");
        sb_insert_test_ryosei.append("('5DFFDEE9-C984-425A-B3B5-96F858A0830D','A110','鷲見恭介','わしみきょうすけ',1,'10'),");
        sb_insert_test_ryosei.append("('BA590333-30E5-410F-86A3-0E5B116DC04A','旧SC室','中野息吹','なかのいぶき',10,'10'),");
        sb_insert_test_ryosei.append("('2BA1A85A-ADC5-448A-8A1B-D2485CEF9A2A','旧SC室','藤田陽斗','ふじたようと',10,'10'),");
        sb_insert_test_ryosei.append("('730A0D2E-5BA1-48EB-8199-7576EA072D5C','コンテナ102','太田祐輔','おおたゆうすけ',10,'10'),");
        sb_insert_test_ryosei.append("('799D7187-425D-48BE-8899-39A9901B3185','コンテナ103','高田暁典','たかたあきのり',10,'10'),");
        sb_insert_test_ryosei.append("('0ECE93DB-48AB-48E1-B5C5-FBBB4DE2AD48','A201','稲岡大悟','いなおかだいご',2,'10'),");
        sb_insert_test_ryosei.append("('ED0B1AB7-2679-4E01-B1AA-F2B7BBF27A72','A201','杉山高大','すぎやまたかひろ',2,'10'),");
        sb_insert_test_ryosei.append("('01B9BC75-1526-4D54-B42C-81B6F87B8F4F','A201','小山怜将','こやまりょうすけ',2,'10'),");
        sb_insert_test_ryosei.append("('FEF36F28-FC4B-485F-8973-3E12B4A2C3E8','A201','倉根啓','くらねはじめ',2,'10'),");
        sb_insert_test_ryosei.append("('3DF4D4AA-39E3-4BB8-8B32-3C8CBC0F33AD','A202','新廉史郎','あたらしれんしろう',2,'10'),");
        sb_insert_test_ryosei.append("('C7D62410-80E8-44E0-963A-E76A821B5D0C','A202','服部航大','はっとりこうだい',2,'10'),");
        sb_insert_test_ryosei.append("('ED66C7B1-C836-4924-88EC-4B9E54C87832','A202','大野慈温','おおのじおん',2,'10'),");
        sb_insert_test_ryosei.append("('F0690098-84EC-4755-809F-B6E54E4E6C00','A202','近藤燿吾','こんどうようご',2,'10'),");
        sb_insert_test_ryosei.append("('339E06D5-D872-4659-A874-80294D5155EE','A203','中嶋智桃','なかじまちもも',2,'10'),");
        sb_insert_test_ryosei.append("('2C8F7DB0-86E3-4D55-B8E9-104381AE34A9','A203','古田万紀子','ふるたまきこ',2,'10'),");
        sb_insert_test_ryosei.append("('5238ECBB-B468-4027-8BC2-0E19120070F7','A203','重岡慧実','しげおかさとみ',2,'10'),");
        sb_insert_test_ryosei.append("('0B21DC6A-2241-417C-83FF-47B209568E9A','A203','藤吉春菜','ふじよしはるな',2,'10'),");
        sb_insert_test_ryosei.append("('B2BBC89A-2863-43FA-9F32-BEF643DE1CDC','A203','侯菲菲','ほうふぇいふぇい',2,'10'),");
        sb_insert_test_ryosei.append("('B20365CC-9C7C-4F78-B5BF-4D6C2417CC61','A204','菊池陽南子','きくちひなこ',2,'10'),");
        sb_insert_test_ryosei.append("('C7EBA966-E69E-45F9-9851-1720B7622470','A204','平松那奈子','ひらまつななこ',2,'10'),");
        sb_insert_test_ryosei.append("('F197104C-6CCD-4AF4-B4CF-7155C684AFF6','A204','竹下光','たけしたひかる',2,'10'),");
        sb_insert_test_ryosei.append("('55712653-D978-4F4B-81FD-FF016CC5B213','A204','村上彩乃','むらかみあやの',2,'10'),");
        sb_insert_test_ryosei.append("('D3E4C7D8-AAA9-4CA8-B2FF-BDE40B618BCD','A205','北村凌雅','きたむらりょうが',2,'10'),");
        sb_insert_test_ryosei.append("('C10871D3-4AAE-439E-A5D4-F433E5852653','A205','髙村匡弘','たかむらまさひろ',2,'10'),");
        sb_insert_test_ryosei.append("('9EF645F7-91B8-4E3C-9709-518BD2B42EFB','A205','塩崎翔大','しおさきしょうた',2,'10'),");
        sb_insert_test_ryosei.append("('9119BE90-8FE8-4A77-A531-AA2783CFB17C','A205','阿式裕斗','あじきゆうと',2,'10'),");
        sb_insert_test_ryosei.append("('11CC5E2D-21AB-492C-83F4-8189F9D1C92A','A206','松井樹丸','まついみきまる',2,'10'),");
        sb_insert_test_ryosei.append("('B6FB4F01-B6E3-43E1-83FA-9859B2B49158','A206','竹田響','たけだひびき',2,'10'),");
        sb_insert_test_ryosei.append("('8468A743-EFCB-4040-BCE3-D289F7DD02FE','A206','中島敦志','なかじまあつし',2,'10'),");
        sb_insert_test_ryosei.append("('B429868E-CB56-42A7-B49F-588DBEBC4C71','A206','延山知弘','のべやまともひろ',2,'10'),");
        sb_insert_test_ryosei.append("('EE9D6E6F-7252-4A12-8586-FEA7EF29DDDB','A207','鈴木駿','すずきしゅん',2,'10'),");
        sb_insert_test_ryosei.append("('3701BF16-7317-409B-8E9F-4E793ED21660','A207','市川公康','いちかわきみやす',2,'10'),");
        sb_insert_test_ryosei.append("('11CCFABF-500A-4C79-A567-916F67E0B6A2','A207','河野尚貴','こうのなおき',2,'10'),");
        sb_insert_test_ryosei.append("('E978490E-B7DD-4917-9229-75E5374F2903','A207','早松龍正','はやまつりゅうせい',2,'10'),");
        sb_insert_test_ryosei.append("('F64F8056-6146-4CCF-A8D6-2320EF6F52A5','A208','原恒士郎','はらこうしろう',2,'10'),");
        sb_insert_test_ryosei.append("('41868C13-E1DD-4188-BE26-1C7AA483894A','A208','生地涼太郎','いくじりょうたろう',2,'10'),");
        sb_insert_test_ryosei.append("('F41C27E4-D7F3-4728-AF2B-B399B62113CA','A208','赤木颯馬','あかぎそうま',2,'10'),");
        sb_insert_test_ryosei.append("('104C9AAF-F8BF-415C-BC2E-EFAC22F3B6C0','A208','福島星','ふくしましょう',2,'10'),");
        sb_insert_test_ryosei.append("('1C825F4A-4BC0-406C-9A81-F0D83EEAB0E5','A209','梁命詩','やんみょんし',2,'10'),");
        sb_insert_test_ryosei.append("('6251DC26-C40E-4B2F-9431-58A9D80CCA09','A209','塚本裕太','つかもとゆうた',2,'10'),");
        sb_insert_test_ryosei.append("('F73CFDB6-D013-4E4D-A4A8-6E05505F1F36','A209','谷口佳瑚','たにぐちけいご',2,'10'),");
        sb_insert_test_ryosei.append("('8DF50388-D9C5-426C-8049-0B68AD390E8E','A209','渡邊雄大','わたなべゆうだい',2,'10'),");
        sb_insert_test_ryosei.append("('A183634C-ACB4-4F48-B018-EE402CA07F39','A210','劉可','りゅうか',2,'10'),");
        sb_insert_test_ryosei.append("('4BE9D7B8-D696-425C-A586-DB329000BC10','A210','齋田真弓','さいだまゆみ',2,'10'),");
        sb_insert_test_ryosei.append("('1DE980EC-B34E-4E6A-B257-6AF487F844E0','A210','平尾莉夏','ひらおりか',2,'10'),");
        sb_insert_test_ryosei.append("('9CE511E6-48FB-4AE0-BD1A-DFBAE19DC854','A210','友保花','ともやすはな',2,'10'),");
        sb_insert_test_ryosei.append("('D536C4FF-3D59-43C5-83E3-73F9BA3FE1E6','A211','高城健人','たかしろけんと',2,'10'),");
        sb_insert_test_ryosei.append("('977326CF-A85D-4B4E-BABA-FAA9DCD33622','A211','菅原輝紀','すがはらてるき',2,'10'),");
        sb_insert_test_ryosei.append("('EB816848-8E2E-47D0-B35A-3C4D15443130','A211','中田和希','なかたかずき',2,'10'),");
        sb_insert_test_ryosei.append("('48E87EC4-88FF-4C7A-91FB-DA2E3736A19A','A211','松島勇名','まつしまいさな',2,'10'),");
        sb_insert_test_ryosei.append("('43A93F64-E26D-4E01-885A-E3A02477399B','A301','向後拓真','こうごたくま',3,'10'),");
        sb_insert_test_ryosei.append("('2F30C8DC-C143-4E1E-9CA0-C24389CE388D','A301','鈴木哲史','すずきてつし',3,'10'),");
        sb_insert_test_ryosei.append("('19A2FD10-0805-48CC-8E6E-6D2E5674113F','A301','西原郁生','にしはらいくみ',3,'10'),");
        sb_insert_test_ryosei.append("('5684C4C5-7005-4EC0-A207-3594956DEF8B','A301','本村剛基','もとむらたけき',3,'10'),");
        sb_insert_test_ryosei.append("('754211A5-0F36-4B04-B8D3-3E1E205356C7','A302','福永航','ふくながわたる',3,'10'),");
        sb_insert_test_ryosei.append("('5A226CE8-4F77-41D5-855C-4CD888C61BCC','A302','安齋久翔','あんさいひさと',3,'10'),");
        sb_insert_test_ryosei.append("('440E89B6-2266-47B8-94F0-D80A22E0E46D','A302','堀碩信','ほりみつのぶ',3,'10'),");
        sb_insert_test_ryosei.append("('1BAEDAD7-0BB0-41B0-8E55-06F740C9FFE3','A302','緒方雄大','おがたゆうだい',3,'10'),");
        sb_insert_test_ryosei.append("('E82512D6-33B4-4F73-822A-31BD3DBAC16F','A303','久野託望','ひさのたくみ',3,'10'),");
        sb_insert_test_ryosei.append("('A916CFF4-005B-41FB-BE43-46724217C041','A303','稲葉皓信','いなばてるのぶ',3,'10'),");
        sb_insert_test_ryosei.append("('3A5B7BE8-3D00-4F25-9F1A-1CFFA3901250','A303','村松薫','むらまつかおる',3,'10'),");
        sb_insert_test_ryosei.append("('ABCE9FED-5A04-45CC-945F-D98928EDE98A','A303','阪野宏章','ばんのひろあき',3,'10'),");
        sb_insert_test_ryosei.append("('22337143-9EF9-4892-9DE5-E01C5045A424','A304','村松朋実','むらまつともみ',3,'10'),");
        sb_insert_test_ryosei.append("('AD5E7CFE-62AF-408D-93B2-0DF703952EEB','A304','青木遥菜','あおきはるな',3,'10'),");
        sb_insert_test_ryosei.append("('8954DB92-55D2-4C17-9A98-856C54B02C06','A304','麻生倫','あそうつぐ',3,'10'),");
        sb_insert_test_ryosei.append("('295A4980-DA5E-4D40-B3BB-0EB3A1140120','A304','千田理紗','ちだりさ',3,'10'),");
        sb_insert_test_ryosei.append("('1518C75F-1525-4629-9809-8AF325149417','A305','石川湖雪','いしかわこゆき',3,'10'),");
        sb_insert_test_ryosei.append("('9D6E916C-EC82-4FC4-A534-5FAFD35BFD4E','A305','玉元麻貴','たまもとまき',3,'10'),");
        sb_insert_test_ryosei.append("('0044402A-082A-4A0D-93E8-3849AD47EAF0','A305','坂上和鴻','さかがみわこう',3,'10'),");
        sb_insert_test_ryosei.append("('9FF9FE7B-BDCA-478A-8B15-03C5BCDDCABB','A306','中垣仁志','なかがきさとし',3,'10'),");
        sb_insert_test_ryosei.append("('D3D28CB4-0BD6-4956-8BEC-934A7ACF541C','A306','柱尾俊介','はしらおしゅんすけ',3,'10'),");
        sb_insert_test_ryosei.append("('3AFFA801-3353-4E58-926F-4495E994FD0E','A306','畑山祐樹','はたやまゆうき',3,'10'),");
        sb_insert_test_ryosei.append("('98DF21FE-8C21-4138-8B37-74C2F5E88026','A306','長壁涼介','おさかべりょうすけ',3,'10'),");
        sb_insert_test_ryosei.append("('38490D30-ED0F-450F-85E7-17FFEE9E6404','A307','茂森勇人','しげもりはやと',3,'10'),");
        sb_insert_test_ryosei.append("('CD509C28-9E21-40B7-B050-3103A134D15D','A307','原田祥仁','はらだしょうじん',3,'10'),");
        sb_insert_test_ryosei.append("('8C26C86F-583D-4431-86ED-3C78E6002A7E','A307','脇山敬人','わきやまけいと',3,'10'),");
        sb_insert_test_ryosei.append("('7803AD86-2A07-42F8-9BB7-D5F4B7566B3E','A307','山田樺音','やまだかおと',3,'10'),");
        sb_insert_test_ryosei.append("('E3A61929-4193-4DF3-961C-CF724FCFCB6D','A308','山本薪志','やまもとまきし',3,'10'),");
        sb_insert_test_ryosei.append("('6AF851AB-F3C1-4268-B08E-E1CC1CD6B54F','A308','小豆澤陸','あずきざわりく',3,'10'),");
        sb_insert_test_ryosei.append("('05F46E14-865A-4D2F-A8FB-AA876F61EC04','A308','尾本晴海','おもとはれうみ',3,'10'),");
        sb_insert_test_ryosei.append("('93D48B2C-9343-4D6A-A3C3-28695262C6DF','A308','齊藤滉平','さいとうこうへい',3,'10'),");
        sb_insert_test_ryosei.append("('FEED20F2-14A7-4827-98E1-B4E8162885E5','A309','谷協至','たにきょうじ',3,'10'),");
        sb_insert_test_ryosei.append("('B16332DF-0B03-484C-A2DD-418DD2388950','A309','佐々木大道','ささきひろみち',3,'10'),");
        sb_insert_test_ryosei.append("('ADB6A9A0-ED89-4B62-A9F0-02305BF23AE3','A309','鈴木将吾','すずきしょうご',3,'10'),");
        sb_insert_test_ryosei.append("('A2F3B14C-4931-4863-B15A-97D26D8768F6','A309','持留光','もちどめひかり',3,'10'),");
        sb_insert_test_ryosei.append("('08DB3166-DA20-4206-A742-CA60D3D1A8BC','A310','木戸悠太郎','きどゆうたろう',3,'10'),");
        sb_insert_test_ryosei.append("('6ACCF772-610D-4A36-A3AA-2662AB81DB37','A310','竹本紘希','たけもとひろき',3,'10'),");
        sb_insert_test_ryosei.append("('B6B6AEC1-467D-4394-8043-D7855666F9CC','A310','橋本竣史','はしもとしゅんじ',3,'10'),");
        sb_insert_test_ryosei.append("('20FEA6EE-0F6D-4068-A4E5-E3E8314136A6','A310','尾畑鑑明','おばたかんめい',3,'10'),");
        sb_insert_test_ryosei.append("('1D1643D3-239C-4CFC-84D7-D8BC66B0A874','A311','江坂裕貴','えさかひろき',3,'10'),");
        sb_insert_test_ryosei.append("('5AFF268E-4FAA-46C2-9550-2E3E1AA16328','A311','山下大碧','やましただいき',3,'10'),");
        sb_insert_test_ryosei.append("('B9884661-F5E6-485E-90E8-B210783619D9','A311','金本公平','かなもとこうへい',3,'10'),");
        sb_insert_test_ryosei.append("('255493FF-E465-4DDE-BC6F-8C235DDCF66E','図書室','長江光斗','ながえゆうと',10,'10'),");
        sb_insert_test_ryosei.append("('CF7D21D8-D2BE-4722-A0AF-D06228EDA3D9','A401','内梨隼人','うちなしはやと',4,'10'),");
        sb_insert_test_ryosei.append("('328896AB-861B-42B6-8749-775148060B82','A401','大川一路','おおかわいちろ',4,'10'),");
        sb_insert_test_ryosei.append("('A1207F73-090D-4BFF-8869-5CF968222EF7','A401','日高拓海','ひだかたくみ',4,'10'),");
        sb_insert_test_ryosei.append("('8E6CB162-348D-4537-B100-EEED342C5AC4','A401','板井健悟','いたいけんご',4,'10'),");
        sb_insert_test_ryosei.append("('9C533301-28C0-438D-BC9F-7CECD8C167EA','A401','安里樹','あさとたつき',4,'10'),");
        sb_insert_test_ryosei.append("('7675C99A-8FE1-4E37-BA79-B2C6E73DF81D','A402','中村聡一郎','なかむらそういちろう',4,'10'),");
        sb_insert_test_ryosei.append("('D097C9CC-C949-4EE2-A759-E5B74684AE5B','A402','杉本圭','すぎもとけい',4,'10'),");
        sb_insert_test_ryosei.append("('1C0CF6BA-1AF9-41F3-BFEA-188FA12D5301','A402','小西遥翔','こにしはると',4,'10'),");
        sb_insert_test_ryosei.append("('9FBF7286-AB2E-4811-95FB-8EE93F03127C','A402','上西勇樹','うえにしゆうき',4,'10'),");
        sb_insert_test_ryosei.append("('9F187E80-7913-4CC3-8C6E-B2E52A340FFA','A403','伊藤大翔','いとうひろと',4,'10'),");
        sb_insert_test_ryosei.append("('3800D2D7-292A-47F4-998F-B6BDBFB19665','A403','辻在穂','つじあきほ',4,'10'),");
        sb_insert_test_ryosei.append("('81DC86CB-D98E-439C-9F63-FEB4954460C9','A403','渡辺亮太','わたなべりょうた',4,'10'),");
        sb_insert_test_ryosei.append("('BF48B86B-8C17-4659-B985-1A18059A9E4C','A403','嵯峨稔己','さがとしき',4,'10'),");
        sb_insert_test_ryosei.append("('A9182173-08A5-4631-A2ED-06DA3A7504ED','A404','グェンファムカンティエン','ぐぇんふぁむかんてぃえん',4,'10'),");
        sb_insert_test_ryosei.append("('E30B9C6F-360F-415E-B528-EAA989AEF055','A404','山本杏奈','やまもとあんな',4,'10'),");
        sb_insert_test_ryosei.append("('16BC4DB6-5C26-4C5D-831D-51354418C7D1','A404','村田貴和子','むらたきわこ',4,'10'),");
        sb_insert_test_ryosei.append("('B483BF4F-0831-4854-999F-2EC894C91C50','A404','林絵里奈','はやしえりな',4,'10'),");
        sb_insert_test_ryosei.append("('4359B363-F63D-432F-81AF-448C352D3C82','A405','田中蓮','たなかれん',4,'10'),");
        sb_insert_test_ryosei.append("('1593ED47-F49F-4721-89F4-D451536DC3BF','A405','八巻風花','やまきふうか',4,'10'),");
        sb_insert_test_ryosei.append("('0817DC51-DB4F-4311-90DB-D52DD8A0260D','A405','広瀬花','ひろせはな',4,'10'),");
        sb_insert_test_ryosei.append("('C83A1384-BDE7-43C3-83EB-E919E22CF1F0','A405','アイジュン','あいじゅん',4,'10'),");
        sb_insert_test_ryosei.append("('A4896470-E445-4DA2-8013-04961A205CEB','A406','清川祥太','きよかわしょうた',4,'10'),");
        sb_insert_test_ryosei.append("('7CDB1366-9BBA-4D37-8C87-51ED383F17A3','A406','原優太郎','はらゆうたろう',4,'10'),");
        sb_insert_test_ryosei.append("('81727C16-0967-4560-807B-F6BB3BFB98B9','A406','麻生雄太','あそうゆうた',4,'10'),");
        sb_insert_test_ryosei.append("('AC752C67-9EF7-43A9-87B6-8A80B68B01DC','A406','鈴木海斗','すずきかいと',4,'10'),");
        sb_insert_test_ryosei.append("('ED6EE17B-6646-40BD-8941-F9F37E6C6469','A407','宮内里桜','みやうちりお',4,'10'),");
        sb_insert_test_ryosei.append("('031DB368-7F73-4904-B398-A2D2B063D852','A407','鈴木さら','すずきさら',4,'10'),");
        sb_insert_test_ryosei.append("('D0BFB448-0FCA-4D85-9E3A-909CA80914CB','A407','重本鈴花','しげもとすずか',4,'10'),");
        sb_insert_test_ryosei.append("('B1E3B335-F514-472E-AC78-A95D85035D57','A408','岩崎稜','いわさきりょう',4,'10'),");
        sb_insert_test_ryosei.append("('97A4EBB6-3E2F-4D1B-B6FD-5DBC37B5A6FB','A408','荒滝俊之','あらたきしゅんすけ',4,'10'),");
        sb_insert_test_ryosei.append("('5E062B27-9832-4185-93DE-1132E9334CA7','A408','池田翔一','いけだしょういち',4,'10'),");
        sb_insert_test_ryosei.append("('DD44E1C5-9F25-407D-9979-26D0C91F41A7','A408','福元敬翔','ふくもとけいと',4,'10'),");
        sb_insert_test_ryosei.append("('7C889BE6-B9B5-4AA4-B82D-44ED0D14DCFC','A409','周子剣','しゅうしけん',4,'10'),");
        sb_insert_test_ryosei.append("('27766FF5-2127-4314-B294-106F4C38FEAC','A409','張賢','ちょうけん',4,'10'),");
        sb_insert_test_ryosei.append("('BA380327-5761-4E8C-A341-1240F522A63D','A409','王焜沢','おうゆんたく',4,'10'),");
        sb_insert_test_ryosei.append("('5EE18EF0-ED33-4252-8C89-A9FACF3D6190','A409','モハメドサクル','もはめどさくる',4,'10'),");
        sb_insert_test_ryosei.append("('780ED2D6-BCAA-4F9C-BBD0-CF38BCF0EFB0','A410','山下礼華','やましたれいか',4,'10'),");
        sb_insert_test_ryosei.append("('0652F2BB-FC79-4A6A-A672-C03B18E37819','A410','佐藤優里','さとうゆうり',4,'10'),");
        sb_insert_test_ryosei.append("('FFF3D089-AE73-4FD2-B48A-BF928A2B9029','A410','若林未己','わかばやしみお',4,'10'),");
        sb_insert_test_ryosei.append("('C8D11018-DA29-44FA-A8B7-EC0E093D2C6E','A410','下山花','しもやまはな',4,'10'),");
        sb_insert_test_ryosei.append("('260DC9C5-4D50-481C-BADA-572502F13F70','A411','岡田陸太郎','おかだりくたろう',4,'10'),");
        sb_insert_test_ryosei.append("('69CA97E3-C8E1-42E1-B745-8A6C8FBED23E','A411','齋藤健留','さいとうたける',4,'10'),");
        sb_insert_test_ryosei.append("('D4D82779-3CAA-4A3D-A04E-A87DF3D3B299','A411','松岡史温','まつおかしおん',4,'10'),");
        sb_insert_test_ryosei.append("('F8A37BC4-C987-42F2-A777-84D5A0AE30D5','B100','矢島臨','やじまのぞむ',5,'10'),");
        sb_insert_test_ryosei.append("('C26F6F78-EA80-4713-AB2B-F9A294CE8504','B100','高井絢平','たかいじゅんぺい',5,'10'),");
        sb_insert_test_ryosei.append("('A21B5079-6C4F-4E5C-AF72-A3A34F8028B9','B100','永富康暉','ながとみこうき',5,'10'),");
        sb_insert_test_ryosei.append("('D25FE823-BD23-4B32-BDF8-C70F1D657F4E','B100','小西悠矢','こにしゆうや',5,'10'),");
        sb_insert_test_ryosei.append("('953D1F23-F271-4B25-8B6B-1349C9D7591D','B101','飯塚丈流','いいづかたける',5,'10'),");
        sb_insert_test_ryosei.append("('439EB606-552F-4F9B-9275-D3A9CB454CEE','B101','坂本洸','さかもとこう',5,'10'),");
        sb_insert_test_ryosei.append("('FAB00186-2BEC-4DB6-AFF7-CD09B60950F7','B101','中村晃太','なかむらこうた',5,'10'),");
        sb_insert_test_ryosei.append("('F62E0AEE-C2F7-4995-929C-4D53C40254B2','B101','白川史也','しらかわふみや',5,'10'),");
        sb_insert_test_ryosei.append("('42A97565-3A5F-4B24-8B4A-A6C173534CDE','B102','長谷川大海','はせがわひろみ',5,'10'),");
        sb_insert_test_ryosei.append("('DFFB391A-AC61-4B8A-A9E1-E19C79728C84','B102','大島武生','おおしまたけお',5,'10'),");
        sb_insert_test_ryosei.append("('3C76B95D-D01D-4282-BD8C-4C9C7AC3174F','B102','清野悠輔','きよのゆうすけ',5,'10'),");
        sb_insert_test_ryosei.append("('16A2445D-CF27-45DE-B2BE-804D6807983E','B102','真柴誠','ましばまこと',5,'10'),");
        sb_insert_test_ryosei.append("('2C5446AB-3678-40DF-AAAF-2D9B15278F6F','B103','谷口将史','たにぐちまさし',5,'10'),");
        sb_insert_test_ryosei.append("('CDF52B8D-B548-4D93-BB4E-68D9499BA294','B103','佐藤隆太郎','さとうりゅうたろう',5,'10'),");
        sb_insert_test_ryosei.append("('DDF703FD-F6F4-49FB-B95D-8E45DFD3B2A8','B103','小幡直','おばたなお',5,'10'),");
        sb_insert_test_ryosei.append("('0D80BFEC-0A7B-4202-8486-A888D296C5C5','B103','藤巻駿太','ふじまきしゅんた',5,'10'),");
        sb_insert_test_ryosei.append("('A53937D5-8CC6-4F02-9DAA-8C1766F3775F','B104','戸矢龍','とやりゅう',5,'10'),");
        sb_insert_test_ryosei.append("('585D4237-66DA-43E8-96E6-7CD0482AED24','B104','細原直人','ほそはらなおと',5,'10'),");
        sb_insert_test_ryosei.append("('0B6A94C0-40E7-4898-BD35-7BD6FE23D91B','B104','湯河虎太郎','ゆかわこたろう',5,'10'),");
        sb_insert_test_ryosei.append("('13F0534E-71E8-4FDB-8B17-783C7AC1407D','B104','寺沢遼太郎','てらさわりょうたろう',5,'10'),");
        sb_insert_test_ryosei.append("('A459C2A1-BDE2-4F59-AF2D-FCD6EFAD18B8','B105','和田一希','わだかずき',5,'10'),");
        sb_insert_test_ryosei.append("('E4CF8C6F-E99C-410E-B325-45EAB125FD0D','B105','尾崎瑠星','おざきりゅうせい',5,'10'),");
        sb_insert_test_ryosei.append("('42CB749C-6492-41AE-B7C4-1BC2F27C513F','B105','溝口聖','みぞぐちあきら',5,'10'),");
        sb_insert_test_ryosei.append("('29CB8357-B661-438A-B9E3-4A2F3A456A26','B106','小出斎','こいでさい',5,'10'),");
        sb_insert_test_ryosei.append("('3AFD8EC6-ED15-4DEF-86E1-BCB7BECD4253','B106','藤津尚仁','ふじつなおと',5,'10'),");
        sb_insert_test_ryosei.append("('F0E68664-2227-4770-9478-C942B456D66C','B106','草川顕吾','くさかわけんご',5,'10'),");
        sb_insert_test_ryosei.append("('C90D1305-CDD6-439B-BB83-FB3AC22101C2','B106','片岡悠輝','かたおかはるき',5,'10'),");
        sb_insert_test_ryosei.append("('D2A2B11F-D346-4B2B-8718-B729B00D09D4','B106','三宅裕也','みやけゆうや',5,'10'),");
        sb_insert_test_ryosei.append("('BC0D9D0C-3529-41AB-88E6-0D9017BA66C9','B106','生駒忠大','いこまただひろ',5,'10'),");
        sb_insert_test_ryosei.append("('24D0EA8D-3378-4F47-99F0-EB9CCBA6E8BA','B107','熊谷晃希','くまがいこうき',5,'10'),");
        sb_insert_test_ryosei.append("('E6538FC9-78A2-4B7E-89E6-A90C54B18173','B401','向谷陸','むこうだにりく',7,'10'),");
        sb_insert_test_ryosei.append("('3B65E731-2F0C-4EF3-B151-5AED9665897C','B401','金山駿平','かなやましゅんぺい',7,'10'),");
        sb_insert_test_ryosei.append("('4FB0015E-79DE-4173-BBBD-EE0D968CBC02','B401','山田隆太','やまだりゅうた',7,'10'),");
        sb_insert_test_ryosei.append("('853AB660-DBD3-43B7-A0E8-121AAF0285FB','B401','山本琢人','やまもとたくと',7,'10'),");
        sb_insert_test_ryosei.append("('9F49A355-1C10-4C76-A554-89CDF78F6C30','B402','中條朝陽','ちゅうじょうあさひ',7,'10'),");
        sb_insert_test_ryosei.append("('D96B822A-AF19-45DC-A0E4-A9E2F34E9DCC','B402','玉木康太','たまきこうた',7,'10'),");
        sb_insert_test_ryosei.append("('EF5FAD3B-45AB-40CE-AF30-8D813F974227','B402','菊地貫太','きくちかんた',7,'10'),");
        sb_insert_test_ryosei.append("('EB2B4C37-6C62-44E9-8283-25648C380338','B402','NieXingchen','じょうせいしん',7,'10'),");
        sb_insert_test_ryosei.append("('199D7745-03BA-488A-9E58-64572673113E','B403','寺岡佑樹','てらおかゆうき',7,'10'),");
        sb_insert_test_ryosei.append("('777A6282-63F6-4FE7-90BD-A66C8D2A5739','B403','宮本直也','みやもとなおや',7,'10'),");
        sb_insert_test_ryosei.append("('C2F55F38-FD04-4A10-A677-143E0B7FAFB6','B403','後藤武琉','ごとうたける',7,'10'),");
        sb_insert_test_ryosei.append("('7D2CCC2F-6B6E-415C-8E57-2488D422015C','B404','四十坊純也','しじゅうぼうじゅんや',7,'10'),");
        sb_insert_test_ryosei.append("('70A27C03-3BC4-4277-8582-0611C8C62873','B404','倉岡成','くらおかじょう',7,'10'),");
        sb_insert_test_ryosei.append("('75C4094E-D55D-483E-9686-070EFD5FC2DB','B404','江本晃希','えもとこうき',7,'10'),");
        sb_insert_test_ryosei.append("('3F63EEF4-3483-46FD-9DE3-B4B8AF73A1DA','B404','田村太','たむらふとし',7,'10'),");
        sb_insert_test_ryosei.append("('CF1FB712-C4F5-420F-9E1C-D4410D505601','B405','笠島光司','かさしまこうじ',7,'10'),");
        sb_insert_test_ryosei.append("('CF5511F8-5B3E-4051-A576-AC6D4F9ECE3A','B405','薮内慶大','やぶうちけいた',7,'10'),");
        sb_insert_test_ryosei.append("('4B60EDAE-E760-4EBA-B712-17F6D149891E','B405','吉原主税','よしはらちから',7,'10'),");
        sb_insert_test_ryosei.append("('E3D8DC0F-3C90-4CF9-8891-3571C20192E1','B405','吉田岳史','よしだたけし',7,'10'),");
        sb_insert_test_ryosei.append("('67344373-BBCD-436A-B940-A48023463663','B406','中村瑛','なかむらあきら',7,'10'),");
        sb_insert_test_ryosei.append("('3CB5846A-83D8-4EFE-822F-D0A0175C7A0E','B406','梅澤和真','うめざわかずま',7,'10'),");
        sb_insert_test_ryosei.append("('65154138-E1E9-4F19-9986-E9FB6BEB5994','B406','板原舜也','いたはらしゅんや',7,'10'),");
        sb_insert_test_ryosei.append("('08831B1C-4F60-4900-BC1E-DC689CEC0D27','B406','松島康','まつしまやすし',7,'10'),");
        sb_insert_test_ryosei.append("('11452F71-437F-4DB9-B7EF-E19A92BAE740','B407','西尾和朗','にしおかずあき',7,'10'),");
        sb_insert_test_ryosei.append("('3E3E08D0-2DF4-4E4D-BADB-4F6AF9011086','B407','齋藤魁','さいとうかい',7,'10'),");
        sb_insert_test_ryosei.append("('D050A07A-53C7-46D3-AA46-8A8A16FE33DF','B407','竹内優太','たけうちゆうた',7,'10'),");
        sb_insert_test_ryosei.append("('EFB59692-A584-42D7-8C67-D24346F07F2C','B407','松野飛太','まつのひゅうた',7,'10'),");
        sb_insert_test_ryosei.append("('FF7D6EF1-0B94-48DC-985E-E990958A214F','B408','寺田稔彦','てらだとしひこ',7,'10'),");
        sb_insert_test_ryosei.append("('59104E95-81B5-4DA6-B264-15F163B6A552','B408','原渚沙','はらなぎさ',7,'10'),");
        sb_insert_test_ryosei.append("('AB339781-236E-48FE-8074-14039361B473','B408','松井優','まついゆう',7,'10'),");
        sb_insert_test_ryosei.append("('5FAAA8B3-FEA4-42CF-8DE7-2EBBDBF1AA04','B408','黒羽勇','くろはゆう',7,'10'),");
        sb_insert_test_ryosei.append("('7B5719A4-9AE4-4A1D-A8F3-78BA251D594F','B409','尾方司貴','おがたしき',7,'10'),");
        sb_insert_test_ryosei.append("('96CFF14B-F4DC-4D6A-8A52-6F3E4CCBE923','B409','清水翼','しみずつばさ',7,'10'),");
        sb_insert_test_ryosei.append("('15C06617-88DA-4324-8051-895752F0FD5C','B409','島田遼','しまだはるか',7,'10'),");
        sb_insert_test_ryosei.append("('7376A023-9B05-48E5-94DB-5BE363176BDC','B410','山口真希','やまぐちまき',7,'10'),");
        sb_insert_test_ryosei.append("('AD092CEA-E78F-43D3-A2B2-C8DF1FF938B7','B410','河合愛実','かわいまなみ',7,'10'),");
        sb_insert_test_ryosei.append("('0677F33D-48FC-40E0-BD7E-455AAB55C0F0','B410','樟香里','くすかおり',7,'10'),");
        sb_insert_test_ryosei.append("('D8F57391-ED7F-487A-89B9-C085A41993E3','B410','原田杜美','はらだもりみ',7,'10'),");
        sb_insert_test_ryosei.append("('C8A443B7-88D3-4CA7-8DDA-BC6137B43756','B411','目黒美貴','めぐろみき',7,'10'),");
        sb_insert_test_ryosei.append("('55F92FF5-9863-41A6-85C1-15B35E936D2F','B411','山本ましろ','やまもとましろ',7,'10'),");
        sb_insert_test_ryosei.append("('77586119-E381-450B-9AC1-2EFCDFEE031F','B411','高橋梨佳子','たかはしりかこ',7,'10'),");
        sb_insert_test_ryosei.append("('AD9D2069-67A5-4F7C-86F3-929E031614B3','B411','若林奏','わかばやしかな',7,'10'),");
        sb_insert_test_ryosei.append("('F6D5659E-A25C-486A-8C1A-7B304C3D03BF','B412','笠野英正','かさのひでまさ',7,'10'),");
        sb_insert_test_ryosei.append("('5330CFC0-797B-4F19-B459-3EBA5A2C7D60','B412','落合拓麿','おちあいたくま',7,'10'),");
        sb_insert_test_ryosei.append("('26852D1D-CD94-4040-986D-E824F17B63AD','B412','石原亜鈴','いしはらあれい',7,'10'),");
        sb_insert_test_ryosei.append("('C3658284-8C8D-4821-81EF-4024B1F71CCE','C109','作部羊平','さくべようへい',7,'10'),");
        sb_insert_test_ryosei.append("('C864DB3F-4499-49F1-B0EE-011DF4C1C7CA','第二音楽室','北村剛','きたむらつよし',7,'10'),");
        sb_insert_test_ryosei.append("('B8F3582D-2461-481A-98F2-68A173984EEF','C101','砂川桃吾','すながわとうご',8,'10'),");
        sb_insert_test_ryosei.append("('7C234222-5495-4536-A114-9A3D1E37BE82','C101','渡邊雅廣','わたなべまさひろ',8,'10'),");
        sb_insert_test_ryosei.append("('5DC85929-6ABF-46DB-8058-64ADA3F7CF3B','C101','谷口幸暉','たにぐちけんご',8,'10'),");
        sb_insert_test_ryosei.append("('323EA7D9-4C16-449C-A07D-82E5410F6A69','C101','須磨健太郎','すまけんたろう',8,'10'),");
        sb_insert_test_ryosei.append("('BA82C57E-276C-4710-A9E4-521162A3E9C1','C101','金種澈','きむじゅんちょる',8,'10'),");
        sb_insert_test_ryosei.append("('89CB32C7-E971-4E9B-BBA9-C1261E6F0434','C103','西口耕矢','にしぐちこうや',8,'10'),");
        sb_insert_test_ryosei.append("('1D3522F7-4B8A-4D7C-A24E-9839B095D7CB','C103','山口琉星','やまぐちりゅうせい',8,'10'),");
        sb_insert_test_ryosei.append("('3248E804-0475-4C20-B2A3-79F0F8D8A8FB','C103','高橋空吾','たかはしくうご',8,'10'),");
        sb_insert_test_ryosei.append("('B261D724-026D-4C0F-8888-58F9F267CB65','C103','片山夏樹','かたやまなつき',8,'10'),");
        sb_insert_test_ryosei.append("('2999630C-B68E-437D-9CC7-0C8378B3A340','C103','長部悦哲','おさべよしあき',8,'10'),");
        sb_insert_test_ryosei.append("('AD6F1EE3-7E7D-4D5B-AC4C-2A8195184094','C103','齋藤大輝','さいとうひろき',8,'10'),");
        sb_insert_test_ryosei.append("('9469D28B-7110-432E-90D2-1247510A2D2A','C105','別所拓実','べっしょたくみ',8,'10'),");
        sb_insert_test_ryosei.append("('EF34A259-7D30-4DF8-806E-350DCC62169E','C105','武田智容','たけだともひろ',8,'10'),");
        sb_insert_test_ryosei.append("('D1E27A1F-B7A2-4438-AB1A-E1E1E71DCB84','C105','あらた花魁','あらたすぐる',8,'10'),");
        sb_insert_test_ryosei.append("('BE3E518B-870B-4EE8-A10D-81FD3B0471C5','C105','青木康幸','あおきやすゆき',8,'10'),");
        sb_insert_test_ryosei.append("('9069B734-EFBC-40CE-BBDA-8C1A2361A55C','C105','ラファエルロドリゲス','ラファエルロドリゲス',8,'10'),");
        sb_insert_test_ryosei.append("('CAFA7167-EE3C-4B47-B4AB-D78FF772F09F','C105','川副博嗣','かわぞえひろつぐ',8,'10'),");
        sb_insert_test_ryosei.append("('BBB4865E-11F6-43B0-B776-CCDC36CF9EE6','C105','リュウ・ユウ・フク','リュウ・ユウ・フク',8,'10'),");
        sb_insert_test_ryosei.append("('4B39BD42-5A60-4531-A4F7-58658270C2AB','C107','小網健義','こあみたけよし',8,'10'),");
        sb_insert_test_ryosei.append("('781F9B43-27E4-42C2-AE3B-3BF9500899AC','C107','柴田佳祐','しばたけいすけ',8,'10'),");
        sb_insert_test_ryosei.append("('DEBFBA78-9222-42E5-9E9E-B1A512C6433E','C107','石内良季','いしうちよしき',8,'10'),");
        sb_insert_test_ryosei.append("('6B00D36E-7479-429E-8671-D11CFF8ED6CA','C107','申昇容','かねやますんよん',8,'10'),");
        sb_insert_test_ryosei.append("('8427C3FD-1A26-41FC-87C9-88CB8438D187','C107','松本悠太郎','まつもとゆうたろう',8,'10'),");
        sb_insert_test_ryosei.append("('67589236-D1FA-4EC9-B005-938D62B1F053','C107','潘藝心','はんげいしん',8,'10'),");
        sb_insert_test_ryosei.append("('6FF21F6D-CC6D-4448-931A-2FF4A6E65227','C201','渡邊鷹志','わたなべたかし',8,'10'),");
        sb_insert_test_ryosei.append("('A982111C-4851-4A6F-A94A-E26FFD2A8E6F','C201','片岡佑介','かたおかゆうすけ',8,'10'),");
        sb_insert_test_ryosei.append("('2E653BE4-C1AE-4510-AFBB-C0587D4D9C54','C201','能津竣一','のづしゅんいち',8,'10'),");
        sb_insert_test_ryosei.append("('CD387564-3CF7-4010-808B-67644FE19EA8','C201','眞榮田亮','まえだりょう',8,'10'),");
        sb_insert_test_ryosei.append("('739F4036-10E3-497E-B96F-C95C8C7B5035','C201','谷上理規','たにうえりき',8,'10'),");
        sb_insert_test_ryosei.append("('9BFDDD7C-E781-44CC-980E-F80C5ED7D589','C201','山内景介','やまうちけいすけ',8,'10'),");
        sb_insert_test_ryosei.append("('D32D02CB-656A-4215-8631-B7DB31FC26C8','C201','長谷信孝','はせのぶたか',8,'10'),");
        sb_insert_test_ryosei.append("('95E04F9B-A204-48CD-996F-6966526FFA45','C201','千島龍汰','ちしまりゅうた',8,'10'),");
        sb_insert_test_ryosei.append("('902DFD65-436B-4208-9D09-51AEB3EDB217','C203','石田和生','いしだかずき',8,'10'),");
        sb_insert_test_ryosei.append("('AD902E0E-91AB-452C-B453-BF910B6A3993','C203','工藤由晶','くどうゆうしょう',8,'10'),");
        sb_insert_test_ryosei.append("('5DAAC8ED-3F8E-4529-A367-384479AC34E4','C203','カーンズ一早','カーンズかずさ',8,'10'),");
        sb_insert_test_ryosei.append("('3333BCFC-BCAD-4CA2-BE60-F6BCF9763D6F','C203','吉原総','よしはらそう',8,'10'),");
        sb_insert_test_ryosei.append("('12883680-8770-40E9-AF0C-3FFE300B031C','C203','吉田啓','よしだけい',8,'10'),");
        sb_insert_test_ryosei.append("('4DE9ED43-3884-4C66-B3F6-02FD76DDDDBF','C203','水口智貴','みずぐちともき',8,'10'),");
        sb_insert_test_ryosei.append("('1E83A4D5-733E-4FA8-9317-1D5A928429CC','C203','中川精華','なかがわせいか',8,'10'),");
        sb_insert_test_ryosei.append("('350591C1-D3BF-48D4-8938-60C061467011','C203','巌西純哉','いわにしじゅんや',8,'10'),");
        sb_insert_test_ryosei.append("('3469349D-9836-43B3-A0D5-3EB2B97B8A57','C203','福田胡桃','ふくだくるみ',8,'10'),");
        sb_insert_test_ryosei.append("('B25D93EE-8F9F-4F3D-BB09-A6D56DA8057C','C205','飯田千遥','いいだちはる',8,'10'),");
        sb_insert_test_ryosei.append("('3AEFE991-3624-45E2-9FFB-51996503FA1E','C205','宇野澤奈々華','うのさわななか',8,'10'),");
        sb_insert_test_ryosei.append("('CBD40A2F-37EE-47F8-8CD7-02F642986C57','C205','花坂光平','はなさかこうへい',8,'10'),");
        sb_insert_test_ryosei.append("('3241C817-67D2-4343-80CF-374FB803FECA','C205','押切真大','おしきりまこと',8,'10'),");
        sb_insert_test_ryosei.append("('EF52969A-F554-465D-A4F4-FC3129E3CE06','C205','神品芳孝','こうしなよしたか',8,'10'),");
        sb_insert_test_ryosei.append("('4EFB5B40-BEB9-4F76-BC74-067701356F5F','コンテナ102','長谷川槙也','はせがわしんや',10,'10'),");
        sb_insert_test_ryosei.append("('AF3391BD-E366-4D82-BC3B-DAE4EB8E8B23','コンテナ101','オブスターデニス','おぶすたーでにす',10,'10'),");
        sb_insert_test_ryosei.append("('A0362DF5-7562-4011-A8DD-EC6C415BC807','コンテナ101','千葉玲旺','ちばれお',10,'10'),");
        sb_insert_test_ryosei.append("('F7EA91A6-E767-4F03-B0EB-F1263559082F','図書室','大島直也','おおしまなおや',10,'10'),");
        sb_insert_test_ryosei.append("('7ED536E9-8B16-4A63-9BB5-31838AA3E1E4','C301','王雨','おうれいん',9,'10'),");
        sb_insert_test_ryosei.append("('4C765C17-F7CF-4C51-BD4A-8CA0355CABB2','C301','佐伯和香','さえきけんご',9,'10'),");
        sb_insert_test_ryosei.append("('7A8D95EB-4BB2-4B99-908B-2636B041C501','C301','上里叶子','うえざときょうこ',9,'10'),");
        sb_insert_test_ryosei.append("('DF5C64CC-F004-4F6B-924A-1E2D4D5D2BB3','C301','南沢想','みなみさわそう',9,'10'),");
        sb_insert_test_ryosei.append("('AF359DC6-A4C0-4F42-8C01-C877B0E78685','C301','長谷川美夏','はせがわみなつ',9,'10'),");
        sb_insert_test_ryosei.append("('04435347-7A09-4250-A8FF-7A8DF590967D','C302','岡田隼弥','おかだじゅんや',9,'10'),");
        sb_insert_test_ryosei.append("('30FA934D-B150-4694-92AA-F44A2669D289','C302','谷知晃','たにともあき',9,'10'),");
        sb_insert_test_ryosei.append("('3A4E6E60-961E-447B-BEB7-A040EB31E22D','C302','後藤翼','ごとうつばさ',9,'10'),");
        sb_insert_test_ryosei.append("('5D0E0F60-3DF6-42BA-9CB7-C4CA399A604E','C302','三上周一郎','みかみしゅういちろう',9,'10'),");
        sb_insert_test_ryosei.append("('7846BAD7-B447-473A-8F9D-41735EA3EB6D','C302','谷岡翔太','たにおかしょうた',9,'10'),");
        sb_insert_test_ryosei.append("('E1B3818A-8954-4AFA-A91D-AE6394D1AB67','C302','徐新源','じょしんげん',9,'10'),");
        sb_insert_test_ryosei.append("('4C7A42DA-DAFF-4679-BBFB-45CA8C8DC646','C303','黒田尚輝','くろだなおき',9,'10'),");
        sb_insert_test_ryosei.append("('4133DD33-351D-4E72-962A-C4537072D5AD','C303','黄偉軒','こういけん',9,'10'),");
        sb_insert_test_ryosei.append("('53D58313-B937-4AC5-B6E0-144962012198','C304','福士謙二','ふくしけんじ',9,'10'),");
        sb_insert_test_ryosei.append("('55CC1906-7160-40D8-BDD8-3EBBB2B3E7A6','C304','温井遥介','ぬくいようすけ',9,'10'),");
        sb_insert_test_ryosei.append("('09FB9262-BCE5-4051-A676-7F8D4B753A06','C304','大須賀舜','おおすがしゅん',9,'10'),");
        sb_insert_test_ryosei.append("('B4E3A62F-E207-4BE9-A964-87FCC68FFDAD','C304','西岡喜優','にしおかたかゆう',9,'10'),");
        sb_insert_test_ryosei.append("('7D72D200-667E-4CBC-82F1-FADF61F6C6B1','C304','小雲立也','おぐもたつや',9,'10'),");
        sb_insert_test_ryosei.append("('30338402-9F83-478D-AC53-A2376BC12117','C304','森口卓真','もりぐちたくま',9,'10'),");
        sb_insert_test_ryosei.append("('4CB8D5F6-D420-4D20-BF35-EE7116FE5476','C305','市来蓮大','いちきれんた',9,'10'),");
        sb_insert_test_ryosei.append("('501807AC-2677-4148-9B61-2F9610E5C557','C305','小坂哲平','こさかてっぺい',9,'10'),");
        sb_insert_test_ryosei.append("('91741805-A416-4956-97A6-02E8E759D87D','C305','筒井隼介','つついしゅんすけ',9,'10'),");
        sb_insert_test_ryosei.append("('C5A8ADE6-E512-471B-A154-F37B40CDD0A9','C305','杉野僚祐','すぎのりょうすけ',9,'10'),");
        sb_insert_test_ryosei.append("('92BD793A-110C-49CF-A55C-07E4660AEAEA','C305','渡辺天真','わたなべてんしん',9,'10'),");
        sb_insert_test_ryosei.append("('6703260B-1A87-4732-9E87-EF1ED5F3F6AD','C305','石田新一','いしだしんいち',9,'10'),");
        sb_insert_test_ryosei.append("('D00E4844-851B-4971-84E7-BBC013C2E57B','C401','荒川亮太','あらかわりょうた',9,'10'),");
        sb_insert_test_ryosei.append("('ECDDA49D-E038-4912-9069-180DFC51DC2B','C401','安波雅人','やすなみまさと',9,'10'),");
        sb_insert_test_ryosei.append("('B8508BFC-2D66-4257-BBEF-CD126596BC8C','C401','山田将矢','やまだまさや',9,'10'),");
        sb_insert_test_ryosei.append("('64029965-A83B-4116-83B0-C7D2C01E5F61','C401','江夏道晴','えなつみちはる',9,'10'),");
        sb_insert_test_ryosei.append("('EE7A2166-C0AC-48B2-A8E9-274B3C4B8141','C401','丹羽望','にわのぞむ',9,'10'),");
        sb_insert_test_ryosei.append("('81CAE79C-65C3-419D-A430-7C405C91B716','C401','岡崎凜','おかざきりん',9,'10'),");
        sb_insert_test_ryosei.append("('81A21A6C-2BAE-4028-A932-AC56F8AE2C9B','C402','井上海舟','いのうえかいしゅう',9,'10'),");
        sb_insert_test_ryosei.append("('335075FE-ACE1-4D5C-BE3E-D75A7AFA3627','C402','田中秀汰','たなかしゅうた',9,'10'),");
        sb_insert_test_ryosei.append("('E0A7644C-7A30-4B7C-9947-9727C7DDDCEF','C402','山本隼士','かねやまはやと',9,'10'),");
        sb_insert_test_ryosei.append("('3CE67C90-2029-4064-948B-824CEE436A1C','C402','島田悠生','しまだゆうせい',9,'10'),");
        sb_insert_test_ryosei.append("('CCCDE43C-BD4A-4342-92A8-E9A6D43729F1','C402','櫻井洸太','さくらいこうた',9,'10'),");
        sb_insert_test_ryosei.append("('65B3437E-8F6B-4A96-9B7C-98927D156F67','C402','森田歩','もりたあゆむ',9,'10'),");
        sb_insert_test_ryosei.append("('8F6546D4-21F1-457C-A17B-94848D50C6CC','C403','片桐潮士','かたぎりしおじ',9,'10'),");
        sb_insert_test_ryosei.append("('4BA40CD8-7F6F-4F4F-873A-4613CE52BA3E','C403','酒井竜太郎','さかいりゅうたろう',9,'10'),");
        sb_insert_test_ryosei.append("('CBF4A8D5-8061-43E3-98B6-EB3CF3E52BE2','C403','小林武史','こばやしたけし',9,'10'),");
        sb_insert_test_ryosei.append("('874A3F8E-C07F-4F51-A54E-8AD79B314EB4','C403','橋本竜馬','はしもとりょうま',9,'10'),");
        sb_insert_test_ryosei.append("('768B6780-29D1-41BE-B862-C5B0353BB9B3','C404','笠井遥','かさいはるか',9,'10'),");
        sb_insert_test_ryosei.append("('4BAEF281-3551-453E-BD1D-7F908F02A775','C404','丹澤優','たんざわすぐれ',9,'10'),");
        sb_insert_test_ryosei.append("('A63856E8-7ACC-4BF8-A4C0-75C293C31ED9','C404','杉谷佳琳','すぎたにかりん',9,'10'),");
        sb_insert_test_ryosei.append("('938C1692-4D00-4CEC-924C-7DEF092D1785','C404','西山香帆','にしやまかほ',9,'10'),");
        sb_insert_test_ryosei.append("('557FE2B3-2B6D-4C74-B5C2-1B3B02D9BA6C','C404','村上日菜子','むらかみひなこ',9,'10'),");
        sb_insert_test_ryosei.append("('2A867E4A-C3F5-42C9-968A-A9DE8A2A601D','C404','田中瑠莉','たなかるり',9,'10'),");
        sb_insert_test_ryosei.append("('258FE8D3-011C-4794-B5C6-9314C41F4F03','C404','中西萌','なかにしもえ',9,'10'),");
        sb_insert_test_ryosei.append("('7517CC04-C5BA-4728-AE01-A741C51353A2','図書室','上柿大','うえがきまさる',10,'10'),");
        sb_insert_test_ryosei.append("('7EBA958F-4787-4A9C-A4DB-342663DF1265','B201','安田淳敏','やすだあつとし',5,'10'),");
        sb_insert_test_ryosei.append("('09827ABB-2553-432A-96E8-D624BC7CF606','B201','金山隼','かねやまじゅん',5,'10'),");
        sb_insert_test_ryosei.append("('16ADF711-B64B-45AE-BD6C-2F5FC78050DA','B201','額田大晃','ぬかだひろあき',5,'10'),");
        sb_insert_test_ryosei.append("('975E5BAB-82A0-43CE-9ADF-90FE3F5B1B29','B201','伊庭達哉','いばたつや',5,'10'),");
        sb_insert_test_ryosei.append("('838674C3-BD41-4BBD-912C-C68A535C9F41','B201','夏晨嘯','しゃちぇんしゃお',5,'10'),");
        sb_insert_test_ryosei.append("('78C69728-2D60-4AC1-93C1-03FBF6763D56','B202','李在晟','いじぇそん',5,'10'),");
        sb_insert_test_ryosei.append("('DB441EF7-F82B-47AA-8974-D9FF9917296B','B202','佐々木駿斗','ささきはやと',5,'10'),");
        sb_insert_test_ryosei.append("('F9E828CB-84AC-4A5F-B7AD-18E5E4345BE7','B202','石川開','いしかわかい',5,'10'),");
        sb_insert_test_ryosei.append("('CF37E60C-F466-4AD4-A751-C02C578836A8','B202','小川哲也','おがわてつや',5,'10'),");
        sb_insert_test_ryosei.append("('40DA4BE1-797E-4BEB-BDCC-547098D73570','B202','安東星琉','あんどうせいりゅう',5,'10'),");
        sb_insert_test_ryosei.append("('97E8D32C-DA6B-44AB-B386-B4BA3E8891A1','B203','大湊浩明','おおみなとひろあき',5,'10'),");
        sb_insert_test_ryosei.append("('FCE1E757-F19B-4305-912C-EF0CC9B041C1','B203','坂田亮介','さかたりょうすけ',5,'10'),");
        sb_insert_test_ryosei.append("('FB1A3209-0EA4-47FF-AFC8-6C478F8B0098','B203','中村瞭佑','なかむらりょうすけ',5,'10'),");
        sb_insert_test_ryosei.append("('57555C2B-54CE-4F11-B03D-74D1E86F4D60','B203','河崎涼太','かわさきりょうた',5,'10'),");
        sb_insert_test_ryosei.append("('744785EA-E1A4-4CC4-B300-95582C7A27E0','B204','木戸口将也','きどぐちまさや',5,'10'),");
        sb_insert_test_ryosei.append("('B65FE5DA-4DBF-4CDD-9928-83A0A4D16F37','B204','國友芳虎','くにともよしとら',5,'10'),");
        sb_insert_test_ryosei.append("('986EA362-ACFD-4493-BAC4-E06282EE3994','B204','西峯寛人','にしみねひろと',5,'10'),");
        sb_insert_test_ryosei.append("('FC96B2D6-F085-4705-9DE1-000FCB5B15D8','B204','松本弥','まつもとわたる',5,'10'),");
        sb_insert_test_ryosei.append("('44768324-8B05-4436-BDBF-2ED75528726F','B205','酒井拓海','さかいたくみ',5,'10'),");
        sb_insert_test_ryosei.append("('88C22A74-1DFE-459D-9E73-01443AF06DCA','B205','日高颯哉','ひだかそうや',5,'10'),");
        sb_insert_test_ryosei.append("('D9EADE48-E2C5-4EC8-A987-E75B98776045','B205','原田尚紀','はらだなおき',5,'10'),");
        sb_insert_test_ryosei.append("('AC24DD8E-87E0-4338-AFAA-663C3B4593EC','B205','林和幸','はやしかずゆき',5,'10'),");
        sb_insert_test_ryosei.append("('B1C89B68-FB65-4148-A190-7272DA942FEF','B206','戴丹蕾','たいたんらい',5,'10'),");
        sb_insert_test_ryosei.append("('DB80F042-1CE1-4FB4-83FE-4C7F4DF0ADA5','B206','渡邊蘭子','わたなべらんこ',5,'10'),");
        sb_insert_test_ryosei.append("('5DDB60B8-1407-42B9-A732-CF15E5E926F7','B206','山本彩加','やまもとあやか',5,'10'),");
        sb_insert_test_ryosei.append("('FF77203F-3DF2-40C4-A785-F6A5B6A58ABF','B206','菅間あさ希','すがまあさき',5,'10'),");
        sb_insert_test_ryosei.append("('F6768859-D5ED-49D3-9DF2-8ED8D5F34118','B207','田邊萌夏','たなべもえか',5,'10'),");
        sb_insert_test_ryosei.append("('F23E4317-A3D2-4636-B014-5FB98AF8830F','B207','李萌','りもん',5,'10'),");
        sb_insert_test_ryosei.append("('49B4DC94-F288-4F9D-932D-EB14D2BA4922','B207','小澤史織','おざわしおり',5,'10'),");
        sb_insert_test_ryosei.append("('E07A31C2-6C3C-4B78-847D-47BF1CD73414','B207','藤井愛美','ふじいまなみ',5,'10'),");
        sb_insert_test_ryosei.append("('C9B97EA4-0293-48DA-B1C8-D61DFEDDFBC9','B208','堀本大輝','ほりもとだいき',5,'10'),");
        sb_insert_test_ryosei.append("('E714C3F0-67D7-4B46-B2F2-EF3D2C0C0ED1','B208','仲野比呂司','なかのひろし',5,'10'),");
        sb_insert_test_ryosei.append("('E715DAF2-DEDD-4D3F-8B75-6B60EF48992E','B208','李開理','りかいり',5,'10'),");
        sb_insert_test_ryosei.append("('6FF572D5-34C1-4B98-803F-49A9D1ACFD75','B208','永田遼太郎','ながたりょうたろう',5,'10'),");
        sb_insert_test_ryosei.append("('235D1B6A-CA3D-4476-A31D-2CBA2E215265','B209','河合敦紀','かわあいあつき',5,'10'),");
        sb_insert_test_ryosei.append("('ABD87D8D-9696-41CC-8A2F-9E2976E4147C','B209','野坂智章','のさかともあき',5,'10'),");
        sb_insert_test_ryosei.append("('9174C7E5-182F-4427-BE04-A879562161D3','B209','嵯峨祐天','さがゆうてん',5,'10'),");
        sb_insert_test_ryosei.append("('8A5C2974-FB1B-485F-BBEF-9E649C398A44','B209','浅岡正宗','あさおかまさむね',5,'10'),");
        sb_insert_test_ryosei.append("('4E7B8BE7-5946-4099-AAC1-CAD0FA3B3D04','B210','阿津良典','あづりょうすけ',5,'10'),");
        sb_insert_test_ryosei.append("('1E05498C-3116-4023-895D-750CDF7812FE','B210','魏恒','ぎこう',5,'10'),");
        sb_insert_test_ryosei.append("('5246A578-986F-43DD-8CB3-9D08DC0922C7','B210','福本夏生','ふくもとなつお',5,'10'),");
        sb_insert_test_ryosei.append("('EDD91C8E-AB5B-4A97-8E5A-910108FD42FE','B210','川口和将','かわぐちかずまさ',5,'10'),");
        sb_insert_test_ryosei.append("('C38E8E9B-B015-4002-963C-DB5016667930','B210','松葉一朗','まつばいちろう',5,'10'),");
        sb_insert_test_ryosei.append("('68756012-FB94-4D83-B48E-8817C1D4DCBD','B210','PANGYOUYUAN','ポンようゆょん',5,'10'),");
        sb_insert_test_ryosei.append("('98C9BA39-37DA-4FF2-A97A-46F883B566CB','B210','小野颯人','おのはやと',5,'10'),");
        sb_insert_test_ryosei.append("('95423E51-10F2-4735-A9AF-20C2A0BD60EE','B211','小林夕莉','こばやしゆうり',5,'10'),");
        sb_insert_test_ryosei.append("('EF5DC6EF-4760-4171-8D69-700A8543DB6A','B211','片山花菜','かたやまはな',5,'10'),");
        sb_insert_test_ryosei.append("('E6DDC74B-97F7-450B-A50A-9632ECF6A4E7','B211','飯森灯里','いいもりあかり',5,'10'),");
        sb_insert_test_ryosei.append("('ED1F5F11-DBD3-4C8A-A0F6-FAA743237011','B211','宮崎友','みやざきゆう',5,'10'),");
        sb_insert_test_ryosei.append("('9F5E2992-0CAB-4FD3-9198-808D5677C1FC','B212','佐々木涼','ささきりょう',5,'10'),");
        sb_insert_test_ryosei.append("('2668467F-8606-4654-A65D-4D304C0EE7BF','B212','宋円夢','そうえんむ',5,'10'),");
        sb_insert_test_ryosei.append("('DFC9DA80-99C3-4060-8C10-AFC40318DA94','B212','柴爽香','しばさやか',5,'10'),");
        sb_insert_test_ryosei.append("('0D01D1CD-8040-4F48-A993-C301CB918D22','B212','中村眞子','なかむらまこ',5,'10'),");
        sb_insert_test_ryosei.append("('121B9B22-9443-44A3-936C-504D7D991CB1','B212','稲垣美帆','いながきみほ',5,'10'),");
        sb_insert_test_ryosei.append("('0B5C7211-9F66-4E1E-94AD-5A8FCCD5798A','旧印','関田大介','せきだだいすけ',10,'10'),");
        sb_insert_test_ryosei.append("('3F7914D1-1776-4F46-8D9D-3D78BB71613B','図書室','有馬大樹','ありまたいじゅ',10,'10'),");
        sb_insert_test_ryosei.append("('2DC0E931-B8C3-424D-BD54-7EEBA27FBEB1','図書室','大坂健太','おおさかけんた',10,'10'),");
        sb_insert_test_ryosei.append("('F407F3B1-7E8E-4232-8E60-92444A09DA33','図書室','井芹徹大','いせりてった',10,'10'),");
        sb_insert_test_ryosei.append("('C20697A4-ECFD-440C-A32B-7B68BEE68CBA','図書室','西村航成','にしむらこうせい',10,'10'),");
        sb_insert_test_ryosei.append("('7475F67D-B714-41B2-94C2-A1F5B12279B6','図書室','加藤一樹','かとういつき',10,'10'),");
        sb_insert_test_ryosei.append("('2EBA9622-4EF2-4BEC-8EBC-CD9FA475C9D8','B301','陳玲','ちんれい',6,'10'),");
        sb_insert_test_ryosei.append("('57E2F3DD-B9C9-4A68-97E6-531861F0EB19','B301','米倉美咲','よねくらみさき',6,'10'),");
        sb_insert_test_ryosei.append("('FBAA85FD-35E5-4057-8651-D033F0347C9F','B301','小仲美齢','こなかみれい',6,'10'),");
        sb_insert_test_ryosei.append("('C5FEBA60-E4B6-4C2C-AD45-AB9D9BA31995','B302','三好美咲','みよしみさき',6,'10'),");
        sb_insert_test_ryosei.append("('04706E71-55C5-48C3-BA50-30161D3A8173','B302','江田香織','えだかおり',6,'10'),");
        sb_insert_test_ryosei.append("('11BA96CD-C655-468C-B460-EF792C8E8068','B302','篠原李欧','しのはらりおう',6,'10'),");
        sb_insert_test_ryosei.append("('F168210D-3441-4A2E-A009-D4E892F3A9C4','B302','郭健','かくけん',6,'10'),");
        sb_insert_test_ryosei.append("('950B391F-1E9B-4192-BB47-03473DC28E94','B303','千葉美香','ちばみか',6,'10'),");
        sb_insert_test_ryosei.append("('6451FC3B-A4AB-4FC5-98A5-0707427FDD33','B303','福重茜','ふくしげあかね',6,'10'),");
        sb_insert_test_ryosei.append("('95932A7C-D296-4E04-B4A6-F085492A57A6','B303','求思園','きゅうしえん',6,'10'),");
        sb_insert_test_ryosei.append("('B303BA7C-0D79-4C45-BDD9-44FF785E302C','B303','片山真由美','かたやままゆみ',6,'10'),");
        sb_insert_test_ryosei.append("('512BEC37-B03E-4B31-8F8A-A9033A24CDCF','B304','尾﨑衿子','おざきえりこ',6,'10'),");
        sb_insert_test_ryosei.append("('C4B0AC81-02B0-4552-8998-B7A264BC8CAD','B304','渡邊理香','わたなべりか',6,'10'),");
        sb_insert_test_ryosei.append("('90333D9C-B55F-492B-9473-7E99820989C7','B304','鄭怡','ていい',6,'10'),");
        sb_insert_test_ryosei.append("('52017076-72F5-4828-9698-2A63903E6AA5','B304','山崎嘉那子','やまざきかなこ',6,'10'),");
        sb_insert_test_ryosei.append("('EADBD59B-C194-4673-9D63-B6BED504D384','B305','窪田航','くぼたわたる',6,'10'),");
        sb_insert_test_ryosei.append("('2144AC48-43C7-4CE6-8256-BFDC4095A381','B305','石川朔太郎','いしかわさくたろう',6,'10'),");
        sb_insert_test_ryosei.append("('DF2BD36B-8B01-4247-89BF-0BCAFE1D21EA','B305','重田龍一','しげたりゅういち',6,'10'),");
        sb_insert_test_ryosei.append("('EA787650-A04C-4221-AF3C-DBB98783AAA8','B305','宅悠介','たくゆうすけ',6,'10'),");
        sb_insert_test_ryosei.append("('B313CFFE-C09A-4B77-8661-52611B3BFA9C','B306','山口惟乙','やまぐちいおと',6,'10'),");
        sb_insert_test_ryosei.append("('8F143FF2-DD27-4B40-AAFA-5D440874BAB5','B306','森脇瀬菜','もりわきせな',6,'10'),");
        sb_insert_test_ryosei.append("('06E7BBB0-D4DC-4BD8-83AB-5C7E9B185C73','B306','岡村浩平','おかむらこうへい',6,'10'),");
        sb_insert_test_ryosei.append("('DAB5C117-355F-4157-8179-510A92C146A2','B306','野田周英','のだしゅうえい',6,'10'),");
        sb_insert_test_ryosei.append("('53A5E0E8-CB91-4360-9669-40A26550B76D','B307','高田昌汰','たかたしょうた',6,'10'),");
        sb_insert_test_ryosei.append("('535C1877-278E-4B23-8C98-6F28E4A16B50','B307','高見侑磨','たかみゆうま',6,'10'),");
        sb_insert_test_ryosei.append("('05DE15D9-8ABA-49F2-B8B3-0BA31B0143DD','B307','汐除明','しおよけあきら',6,'10'),");
        sb_insert_test_ryosei.append("('752CEC36-984F-4FBF-B9E6-425AC7F4E2F8','B307','松澤宏道','まつざわこうどう',6,'10'),");
        sb_insert_test_ryosei.append("('E9E04811-CD3E-4E1E-A873-5B75621DA11C','B308','柄本耀介','えのもとようすけ',6,'10'),");
        sb_insert_test_ryosei.append("('4B1EE88A-E48D-42F5-B104-79D55979CEA4','B308','上平拓夢','うえひらたくむ',6,'10'),");
        sb_insert_test_ryosei.append("('0890FF82-A8F7-4556-827F-BF40DAB7AB18','B308','喜田吉紀','きだよしき',6,'10'),");
        sb_insert_test_ryosei.append("('1BC2590D-6853-4D2E-8FC0-050B7722D01D','B308','平岡寛星','ひらおかかんせい',6,'10'),");
        sb_insert_test_ryosei.append("('8E8ED31E-611D-4980-9049-AFE067D2566F','B309','本田大洋','ほんだたいよう',6,'10'),");
        sb_insert_test_ryosei.append("('F8B1E27B-ECFE-46EA-80C2-F8CE95E4527F','B309','北堯','きたたかし',6,'10'),");
        sb_insert_test_ryosei.append("('AE927AE4-008E-480D-95DB-CEA953A86A9C','B309','薬師神宏大','やくしじんこうだい',6,'10'),");
        sb_insert_test_ryosei.append("('F4908139-614B-45B0-A705-9D27D27FDA95','B309','中田拓海','なかたたくみ',6,'10'),");
        sb_insert_test_ryosei.append("('FB300763-1BB8-4443-81A4-57B303F307E5','B309','王作造','おうさくぞう',6,'10'),");
        sb_insert_test_ryosei.append("('CAEC8980-B45E-4B99-B96A-EE0F1CB148CB','B310','中島健雄','なかじまたけお',6,'10'),");
        sb_insert_test_ryosei.append("('55390759-F03F-496C-82AA-4E57E11538FE','B310','恒川平','つねかわたいら',6,'10'),");
        sb_insert_test_ryosei.append("('EA3D085E-2B8C-41B3-992D-2DDDCAAE414D','B310','吉田巖嗣','よしだがんじ',6,'10'),");
        sb_insert_test_ryosei.append("('8ED5E40F-F0A2-4EED-B67C-19B3A1C9C520','B310','神山水樹','かみやまみずき',6,'10'),");
        sb_insert_test_ryosei.append("('74E47CE5-F13F-4536-9D28-005173ACCF32','B310','長田太地','ながただいち',6,'10'),");
        sb_insert_test_ryosei.append("('FED44826-32BB-41D9-B55E-3CFAF58CDFE3','B311','四十坊広大','しじゅうぼうこうだい',6,'10'),");
        sb_insert_test_ryosei.append("('6AD7335A-28EC-42D4-9664-6711639B0089','B311','藤川貴司','ふじかわたかし',6,'10'),");
        sb_insert_test_ryosei.append("('7816377A-CEB8-4FEF-B6D2-D95DB5310945','B311','藤川太壱','ふじかわたいち',6,'10'),");
        sb_insert_test_ryosei.append("('86A23A6A-EB97-4694-B378-7A2FB65EADAF','B311','家田英明','いえだひであき',6,'10'),");
        sb_insert_test_ryosei.append("('AE4971D1-479D-4235-96E9-F69827154E07','B312','田下駿介','たしたしゅんすけ',6,'10'),");
        sb_insert_test_ryosei.append("('AB0B85B9-37B7-4EC9-A8E8-8F8FC1B9A7C3','B312','田中美士','たなかきよし',6,'10'),");
        sb_insert_test_ryosei.append("('78B6F31D-C7A7-4391-95D5-D1E7CA87CC14','B312','東浦丈生','ひがしうらたけお',6,'10'),");
        sb_insert_test_ryosei.append("('633581C4-E804-4D49-8934-626DD2CD70B1','B312','佐藤桂','さとうけい',6,'10'),");
        sb_insert_test_ryosei.append("('E9E95A10-9019-4418-A2B7-6026880976B8','C100','山戸康祐','やまとこうすけ',10,'10'),");
        */
        sb_insert_test_ryosei.append("('FCDA8CF3-ED0D-4078-831E-91EA2A7F4429','B地下踊り場','飯田駿介','いいだしゅんすけ',10,'10');");

        String sql_insert_test_ryosei = sb_insert_test_ryosei.toString();
        db.execSQL(sql_insert_test_ryosei);
        set_createdat_forTest(db);
    }

    void set_createdat_forTest(SQLiteDatabase db){
        //db.execSQL("update ryosei set created_at='2022-01-19 22:22:22.222',ryosei_name_alphabet='test',slack_id='testslack', last_event_id='testlastevid',last_event_datetime='2022-01-20 19:19:19.119', updated_at='2022-01-20 19:19:19.119';" );
        db.execSQL("update ryosei set created_at='2022-01-19 22:22:22.222';" );


    }



    public void addParcel(
            SQLiteDatabase db,
            String owner_uid,
            String owner_room,
            String owner_ryosei_name,
            String register_staff_uid,
            String register_staff_room_name,
            String register_staff_ryosei_name,
            int placement,
            String note) {
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


        nimotsuCountAdder(db, owner_uid);//ryoseiテーブルの更新
        event_add_touroku(db, string_register_time,uuid,owner_uid, owner_room, owner_ryosei_name, note);//eventテーブルの更新
    }

    /*
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
        event_add_touroku(db,string_register_time, uuid,owner_uid, owner_room, owner_ryosei_name);
    }
    */

    public void event_add_touroku(
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
                "sharing_status" +
                ") values (");

        sb_insert_Parcel.append(" \"" + uuid + "\",");
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
            sql = "SELECT *  FROM ryosei order by uid Limit 5";
        } else if (type == 10) {
            sql = "SELECT *  FROM ryosei where sharing_status = '10' order by uid Limit 5";
        } else if (type == 11) {
            sql = "SELECT *  FROM ryosei where sharing_status = '11' order by uid Limit 5";
        } else {
            sql = "SELECT *  FROM ryosei order by uid Limit 10";
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
            sql = "SELECT *  FROM parcels order by uid Limit 5";
        } else if (type == 10) {
            sql = "SELECT *  FROM parcels where sharing_status = '10' order by uid Limit 5";
        } else if (type == 11) {
            sql = "SELECT *  FROM parcels where sharing_status = '11' order by uid Limit 5";
        } else {
            sql = "SELECT *  FROM parcels order by uid Limit 10";
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
            sql = "SELECT *  FROM parcel_event order by uid Limit 50";
        } else if (type == 10) {
            sql = "SELECT *  FROM parcel_event where sharing_status = '10' order by uid Limit 50";
        } else if (type == 11) {
            sql = "SELECT *  FROM parcel_event where sharing_status = '11' order by uid Limit 50";
        } else {
            sql = "SELECT *  FROM parcel_event order by uid Limit 50";
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}



