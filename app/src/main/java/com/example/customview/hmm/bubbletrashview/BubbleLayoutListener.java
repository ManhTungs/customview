package com.example.customview.hmm.bubbletrashview;

import android.graphics.Point;

interface BubbleLayoutListener {

    boolean onBubbleMove(Point position);

    boolean onBubbleFling(Point predictPosition);

    void onBubbleRelease();

}
