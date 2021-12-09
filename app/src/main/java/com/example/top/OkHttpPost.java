package com.example.top;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpPost extends AsyncTask<String,String,String> {
    Context context;
    Handler handler;
    private Listener listener;
    private DatabaseHelper _helper;
    public OkHttpPost(Context context, Handler handler) {//コンテキスト　呼び出し元と　非同期処理をメインスレッドから飛ばしてここで行っている。
        this.context = context;
        this.handler = handler;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    String json = "{\"name\":\"名前\", \"taxis\":\"分類\"}";
    String url="http://192.168.100.3:8080/";
    //String json = "{\"name\": \"foo\", \"description\": \"bar\", \"price\": 1, \"tax\":1.1}";
    String test = "success";

    @Override
    protected synchronized String doInBackground(String... strings) {

        OkHttpClient client = new OkHttpClient();
//        String url = "http://127.0.0.1:8000/items/";
//        RequestBody body = RequestBody.create(JSON, json);
        RequestBody formBody = new FormBody.Builder()
                .add("", json)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Response response = client.newCall(request).execute();
            // PCからのメッセージをポップアップで表示する
            String popup_msg = response.body().string();//HTTPリクエストを出してそのリスポンスを個々で取得している。responce型が実行されると中身が入ってる
            if(popup_msg=="")return null;
            executor.execute(() -> {//あろう関数　関数表記を単純にしたもの
                handler.post(() -> {
//                    Toast.makeText(context, popup_msg, Toast.LENGTH_SHORT).show();
                    _helper = new DatabaseHelper(context);
                    SQLiteDatabase db = _helper.getWritableDatabase();
                    try {
                        db.execSQL(popup_msg);
                        _helper.update_sharingstatus_parcels(db);
                    } catch (SQLException e) {
                        Log.e("ERROR", e.toString());
                    }

                });
            });

//            if (listener != null) {
//                listener.ok(url);
//                //listener.onSuccess(progress[0]);
//            }

            return "Success";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onSuccess(result);
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        String onSuccess(String res);
    }
}