package com.example.customview.hmm1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;

import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;
import com.example.customview.R;
import com.otaliastudios.cameraview.gesture.Gesture;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class MyService extends LifecycleService implements View.OnTouchListener {
    private static final int COLOR_RED = 1, COLOR_YELLOW = 2, COLOR_GREEN = 3, COLOR_BLUE = 4, COLOR_WHITE = 5, COLOR_BLACK = 6, COLOR_CYAN = 7, COLOR_PICKER = 8;
    private static int CURRENT_COLOR_DRAW = COLOR_BLACK;
    private static int PRE_COLOR = COLOR_BLACK;
    private static final int CONTROLLER_SELECTING = 1, CONTROLLER_NON_SELECT = 2;
    private static final int LEFT = 1, RIGHT = 2, TOP = 3, BOTTOM = 4;
    private static int CONTROLLER_STATE_POSITION = LEFT;
    private static int CONTROLLER_STATE = 2;
    private BottomSheetColorPicker bottomSheetColorPicker;
    private BottomSheetPenSize bottomSheetPenSize;
    private BottomSheetOpacity bottomSheetOpacity;
    private CustomCameraView customCameraView;
    int deltaX;
    float lastX;
    float lastY;
    int deltaY;

    int dXMax;
    int dYMAX;
    int initialX;
    int initialY;
    int initialTouchX;
    int initialTouchY;
    public static Window window;
    RelativeLayout rlOverlay;
    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams, layoutParams1, layoutParams2, layoutParams3, layoutParams4, drawControllerLayoutParam, colorPickerLayoutParam,cameraLayoutParams;
    DrawControllerView drawControllerView;
    ControllerView controllerView;
    private ActionView controllerView1, controllerView2, controllerView3, controllerView4;
    public static final String CHANNEL_ID = "record_channel";
    private static CharSequence CHANNEL_NAME = "record_channel_name";
    public static final int NOTIFICATION_ID = 1;

    private BroadcastReceiver mBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            ScreenShot.takeScreenshot(context,);

        }
    } ;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, sendNotificationDefault(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        } else {
            startForeground(NOTIFICATION_ID, sendNotificationDefault());
        }

        initViewOverlay();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter("hahaha");
        registerReceiver(mBR,intentFilter);

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
        if (drawControllerView==null){
            drawControllerView = new DrawControllerView(this);
            drawControllerView.getDrawView().setDrawColor(Color.BLACK);
        }

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
            drawControllerView.getDrawView().setDrawingMode(DrawingMode.ERASER);
        });
        drawControllerView.getImgMultiColor().setOnClickListener(v -> {
            if (bottomSheetColorPicker==null){
                Context context = new ContextThemeWrapper(this, R.style.Theme_Customview);
                bottomSheetColorPicker = new BottomSheetColorPicker(context);
                bottomSheetColorPicker.show();
                initViewColorPicker(bottomSheetColorPicker);
            }else {
                bottomSheetColorPicker.show();
            }
        });
        drawControllerView.getImgPenSize().setOnClickListener(v -> {
//            drawControllerView.getDrawView().setDrawWidth(100);
            if (bottomSheetPenSize==null){
                Context context = new ContextThemeWrapper(this, R.style.Theme_Customview);
                bottomSheetPenSize = new BottomSheetPenSize(context);
                bottomSheetPenSize.show();
                initViewPenSize(bottomSheetPenSize);
            }else {
                bottomSheetPenSize.show();
            }
        });

        drawControllerView.getImgBrush().setOnClickListener(v -> {
            drawControllerView.getDrawView().setDrawingMode(DrawingMode.DRAW);
            drawControllerView.getDrawView().setDrawingTool(DrawingTool.PEN);

        });
        drawControllerView.getImgRectangle().setOnClickListener(v -> {
            drawControllerView.getDrawView().setDrawingTool(DrawingTool.RECTANGLE);

        });
        drawControllerView.getImgCircle().setOnClickListener(v -> {
            drawControllerView.getDrawView().setDrawingTool(DrawingTool.CIRCLE);
        });
        drawControllerView.getImgClean().setOnClickListener(v -> {
            drawControllerView.getDrawView().restartDrawing();
        });
        drawControllerView.getImgAlpha().setOnClickListener(v -> {
            if (bottomSheetOpacity==null){
                Context context = new ContextThemeWrapper(this, R.style.Theme_Customview);
                bottomSheetOpacity=new BottomSheetOpacity(context);
                bottomSheetOpacity.show();
                initViewOpacity(bottomSheetOpacity);
            }else {
                bottomSheetOpacity.show();
            }

        });

    }

    private void initViewOpacity(BottomSheetOpacity bottomSheetOpacity) {
        bottomSheetOpacity.getBinding().icDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetOpacity.dismiss();
            }
        });
        bottomSheetOpacity.getBinding().seekbarOfFragmentEditWatermark.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                drawControllerView.getDrawView().setDrawAlpha(seekBar.getProgress());
            }
        });
    }

    private void initViewColorPicker(BottomSheetColorPicker bottomSheetColorPicker) {
        bottomSheetColorPicker.getViewBinding().viewColorRed.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        bottomSheetColorPicker.getViewBinding().viewColorYellow.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
        bottomSheetColorPicker.getViewBinding().viewColorGreen.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        bottomSheetColorPicker.getViewBinding().viewColorCyan.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
        bottomSheetColorPicker.getViewBinding().viewColorViolet.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
        bottomSheetColorPicker.getViewBinding().viewColorWhite.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        bottomSheetColorPicker.getViewBinding().viewColorBlack.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        bottomSheetColorPicker.getViewBinding().viewColorPicker.setImageResource(R.drawable.ic_multi_color);


//        changeSelectColor(PRE_COLOR, CURRENT_COLOR_DRAW);

        bottomSheetColorPicker.getViewBinding().viewColorRed.setOnClickListener(v -> {
            PRE_COLOR = CURRENT_COLOR_DRAW;
            CURRENT_COLOR_DRAW = COLOR_RED;
            changeSelectColor(PRE_COLOR, CURRENT_COLOR_DRAW);
            drawControllerView.getDrawView().setDrawColor(Color.RED);
        });
        bottomSheetColorPicker.getViewBinding().viewColorYellow.setOnClickListener(v -> {
            PRE_COLOR = CURRENT_COLOR_DRAW;
            CURRENT_COLOR_DRAW = COLOR_YELLOW;
            changeSelectColor(PRE_COLOR, CURRENT_COLOR_DRAW);
            drawControllerView.getDrawView().setDrawColor(Color.YELLOW);
        });
        bottomSheetColorPicker.getViewBinding().viewColorGreen.setOnClickListener(v -> {
            PRE_COLOR = CURRENT_COLOR_DRAW;
            CURRENT_COLOR_DRAW = COLOR_GREEN;
            changeSelectColor(PRE_COLOR, CURRENT_COLOR_DRAW);
            drawControllerView.getDrawView().setDrawColor(Color.GREEN);
        });
        bottomSheetColorPicker.getViewBinding().viewColorCyan.setOnClickListener(v -> {
            PRE_COLOR = CURRENT_COLOR_DRAW;
            CURRENT_COLOR_DRAW = COLOR_CYAN;
            changeSelectColor(PRE_COLOR, CURRENT_COLOR_DRAW);
            drawControllerView.getDrawView().setDrawColor(Color.CYAN);
        });
        bottomSheetColorPicker.getViewBinding().viewColorViolet.setOnClickListener(v -> {
            PRE_COLOR = CURRENT_COLOR_DRAW;
            CURRENT_COLOR_DRAW = COLOR_BLUE;
            changeSelectColor(PRE_COLOR, CURRENT_COLOR_DRAW);
            drawControllerView.getDrawView().setDrawColor(Color.BLUE);
        });
        bottomSheetColorPicker.getViewBinding().viewColorWhite.setOnClickListener(v -> {
            PRE_COLOR = CURRENT_COLOR_DRAW;
            CURRENT_COLOR_DRAW = COLOR_WHITE;
            changeSelectColor(PRE_COLOR, CURRENT_COLOR_DRAW);
            drawControllerView.getDrawView().setDrawColor(Color.WHITE);
        });
        bottomSheetColorPicker.getViewBinding().viewColorBlack.setOnClickListener(v -> {
            PRE_COLOR = CURRENT_COLOR_DRAW;
            CURRENT_COLOR_DRAW = COLOR_BLACK;
            changeSelectColor(PRE_COLOR, CURRENT_COLOR_DRAW);
            drawControllerView.getDrawView().setDrawColor(Color.BLACK);
        });
        bottomSheetColorPicker.getViewBinding().viewColorPicker.setOnClickListener(v -> {
            PRE_COLOR = CURRENT_COLOR_DRAW;
            CURRENT_COLOR_DRAW=COLOR_PICKER;
            changeSelectColor(PRE_COLOR, CURRENT_COLOR_DRAW);
            bottomSheetColorPicker.getViewBinding().colorPickerView.setVisibility(View.VISIBLE);
        });
        bottomSheetColorPicker.getViewBinding().imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetColorPicker.dismiss();
            }
        });
        bottomSheetColorPicker.getViewBinding().colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                drawControllerView.getDrawView().setDrawColor(envelope.getColor());
                bottomSheetColorPicker.getViewBinding().frStrokeColorPicker.setBackgroundTintList(ColorStateList.valueOf(envelope.getColor()));
            }
        });

    }
    private void initViewPenSize(BottomSheetPenSize bottomSheetPenSize) {
        bottomSheetPenSize.getBinding().seekbarOfFragmentEditWatermark.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                drawControllerView.getDrawView().setDrawWidth(seekBar.getProgress());
            }
        });
        bottomSheetPenSize.getBinding().icDone.setOnClickListener(v -> {
            bottomSheetPenSize.dismiss();
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
                openCamera();
                CONTROLLER_STATE = CONTROLLER_NON_SELECT;
                windowManager.removeView(rlOverlay);
                windowManager.removeView(controllerView1);
                windowManager.removeView(controllerView2);
                windowManager.removeView(controllerView3);
                windowManager.removeView(controllerView4);
            }
        });
        controllerView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("dfdf", "onClick: screen shot" );
                sendActionScreenShot();
