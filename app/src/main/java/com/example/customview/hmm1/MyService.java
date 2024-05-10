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
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.customview.R;

public class MyService extends Service implements View.OnTouchListener {
    private static final int CONTROLLER_SELECTING = 1, CONTROLLER_NON_SELECT = 2;
    private static final int LEFT = 1, RIGHT = 2, TOP = 3, BOTTOM = 4;
    private static int CONTROLLER_STATE_POSITION = LEFT;
    private static int CONTROLLER_STATE = 2;
    int dXMax;
    int dYMAX;
    int initialX;
    int initialY;
    int initialTouchX;
    int initialTouchY;
    RelativeLayout rlOverlay;
    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams, layoutParams1, layoutParams2, layoutParams3, layoutParams4;
    ControllerView controllerView;
    private ControllerView controllerView1, controllerView2, controllerView3, controllerView4;
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
        createLayoutParamsView();
    }

    private void createLayoutParamsView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        dXMax = displayMetrics.widthPixels;
        dYMAX = displayMetrics.heightPixels;
        initControllerParams();
        initRlOverlayParams();
        initOptionController();

        initOnClick();

    }

    private void initOptionController() {
        controllerView1 = new ControllerView(this);
        controllerView2 = new ControllerView(this);
        controllerView3 = new ControllerView(this);
        controllerView4 = new ControllerView(this);

        //layout param1
        layoutParams1 = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );

        layoutParams1.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams1.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams1.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            layoutParams1.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }
        layoutParams1.gravity = Gravity.TOP | Gravity.START;
        layoutParams1.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams1.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        //layout param2
        layoutParams2 = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );

        layoutParams2.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams2.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            layoutParams2.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }

        layoutParams2.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams2.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams2.gravity = Gravity.TOP | Gravity.START;

        //layout params3
        layoutParams3 = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );

        layoutParams3.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams3.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams3.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            layoutParams3.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }

        layoutParams3.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams3.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams3.gravity = Gravity.TOP | Gravity.START;

        //layout params 4
        layoutParams4 = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );

        layoutParams4.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams4.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams4.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            layoutParams4.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }

        layoutParams4.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams4.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams4.gravity = Gravity.TOP | Gravity.START;

    }

    private void initRlOverlayParams() {


        /*controllerView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dfdf", "onClick: option1" );
            }
        });
        controllerView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dfdf", "onClick: option2" );

            }
        });
        controllerView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dfdf", "onClick: option3" );

            }
        });
        controllerView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dfdf", "onClick: option4" );

            }
        });*/

    }

    private void initControllerParams() {
        controllerView = new ControllerView(this);
        layoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );

        layoutParams.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }

        layoutParams.gravity = Gravity.TOP | Gravity.START;

        layoutParams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        windowManager.addView(controllerView, layoutParams);
        controllerView.setOnTouchListener(this);
    }

    private void initOnClick() {
        controllerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CONTROLLER_STATE == CONTROLLER_NON_SELECT) {
                    CONTROLLER_STATE = CONTROLLER_SELECTING;
                    rlOverlay = new RelativeLayout(MyService.this);
                    WindowManager.LayoutParams layoutParamsOverlay = new WindowManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                            PixelFormat.TRANSLUCENT);
                    layoutParamsOverlay.format = PixelFormat.TRANSLUCENT;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        layoutParamsOverlay.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                    } else {
                        layoutParamsOverlay.type = WindowManager.LayoutParams.TYPE_PHONE;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        layoutParamsOverlay.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
                    }
                    layoutParamsOverlay.gravity = Gravity.TOP | Gravity.START;
                    layoutParamsOverlay.alpha = 0.5F;
                    layoutParamsOverlay.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                    layoutParamsOverlay.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                    rlOverlay.setBackgroundColor(getResources().getColor(R.color.black));


                    windowManager.removeView(controllerView);
                    windowManager.addView(rlOverlay, layoutParamsOverlay);
                    windowManager.addView(controllerView, layoutParams);

                    if (CONTROLLER_STATE_POSITION==LEFT){
                        layoutParams1.x = layoutParams.x;
                        layoutParams1.y = layoutParams.y - 200;
                        windowManager.addView(controllerView1, layoutParams1);

                        layoutParams2.x = (int) (layoutParams.x + (200 * ((Math.sqrt(2) / 2))));
                        layoutParams2.y = (int) (layoutParams.y - (200 * ((Math.sqrt(2) / 2))));

                        windowManager.addView(controllerView2, layoutParams2);

                        layoutParams3.x = (int) (layoutParams.x + (200 * ((Math.sqrt(2) / 2))));
                        layoutParams3.y = (int) (layoutParams.y + (200 * ((Math.sqrt(2) / 2))));

                        windowManager.addView(controllerView3, layoutParams3);

                        layoutParams4.x = layoutParams.x;
                        layoutParams4.y = (int) (layoutParams.y + 200);

                        windowManager.addView(controllerView4, layoutParams4);
                    }else {
                        layoutParams1.x = layoutParams.x;
                        layoutParams1.y = layoutParams.y - 200;
                        windowManager.addView(controllerView1, layoutParams1);

                        layoutParams2.x = (int) (layoutParams.x + (200 * ((Math.sqrt(2) / 2))));
                        layoutParams2.y = (int) (layoutParams.y - (200 * ((Math.sqrt(2) / 2))));

                        windowManager.addView(controllerView2, layoutParams2);

                        layoutParams3.x = (int) (layoutParams.x + (200 * ((Math.sqrt(2) / 2))));
                        layoutParams3.y = (int) (layoutParams.y + (200 * ((Math.sqrt(2) / 2))));

                        windowManager.addView(controllerView3, layoutParams3);

                        layoutParams4.x = layoutParams.x;
                        layoutParams4.y = (int) (layoutParams.y + 200);

                        windowManager.addView(controllerView4, layoutParams4);
                    }



                } else {
                    CONTROLLER_STATE = CONTROLLER_NON_SELECT;
                    windowManager.removeView(rlOverlay);
                    windowManager.removeView(controllerView1);
                    windowManager.removeView(controllerView2);
                    windowManager.removeView(controllerView3);
                    windowManager.removeView(controllerView4);

                }

                rlOverlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CONTROLLER_STATE = CONTROLLER_NON_SELECT;
                        windowManager.removeView(rlOverlay);
                        windowManager.removeView(controllerView1);
                        windowManager.removeView(controllerView2);
                        windowManager.removeView(controllerView3);
                        windowManager.removeView(controllerView4);
                    }
                });
            }
        });


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
                initialX = layoutParams.x;
                initialY = layoutParams.y;
                initialTouchX = (int) event.getRawX();
                initialTouchY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getRawX() >= dXMax / 2) {
                    CONTROLLER_STATE_POSITION=RIGHT;
                    layoutParams.x = dXMax - v.getWidth();
                    windowManager.updateViewLayout(controllerView, layoutParams);
                } else {
                    CONTROLLER_STATE_POSITION=LEFT;
                    layoutParams.x =1;
                    windowManager.updateViewLayout(controllerView, layoutParams);

                }
