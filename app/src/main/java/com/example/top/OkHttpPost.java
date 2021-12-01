package com.example.top;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

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

    public OkHttpPost(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    String json = "{\"name\":\"名前\", \"taxis\":\"分類\"}";
    //String json = "{\"name\": \"foo\", \"description\": \"bar\", \"price\": 1, \"tax\":1.1}";

    @Override
    protected String doInBackground(String... strings) {

        OkHttpClient client = new OkHttpClient();

//        String url = "http://httpbin.org/post";
        String url = "http://192.168.100.3:8080/parcel/create";
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
            String popup_msg = response.body().string();
            executor.execute(() -> {
                handler.post(() -> {
                    Toast.makeText(context, popup_msg, Toast.LENGTH_SHORT).show();
                });
            });
            return "Success";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        Log.d("Debug",str);
    }
}