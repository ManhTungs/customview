package com.example.customview.hmm1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.byox.drawview.views.DrawView;
import com.example.customview.R;


public class DrawControllerView extends FrameLayout {
    private float lastX, lastY;
    private int deltaX, deltaY;
    private LinearLayout drawController;
    private DrawView drawView;
    private ImageView imgCancel,imgUndo,imgRedo,imgEraser,imgMultiColor,imgPenSize,imgBrush,imgRectangle,imgCircle,imgClean, imgAlpha;
    public DrawControllerView(Context context) {
        this(context, null);
    }

    public DrawControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public DrawControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_draw_controller_view, this);

        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        imgCancel = findViewById(R.id.ic_cancel);
        imgUndo = findViewById(R.id.ic_undo);
        imgRedo = findViewById(R.id.ic_redo);
        imgEraser = findViewById(R.id.ic_eraser);
        imgMultiColor = findViewById(R.id.ic_multi_color);
        imgPenSize = findViewById(R.id.ic_pensize);
        drawController = findViewById(R.id.draw_controller);
        drawView = findViewById(R.id.draw_view);
        imgBrush=findViewById(R.id.ic_pen);
        imgRectangle=findViewById(R.id.ic_retangle);
        imgCircle=findViewById(R.id.ic_circle);
        imgClean=findViewById(R.id.ic_clear);
        imgAlpha =findViewById(R.id.ic_opacity);

        drawController.setOnTouchListener(new OnTouchListener() {
            @Override


            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Lấy vị trí khi chạm vào màn hình
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        // Tính khoảng cách giữa vị trí của view và vị trí chạm vào màn hình
                        deltaX = (int) (v.getX() - lastX);
                        deltaY = (int) (v.getY() - lastY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Tính toán vị trí mới dựa trên sự di chuyển của ngón tay
                        float newX = event.getRawX() + deltaX;
                        float newY = event.getRawY() + deltaY;
                        // Di chuyển view đến vị trí mới
                        v.setX(newX);
                        v.setY(newY);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Thực hiện các hành động cần thiết khi sự kiện kết thúc (nếu cần)
                        break;
                }
                // Trả về true để chỉ định rằng bạn đã xử lý sự kiện chạm vào
                return true;
            }
        });
    }

    public ImageView getImgCancel() {
        return imgCancel;
    }

    public ImageView getImgUndo() {
        return imgUndo;
    }

    public ImageView getImgRedo() {
        return imgRedo;
    }

    public ImageView getImgEraser() {
        return imgEraser;
    }

    public ImageView getImgMultiColor() {
        return imgMultiColor;
    }

    public ImageView getImgPenSize() {
        return imgPenSize;
    }

    public LinearLayout getDrawController() {
        return drawController;
    }

    public DrawView getDrawView() {
        return drawView;
    }

    public ImageView getImgBrush() {
        return imgBrush;
    }

    public ImageView getImgRectangle() {
        return imgRectangle;
    }

    public ImageView getImgCircle() {
        return imgCircle;
    }

    public ImageView getImgClean() {
        return imgClean;
    }

    public ImageView getImgAlpha() {
        return imgAlpha;
    }

    @Override
    public boolean performClick() {
        Log.e("dfdf", "performClick: " );
        return super.performClick();
    }
}
