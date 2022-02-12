package com.example.top;

import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityHelper {
    static public void enableTransparentFooter(AppCompatActivity activity) {
        // システムナビゲーションバーの色を変更
        Window window = activity.getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        int colorId = activity.getResources().getColor(R.color.default_theme);
        window.setNavigationBarColor(colorId);
    }
}
