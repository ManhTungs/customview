package com.example.customview.hmm1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.customview.R;

public class ActionView extends RelativeLayout {
    ImageView img;
    public ActionView(Context context) {
        this(context, null);
    }

    public ActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView=inflate(context, R.layout.view_action_record, this);

        init(context,rootView);
    }

    private void init(Context context, View rootView) {
        img=rootView.findViewById(R.id.img_view_action);
    }

    @Override
    public boolean performClick() {
        super.performClick();

        return true;
    }

    public void setImage(int resId){
        img.setImageResource(resId);
    }

}
