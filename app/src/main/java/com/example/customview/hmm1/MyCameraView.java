package com.example.customview.hmm1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otaliastudios.cameraview.CameraView;

public class MyCameraView extends CameraView {

    private boolean allowFocus = false;

    public void setAllowFocus(boolean b) {
        this.allowFocus = b;
    }

    public MyCameraView(@NonNull Context context) {
        super(context);
    }

    public MyCameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Steal our own events if gestures are enabled
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (allowFocus) {
            return super.onTouchEvent(event);
        }
        return true;
    }
}
