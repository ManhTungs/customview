package com.example.customview.hmm1;

import static com.example.customview.hmm1.MyService.window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.byox.drawview.views.DrawView;
import com.example.customview.R;
import com.example.customview.hmm.bubbletrashview.BubbleService;
import com.example.customview.hmm.bubbletrashview.BubblesManager;
import com.otaliastudios.cameraview.CameraView;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    int MY_REQUEST_CODE = 999;
    Button button;
    private static final int REQUEST_MANAGE_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn_start);
        button.setOnClickListener(v -> {
            Log.e("dfdf", "onCreate: click" );
            String key = BubbleService.EXTRA_CUTOUT_SAFE_AREA;
            final Intent intent = new Intent(this, BubbleService.class);
            intent.putExtra(key, BubblesManager.findCutoutSafeArea(this));
            startService(intent);
        });

//        if (!Settings.canDrawOverlays(this)) {   //Android M Or Over
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, MY_REQUEST_CODE);
//        }

//        requestOverlayPermission(this, (allGranted, gl, dl) -> {
//            if (allGranted) {
//                String key = BubbleService.EXTRA_CUTOUT_SAFE_AREA;
//                final Intent intent = new Intent(this, BubbleService.class);
//                intent.putExtra(key, BubblesManager.findCutoutSafeArea(this));
//                startService(intent);
//            }
//        });

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(layoutParams);
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setSystemBarsVisible(false, WindowInsetsCompat.Type.systemBars());


//        requestPermission();
//        Intent service = new Intent(this, BubbleService.class);
//        startService(service);


    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_MANAGE_EXTERNAL_STORAGE);
            } else {
                // Quyền đã được cấp, tiếp tục xử lý logic của bạn
            }
        } else {
            // Đối với các phiên bản thấp hơn Android 11, yêu cầu quyền truy cập bộ nhớ bình thường
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_MANAGE_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonClick(ButtonClickEvent event) {
        Log.e("dfdf", "onButtonClick: screen shot");
        ScreenShot.takeScreenshot(this, getWindow());
    }


    public static class ButtonClickEvent {

    }

    public void setSystemBarsVisible(boolean visible, int type) {
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (visible) {
            // Show the system bars.
            windowInsetsController.show(type);
        } else {
            // Hide the system bars.
            windowInsetsController.hide(type);
        }
    }


    static void requestOverlayPermission(FragmentActivity activity, RequestCallback callback) {
        PermissionX.init(activity)
                .permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .onExplainRequestReason((scope, deniedList) -> scope.showRequestReasonDialog(deniedList,
                        "You need to grant the app permission to use this feature.",
                        "OK",
                        "Cancel"))
                .request(callback);
    }
}