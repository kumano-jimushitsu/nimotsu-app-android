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
    String json;
    String url = "http://192.168.100.95:8080";
    Context context;
    Handler handler;
    SQLiteDatabase db;
    private Listener listener;
    private DatabaseHelper helper;
    private String method;

    public OkHttpPost(Context context, Handler handler, String json, SQLiteDatabase db, DatabaseHelper helper, String method) {
        super();
        this.context = context;
        this.handler = handler;
        this.json = json;
        this.db = db;
        this.helper = helper;
        this.method = method;
    }

    @Override
    protected synchronized String doInBackground(String... strings) {

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("", json)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String sqlCommand = response.body().string();
            
            /*
            * sqlCommand=リクエストを送るとPCから送られてくる文字列
            * これが空文字列ならOkHttpPostは二回目のリクエストを送らずnullを返す(no op)
            * */
            if(sqlCommand.equals("")) {
                if (this.listener != null) {
                    listener.onReceiveResponseFromPC("");
                    helper.updateSharingStatus(db, method);
                }
                return null;
            }

            try {
                db.execSQL(sqlCommand);
                helper.updateSharingStatusFromPC(db, method);
                listener.onReceiveResponseFromPC("Success");
            } catch (SQLException e) {
                Log.e("ERROR", e.toString());
                listener.onReceiveResponseFromPC("");
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onReceiveResponseFromPC(String res);
    }
}