package com.example.customview.hmm1;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.customview.R;

public class MyService extends Service implements View.OnTouchListener {
    private float dX, dY;
    private float dXMax, dYMAX;
    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;
    ControllerView controllerView;
    public static final String CHANNEL_ID = "record_channel";
    private static CharSequence CHANNEL_NAME = "record_channel_name";
    public static final int NOTIFICATION_ID = 1;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, sendNotificationDefault(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        } else {
            startForeground(NOTIFICATION_ID, sendNotificationDefault());
        }

        initViewOverlay();
        return START_STICKY;
    }

    private void initViewOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        createIconView();
        showIcon();
    }

    private void showIcon() {

    }

    private void createIconView() {
        controllerView=new ControllerView(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
//         dXMax = displayMetrics.widthPixels;
//         dYMAX = displayMetrics.heightPixels;


        layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
//        layoutParams.gravity=Gravity.CENTER;

        layoutParams.flags=WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParams.flags|=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        layoutParams.flags|=WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
//        layoutParams.flags|=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;




        windowManager.addView(controllerView,layoutParams);

//        view.setOnTouchListener(this);
//        controllerView.setOnTouchListener(this);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Notification sendNotificationDefault() {
        createNotificationChannel();


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSound(null)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_camera)
                .setContentTitle("hahahah")
                .setContentText("Đang chạy...")
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManagerCompat.notify(NOTIFICATION_ID, notification);
        }
        return notification;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.performClick();
                // Lấy vị trí chạm ban đầu
                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // Di chuyển View theo vị trí chạm mới
                v.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();
                break;
            case MotionEvent.ACTION_UP:

                // Di chuyển View theo vị trí chạm mới
                if (event.getRawX()>=dXMax/2){
                    v.animate()
                            .x(dXMax-200)
                            .y(event.getRawY() + dY)
                            .setDuration(300)
                            .start();
                }else {
                    v.animate()
                            .x(0)
                            .y(event.getRawY() + dY)
                            .setDuration(300)
                            .start();
                }
                break;

            default:
                return false;
        }
        return true;

    }
}
