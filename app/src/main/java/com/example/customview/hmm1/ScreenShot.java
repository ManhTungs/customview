package com.example.customview.hmm1;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenShot {
    public static void takeScreenshot(Context context,Window window) {
        Log.e("dfdf", "takeScreenshot:vcvcvc " );
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView =window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        // Create a bitmap with the same size as the view
        final Bitmap bitmap = Bitmap.createBitmap(window.getDecorView().getWidth(),
                decorView.getHeight(), Bitmap.Config.ARGB_8888);

        // Specify the location of the image to save
        String directScreenShot= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/EZScreenShot";
        File fileDirectScreenShot=new File(directScreenShot);
        if (!fileDirectScreenShot.exists()){
            fileDirectScreenShot.mkdirs();
        }

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/EZScreenShot/screenshot111345678.png";
        File imageFile = new File(path);
        // Use PixelCopy to capture the screenshot
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PixelCopy.request(window, bitmap, (copyResult) -> {
                if (copyResult == PixelCopy.SUCCESS) {
                    try (FileOutputStream out = new FileOutputStream(imageFile)) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        Toast.makeText(context, "Screenshot saved to " + path, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("dfdf", "takeScreenshot: ",e );
                        Toast.makeText(decorView.getContext(), "Failed to save screenshot", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to capture screenshot", Toast.LENGTH_SHORT).show();
                }
            }, new Handler(Looper.getMainLooper()));
        }
    }
}
