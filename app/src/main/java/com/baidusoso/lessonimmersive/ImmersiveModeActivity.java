package com.baidusoso.lessonimmersive;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

public class ImmersiveModeActivity extends AppCompatActivity {

    final static String TAG = ImmersiveModeActivity.class.getSimpleName();

    float mDownX = 0;
    float mDownY = 0;

    public static void setSystemUiVisibility(Activity activity, boolean enterFullscreen) {
        if (activity == null) {
            return;
        }
        View decor = activity.getWindow().getDecorView();
        if (enterFullscreen) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        /* place the window within the entire screen, ignoring
         *  decorations around the border (such as the status bar).*/
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window window = activity.getWindow();
        // Translucent status bar
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // Translucent navigation bar
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        int systemUiVisibility = decor.getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (enterFullscreen) {
            systemUiVisibility |= flags;
        } else {
            systemUiVisibility &= ~flags;
        }
        decor.setSystemUiVisibility(systemUiVisibility);
    }

    public static boolean isFullScreenActivity(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) > 0;
        }
        View decor = activity.getWindow().getDecorView();
        int systemUiVisibility = decor.getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        return (systemUiVisibility & flags) == flags;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immersive_mode);
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(this::onSystemUiVisibilityChange);
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(this::onGlobalLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSystemUiVisibility(this, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mDownX = event.getRawX();
            mDownY = event.getRawY();
        }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            float upX = event.getRawX();
            float upY = event.getRawY();
            int touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
            if (Math.abs(upX - mDownX) < touchSlop && Math.abs(upY - mDownY) < touchSlop) {
                setSystemUiVisibility(this, false);
                getWindow().getDecorView().removeCallbacks(this::hideSystemUi);
                getWindow().getDecorView().postDelayed(this::hideSystemUi, 3000);
            }
        }
        return true;
    }

    void onSystemUiVisibilityChange(int visibility) {
        Log.d(TAG, "visibility=" + visibility);
    }

    void onGlobalLayout() {
        Log.d(TAG, "isFullScreenActivity=" + isFullScreenActivity(ImmersiveModeActivity.this));
    }

    void hideSystemUi() {
        setSystemUiVisibility(ImmersiveModeActivity.this, true);
    }
}