//                    if (event.getRawY()>=dYMAX/2){
//                        if (event.getRawY()-(dYMAX/2)<event.getRawX()-(dXMax/2)){
//                            layoutParams.y=dYMAX;
//                            windowManager.updateViewLayout(controllerView, layoutParams);
//                        }else {
//                            layoutParams.x= dXMax-v.getWidth();
//                            windowManager.updateViewLayout(controllerView, layoutParams);
//                        }
//                    }else {
//                        if (event.getRawY()<=event.getRawX()-(dXMax/2)){
//                            layoutParams.y=1;
//                            windowManager.updateViewLayout(controllerView, layoutParams);
//                        }else {
//                            layoutParams.x=dXMax-v.getWidth();
//                            windowManager.updateViewLayout(controllerView, layoutParams);
//                        }
//                    }
//                } else {
//
//                    if (event.getRawY()>=dYMAX/2){
//                        Log.e("dfdf", "onTouch: left bot" );
//                        if (((dYMAX)-event.getRawY())<event.getRawX()){
//                            layoutParams.y=dYMAX-v.getHeight();
//                            windowManager.updateViewLayout(controllerView, layoutParams);
//                        }else {
//                            layoutParams.x= 1;
//                            windowManager.updateViewLayout(controllerView, layoutParams);
//                        }
//                    }else {
//                        Log.e("dfdf", "onTouch: left tp[" );
//
//                        if ((event.getRawY())<event.getRawX()){
//                            layoutParams.y=1;
//                            windowManager.updateViewLayout(controllerView, layoutParams);
//                        }else {
//                            layoutParams.x=1;
//                            windowManager.updateViewLayout(controllerView, layoutParams);
//                        }
//
                break;
            case MotionEvent.ACTION_MOVE:
                layoutParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                layoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                windowManager.updateViewLayout(controllerView, layoutParams);
                break;
        }
        return false;

    }
}
