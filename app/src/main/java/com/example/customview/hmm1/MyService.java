package com.example.customview.hmm1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.divyanshu.draw.widget.DrawView;
import com.example.customview.R;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

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
    WindowManager.LayoutParams layoutParams, layoutParams1, layoutParams2, layoutParams3, layoutParams4, drawControllerLayoutParam,colorPickerLayoutParam;
    DrawControllerView drawControllerView;
    ControllerView controllerView;
    private ActionView controllerView1, controllerView2, controllerView3, controllerView4;
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
        initOptionController();
        initDrawController();
        initColorPickerParams();
        initOnClick();
    }

    private void initColorPickerParams() {
        colorPickerLayoutParam = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );

        colorPickerLayoutParam.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            colorPickerLayoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            colorPickerLayoutParam.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            colorPickerLayoutParam.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }
        colorPickerLayoutParam.gravity = Gravity.BOTTOM;
        colorPickerLayoutParam.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        colorPickerLayoutParam.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

    }


    @SuppressLint("ClickableViewAccessibility")
    private void initDrawController() {
        drawControllerView = new DrawControllerView(this);

        drawControllerLayoutParam = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );

        drawControllerLayoutParam.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            drawControllerLayoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            drawControllerLayoutParam.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            drawControllerLayoutParam.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }
        drawControllerLayoutParam.gravity = Gravity.TOP | Gravity.START;
        drawControllerLayoutParam.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        drawControllerLayoutParam.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        drawControllerLayoutParam.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;

        drawControllerView.getImgCancel().setOnClickListener(v -> {
            windowManager.removeView(drawControllerView);
        });
        drawControllerView.getImgUndo().setOnClickListener(v -> {
            drawControllerView.getDrawView().undo();
        });
        drawControllerView.getImgRedo().setOnClickListener(v -> {
            drawControllerView.getDrawView().redo();

        });
        drawControllerView.getImgEraser().setOnClickListener(v -> {
            drawControllerView.getDrawView().clearCanvas();

        });
        drawControllerView.getImgMultiColor().setOnClickListener(v -> {
            LayoutInflater layoutInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view=layoutInflater.inflate(R.layout.layout_bottom_sheet_color_picker, null);
            windowManager.addView(view,colorPickerLayoutParam);

            initViewColorPicker(view);

        });
        drawControllerView.getImgPenSize().setOnClickListener(v -> {

        });



    }

    private void initViewColorPicker(View view) {
        View colorRed,colorYellow,colorGreen,colorCyan,colorViolet,colorWhite,colorBlack;
        ColorPickerView colorPicker;
        ImageView multiColor;
        multiColor=view.findViewById(R.id.view_color_picker);
        colorRed=view.findViewById(R.id.view_color_red);
        colorYellow=view.findViewById(R.id.view_color_yellow);
        colorGreen=view.findViewById(R.id.view_color_green);
        colorCyan=view.findViewById(R.id.view_color_cyan);
        colorViolet=view.findViewById(R.id.view_color_violet);
        colorWhite=view.findViewById(R.id.view_color_white);
        colorBlack=view.findViewById(R.id.view_color_black);
        colorPicker=view.findViewById(R.id.color_picker_view);

        colorRed.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        colorYellow.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
        colorGreen.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        colorCyan.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
        colorViolet.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
        colorWhite.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        colorBlack.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

        multiColor.setImageResource(R.drawable.ic_multi_color);

        colorPicker.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {

            }
        });

    }

    private void initOptionController() {
        controllerView1 = new ActionView(this);
        controllerView2 = new ActionView(this);
        controllerView3 = new ActionView(this);
        controllerView4 = new ActionView(this);

        controllerView1.setImage(R.drawable.ic_record);
        controllerView2.setImage(R.drawable.ic_face_cam);
        controllerView3.setImage(R.drawable.ic_screen_shot);
        controllerView4.setImage(R.drawable.ic_brush);

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

                    if (CONTROLLER_STATE_POSITION == LEFT) {
                        layoutParams1.x = layoutParams.x;
                        layoutParams1.y = layoutParams.y - 200;
                        windowManager.addView(controllerView1, layoutParams1);

                        layoutParams2.x = (int) (layoutParams.x + (200 * ((Math.sqrt(3) / 2))));
                        layoutParams2.y = (int) (layoutParams.y - (200 * ((Math.sqrt(3) / 2))));

                        windowManager.addView(controllerView2, layoutParams2);

                        layoutParams3.x = (int) (layoutParams.x + (200 * ((Math.sqrt(3) / 2))));
                        layoutParams3.y = (int) (layoutParams.y + (200 * ((Math.sqrt(3) / 2))));

                        windowManager.addView(controllerView3, layoutParams3);

                        layoutParams4.x = layoutParams.x;
                        layoutParams4.y = (int) (layoutParams.y + 200);

                        windowManager.addView(controllerView4, layoutParams4);

                    } else {
                        layoutParams1.x = layoutParams.x;
                        layoutParams1.y = layoutParams.y - 200;
                        windowManager.addView(controllerView1, layoutParams1);

                        layoutParams2.x = (int) (layoutParams.x - (200 * ((Math.sqrt(3) / 2))));
                        layoutParams2.y = (int) (layoutParams.y - (200 * ((Math.sqrt(3) / 2))));

                        windowManager.addView(controllerView2, layoutParams2);

                        layoutParams3.x = (int) (layoutParams.x - (200 * ((Math.sqrt(3) / 2))));
                        layoutParams3.y = (int) (layoutParams.y + (200 * ((Math.sqrt(3) / 2))));

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
        controllerView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dfdf", "onClick: option1");
            }
        });
        controllerView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dfdf", "onClick: option2");

            }
        });
        controllerView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dfdf", "onClick: option3");

            }
        });
        controllerView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CONTROLLER_STATE = CONTROLLER_NON_SELECT;
                windowManager.removeView(rlOverlay);
                windowManager.removeView(controllerView1);
                windowManager.removeView(controllerView2);
                windowManager.removeView(controllerView3);
                windowManager.removeView(controllerView4);
                windowManager.addView(drawControllerView, drawControllerLayoutParam);

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
        int id = v.getId();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = layoutParams.x;
                    initialY = layoutParams.y;
                    initialTouchX = (int) event.getRawX();
                    initialTouchY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (event.getRawX() >= dXMax / 2) {
                        CONTROLLER_STATE_POSITION = RIGHT;
                        layoutParams.x = dXMax - v.getWidth();
                        windowManager.updateViewLayout(controllerView, layoutParams);
                    } else {
                        CONTROLLER_STATE_POSITION = LEFT;
                        layoutParams.x = 1;
                        windowManager.updateViewLayout(controllerView, layoutParams);
                    }
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
