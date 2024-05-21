package com.example.customview.hmm.bubbletrashview;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.customview.R;
import com.example.customview.hmm1.ControllerView;

public class BubbleService extends Service {
    public static final String EXTRA_CUTOUT_SAFE_AREA = "cutout_safe_area";
    private static final int BUBBLE_OVER_MARGIN = dp2px(8);

    private boolean isInit;
    private BubblesManager bubblesManager;
    private ControllerView controllerView;
    private WindowManager.LayoutParams layoutParams;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isOverlayGranted(this)){
            if (!isInit) {
                Log.e("dfdf", "onStartCommand: hmmm" );
                isInit = true;
                bubblesManager = new BubblesManager(this);
                bubblesManager.setSafeInsetRect((Rect) intent.getParcelableExtra(EXTRA_CUTOUT_SAFE_AREA));

                BubblesManager.Options options = new BubblesManager.Options();
                options.overMargin = BUBBLE_OVER_MARGIN;
                options.initX = -BUBBLE_OVER_MARGIN;
                options.initY = 150;
                options.floatingViewWidth = dp2px(80);
                options.floatingViewHeight = dp2px(80);
                options.onClickListener = v -> Log.e("ddd", "onStartCommand: Clicked");

                View mView;
//                mView = getBubbleView(this);
//                bubblesManager.addBubble(mView, options);
                options.bubbleRemoveListener = this::stopSelf;
                mView = getBubbleView(this);
                bubblesManager.addBubble(mView, options);
            }
        }
        return START_STICKY;
    }

    private void addBubbleView() {

//        controllerView = new ControllerView(this);
//
//        layoutParams = new WindowManager.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                PixelFormat.TRANSLUCENT
//        );
//
//        layoutParams.format = PixelFormat.TRANSLUCENT;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
//        }
//
//        layoutParams.gravity = Gravity.TOP | Gravity.START;
//
//        layoutParams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
//        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

//        controllerView.setOnTouchListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isInit = false;
        if (bubblesManager != null) {
            bubblesManager.dispose();
            bubblesManager = null;
        }
    }

    @NonNull
    private ImageView getBubbleView(Context context) {
        int padding = dp2px(8);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.ic_avatar);
        imageView.setPadding(padding, padding, padding, padding);
        return imageView;
    }

    static int dp2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    static boolean isOverlayGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }
}
