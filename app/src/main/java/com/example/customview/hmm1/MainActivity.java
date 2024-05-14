package com.example.customview.hmm1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.byox.drawview.views.DrawView;
import com.example.customview.R;

public class MainActivity extends AppCompatActivity {

    int MY_REQUEST_CODE=999;

    DrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Settings.canDrawOverlays(this)) {   //Android M Or Over
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, MY_REQUEST_CODE);
        }
//        drawView=findViewById(R.id.draw_view);




        Intent service=new Intent(this,MyService.class);
        startService(service);
        finish();
    }
}