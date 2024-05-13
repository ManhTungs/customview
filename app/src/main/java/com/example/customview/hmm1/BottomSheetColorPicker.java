//package com.example.customview.hmm1;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatDialog;
//
//import com.example.customview.databinding.LayoutBottomSheetColorPickerBinding;
//import com.google.android.material.bottomsheet.BottomSheetDialog;
//import com.mct.base.ui.binding.BaseBindingOverlayDialog;
//
//public class BottomSheetColorPicker extends BaseBindingOverlayDialog<LayoutBottomSheetColorPickerBinding> {
//    @Override
//    public Class<LayoutBottomSheetColorPickerBinding> getBindingClass() {
//        return LayoutBottomSheetColorPickerBinding.class;
//    }
//    public BottomSheetColorPicker(@NonNull Context context) {
//        super(context);
//    }
//
//    @Override
//    protected AppCompatDialog onCreateDialog(Context context) {
//        return new BottomSheetDialog(context);
//    }
//
//    @Nullable
//    @Override
//    protected DialogOption onCreateDialogOption() {
//        return new DialogOption.Builder()
//                .setBackgroundColor(Color.WHITE)
//                .setCornerRadius(16)
//                .build();
//    }
//
//}
