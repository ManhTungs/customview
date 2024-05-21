package com.example.customview.hmm1;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.example.customview.R;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.gesture.Gesture;
import com.otaliastudios.cameraview.gesture.GestureAction;
import com.otaliastudios.cameraview.markers.DefaultAutoFocusMarker;

public class CustomCameraView extends FrameLayout {
    MyCameraView cameraView;
    ImageView imgCancel, imgExpand, imgRotate;
    View.OnTouchListener listener;

    private static final int FACE_CAMERA_BACK = 1, FACE_CAMERA_FONT = 2;
    private static int STATE_CAMERA = FACE_CAMERA_FONT;

    public CustomCameraView(@NonNull Context context) {
        this(context, null);
    }

    public CustomCameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.custom_camera_view, this);

        initView(context);
        initOnClick();
    }

    private void initOnClick() {
        imgRotate.setOnClickListener(v -> {
            if (STATE_CAMERA == FACE_CAMERA_FONT) {
                STATE_CAMERA = FACE_CAMERA_BACK;
                cameraView.setFacing(Facing.BACK);
            } else {
                STATE_CAMERA = FACE_CAMERA_FONT;
                cameraView.setFacing(Facing.FRONT);
            }
        });
    }

    private void initView(Context context) {
        cameraView = findViewById(R.id.camera_view);
        imgCancel = findViewById(R.id.img_cancel);
        imgRotate = findViewById(R.id.img_rotate);
        imgExpand = findViewById(R.id.img_expand);


        cameraView.setMode(Mode.PICTURE);
        cameraView.setFacing(Facing.FRONT);
        cameraView.setOnTouchListener((v, event) -> {
            if (listener != null) {
                return listener.onTouch(v, event);
            }
            return false;
        });


    }

    public CameraView getCameraView() {
        return cameraView;
    }

    public ImageView getImgCancel() {
        return imgCancel;
    }

    public ImageView getImgExpand() {
        return imgExpand;
    }

    public ImageView getImgRotate() {
        return imgRotate;
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        cameraView.setLifecycleOwner(lifecycleOwner);
    }

    public void setListener(OnTouchListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
