package com.example.customview.hmm1;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialog;

import com.example.customview.databinding.LayoutBottomSheetPenSizeBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mct.base.ui.binding.BaseBindingOverlayDialog;

public class BottomSheetPenSize extends BaseBindingOverlayDialog<LayoutBottomSheetPenSizeBinding> {
    @Override
    public Class<LayoutBottomSheetPenSizeBinding> getBindingClass() {
        return LayoutBottomSheetPenSizeBinding.class;
    }
    public BottomSheetPenSize(@NonNull Context context) {
        super(context);
    }

    @Override
    protected AppCompatDialog onCreateDialog(Context context) {
        return new BottomSheetDialog(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    protected DialogOption onCreateDialogOption() {
        return new DialogOption.Builder()
                .setBackgroundColor(Color.parseColor("#80000000"))
                .setCornerRadius(100)
                .setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                .build();
    }

    @Override
    protected void onDialogCreated(@NonNull AppCompatDialog dialog, DialogOption dialogOption, View view) {
        super.onDialogCreated(dialog, dialogOption, view);
        dialog.setCancelable(false);
    }

    public LayoutBottomSheetPenSizeBinding getBinding(){
        return binding;
    }
}
