package com.example.top.ClickListener;

import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;

public abstract class OnOneItemClickListener implements AdapterView.OnItemClickListener {
    private static final long MIN_CLICK_INTERVAL = 400; //in millis
    private long lastClickTime = 0;

    @Override
    public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime > MIN_CLICK_INTERVAL) {
            lastClickTime = currentTime;
            onOneItemClick(parent, view, position, id);
        }
    }

    public abstract void onOneItemClick(AdapterView<?> parent, View view, int position, long id);
}