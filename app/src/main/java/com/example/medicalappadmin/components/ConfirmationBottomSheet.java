package com.example.medicalappadmin.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.medicalappadmin.databinding.SheetConfirmationBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ConfirmationBottomSheet {
    private static void showConfirmationBottomSheet(Context context, String title, String subtitle, String positiveBtnTxt, String negativeBtnTxt, View.OnClickListener positiveBtnClickListener, View.OnClickListener negativeBtnClickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);

        SheetConfirmationBinding binding = SheetConfirmationBinding.inflate(inflater);

        binding.headerTxt.setText(title);
        binding.secondaryTxt.setText(subtitle);
        binding.positiveBtn.setText(positiveBtnTxt);
        binding.negativeBtn.setText(negativeBtnTxt);




        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(binding.getRoot());

        binding.positiveBtn.setOnClickListener(view->{
            bottomSheetDialog.dismiss();
            if (positiveBtnClickListener != null)
                positiveBtnClickListener.onClick(view);
        });
        binding.negativeBtn.setOnClickListener(view->{
            bottomSheetDialog.dismiss();
            if (negativeBtnClickListener != null)
                negativeBtnClickListener.onClick(view);
        });

        bottomSheetDialog.show();
    }

    public static void confirmLogout(Context context, View.OnClickListener onLogoutConfirm){
        showConfirmationBottomSheet(context, "Log out", "Are you sure you want to Log out?",
                "No", "Yes", null, onLogoutConfirm);
    }
}
