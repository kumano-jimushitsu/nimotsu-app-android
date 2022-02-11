package com.example.top;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

    public class ButteryChecker  extends AsyncTask<Integer, Integer, Integer> {

        private Listener listener;

        // 非同期処理
        @Override
        protected Integer doInBackground(Integer... params) {

            // 10秒数える処理
            do{
                try {
                    //　1sec sleep
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Log.d("debug",""+params[0]);
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Context context = MainActivity.getAppContext();
                Intent batteryStatus = context.registerReceiver(null, ifilter);
                // Are we charging / charged?
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryPct = level * 100 / (float) scale;

                status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;
                level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                batteryPct = level * 100 / (float) scale;
                if(isCharging==true) {
                    params[0]=0;
                }else {
                    // 途中経過を返す
                    params[0]++;
                    if(params[0]==200){
                        params[0]=0;
                    }
                }
                publishProgress(params[0]);
            }while(params[0]<600);

            return params[0] ;
        }

        // 途中経過をメインスレッドに返す
        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (listener != null) {
                listener.onSuccess(progress[0]);
            }
        }

        // 非同期処理が終了後、結果をメインスレッドに返す
        @Override
        protected void onPostExecute(Integer result) {
            if (listener != null) {
                listener.onSuccess(result);
            }
        }


        void setListener(Listener listener) {
            this.listener = listener;
        }

        interface Listener {
            void onSuccess(int count);
        }
    }