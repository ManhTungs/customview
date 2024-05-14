package com.example.customview.hmm1;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import com.example.customview.databinding.LayoutBottomSheetPenSizeBinding;
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
        return null;
    }

    @Nullable
    @Override
    protected DialogOption onCreateDialogOption() {
        return null;
    }

    @Override
    protected void onDialogCreated(@NonNull AppCompatDialog dialog, DialogOption dialogOption, View view) {
        super.onDialogCreated(dialog, dialogOption, view);
    }
}