//                ScreenShot.takeScreenshot();
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

    private void openCamera() {
            cameraLayoutParams = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT
            );

            cameraLayoutParams.format = PixelFormat.TRANSLUCENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cameraLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                cameraLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cameraLayoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
            }
            cameraLayoutParams.gravity = Gravity.START;
            cameraLayoutParams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            cameraLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;


            customCameraView=new CustomCameraView(MyService.this);
            customCameraView.setListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.e("dfdf", "onTouch: ");
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = layoutParams.x;
                            initialY = layoutParams.y;
                            initialTouchX = (int) event.getRawX();
                            initialTouchY = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            if (event.getRawX() >= dXMax / 2f) {
                                CONTROLLER_STATE_POSITION = RIGHT;
                                layoutParams.x = dXMax - v.getWidth();
                                windowManager.updateViewLayout(customCameraView, layoutParams);
                            } else {
                                CONTROLLER_STATE_POSITION = LEFT;
                                layoutParams.x = 1;
                                windowManager.updateViewLayout(customCameraView, layoutParams);
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            layoutParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            layoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(customCameraView, layoutParams);
                            break;
                    }
                    return false;
                }
            });
            customCameraView.getCameraView().setLifecycleOwner(null);
            customCameraView.getCameraView().open();
            windowManager.addView(customCameraView,cameraLayoutParams);


            customCameraView.getImgCancel().setOnClickListener(v -> {
                windowManager.removeView(customCameraView);
            });




    }

    private void sendActionScreenShot() {
////        Intent intent = null;
////        intent = new Intent(this, MyReceiver.class);
////        intent.putExtra("screen_shot",1);
////        assert intent != null;
////        return PendingIntent.getBroadcast(this,1,intent,PendingIntent.FLAG_MUTABLE);
//        Log.e("dfdf", "sendActionScreenShot in service: " );
//        Intent intent = new Intent("hahaha");
//        intent.putExtra("screen_shot", 1);
//        sendBroadcast(intent);
//        ScreenShot.takeScreenshot(this,window);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
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

    private void changeSelectColor(int PRE_COLOR, int CURRENT_COLOR_DRAW) {
        switch (PRE_COLOR) {
            case COLOR_RED:
                bottomSheetColorPicker.getViewBinding().frStrokeRed.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                break;
            case COLOR_YELLOW:
                bottomSheetColorPicker.getViewBinding().frStrokeYellow.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                break;
            case COLOR_GREEN:
                bottomSheetColorPicker.getViewBinding().frStrokeGreen.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                break;
            case COLOR_CYAN:
                bottomSheetColorPicker.getViewBinding().frStrokeCyan.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                break;
            case COLOR_BLUE:
                bottomSheetColorPicker.getViewBinding().frStrokeViolet.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                break;
            case COLOR_WHITE:
                bottomSheetColorPicker.getViewBinding().frStrokeWhite.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                break;
            case COLOR_BLACK:
                bottomSheetColorPicker.getViewBinding().frStrokeBlack.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                break;
            case COLOR_PICKER:
                bottomSheetColorPicker.getViewBinding().frStrokeColorPicker.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                break;
        }
        switch (CURRENT_COLOR_DRAW) {
            case COLOR_RED:
                bottomSheetColorPicker.getViewBinding().frStrokeRed.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
            case COLOR_YELLOW:
                bottomSheetColorPicker.getViewBinding().frStrokeYellow.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
            case COLOR_GREEN:
                bottomSheetColorPicker.getViewBinding().frStrokeGreen.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
            case COLOR_CYAN:
                bottomSheetColorPicker.getViewBinding().frStrokeCyan.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
            case COLOR_BLUE:
                bottomSheetColorPicker.getViewBinding().frStrokeViolet.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
            case COLOR_WHITE:
                bottomSheetColorPicker.getViewBinding().frStrokeWhite.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
            case COLOR_BLACK:
                bottomSheetColorPicker.getViewBinding().frStrokeBlack.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
            case COLOR_PICKER:
                bottomSheetColorPicker.getViewBinding().frStrokeColorPicker.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;
        }
    }
}
