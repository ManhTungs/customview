package com.example.customview.hmm1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("dfdf", "onReceive: in receiver" + action);
//        EventBus.getDefault().post(new MainActivity.ButtonClickEvent());

//        switch (action) {
//            case 1:
//                break;
//        }
    }
}
