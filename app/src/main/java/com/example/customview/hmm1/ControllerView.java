package com.example.customview.hmm1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.customview.R;

public class ControllerView extends RelativeLayout {

    private CustomImageView img;
    private float dX, dY;

    public ControllerView(Context context) {
        this(context, null);
    }

    public ControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView=inflate(context, R.layout.custom_circle_view, this);
        init(context,rootView);
    }

    public void init(Context context,View view) {
        img = findViewById(R.id.img_control);
        view.setOnTouchListener(new OnTouchListener() {
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

//                        if (event.getRawX()>=dXMax/2){
//                            v.animate()
//                                    .x(dXMax-200)
//                                    .y(event.getRawY() + dY)
//                                    .setDuration(300)
//                                    .start();
//                        }else {
//                            v.animate()
//                                    .x(0)
//                                    .y(event.getRawY() + dY)
//                                    .setDuration(300)
//                                    .start();
//                        }
                        v.animate()
                                .x(0)
                                .y(event.getRawY() + dY)
                                .setDuration(300)
                                .start();
                        break;

                    default:
                        return false;
                }
                return true;
            }
        });
    }

    @Override
    public boolean performClick() {
        super.performClick();
//        doSomething();
        return true;
    }
    private void doSomething() {
        Toast.makeText(getContext(), "did something", Toast.LENGTH_SHORT).show();
    }
}
