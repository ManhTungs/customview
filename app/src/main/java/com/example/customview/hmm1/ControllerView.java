package com.example.customview.hmm1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.customview.R;

public class ControllerView extends RelativeLayout {

    private ImageView img;
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
        img = findViewById(R.id.img_view_action);

    }

    @Override
    public boolean performClick() {
        super.performClick();
        doSomething();
        return true;
    }


    private void doSomething() {
        Toast.makeText(getContext(), "did something", Toast.LENGTH_SHORT).show();
    }


}
