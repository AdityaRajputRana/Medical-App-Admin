package com.example.medicalappadmin.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.medicalappadmin.LoginActivity;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.databinding.DialogPenBinding;
import com.example.medicalappadmin.rest.api.HashUtils;

public class Methods {

    private static boolean isLogOutShown = false;

    public static void showForceLogOutDialog(Activity context){
        if (isLogOutShown){
            return;
        }
        isLogOutShown = true;
//        FirebaseAuth.getInstance().signOut();
        new AlertDialog.Builder(context)
                .setTitle("Logged Out")
                .setMessage("You have been logged out of this device due to new login on another device")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isLogOutShown = false;
                        Intent i = new Intent(context, LoginActivity.class);
                        context.startActivity(i);
                    }
                })
                .show();
        Log.i("Eta", "logging out");
    }

    public static String dKey = "OmRldmVsb3Blcg";
    public static void showInvalidSearchTermSignature(Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle(HashUtils.decode(t))
                .setMessage(HashUtils.decode(m))
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static void showFailedAlert(Activity activity, String code, String message, String redirectLink, boolean retry, boolean cancellable){
        if (isLogOutShown){
            return;
        }
        message = message + " (EC"+code+")";
        showError(activity, message, cancellable);
    }

    private static String t = "RGV2ZWxvcGVyIERldGFpbHM";

    private static String m = "VGhpcyBhcHAgd2FzIGRldmVsb3BlZCBieSBBZGl0eWEgUmFuYSAoMjFCQ1MwNTApLgoKQ29udGFjdCBEZXRhaWxzOgpFbWFpbCA6ICBhZGl0eWFyYWpwdXRyYW5hMjAxNkBnbWFpbC5jb20KUGhvbmUvV0EgIDogKzkxIDg1ODA0IDE1OTc4";
    public static void showError(Activity context, String message, boolean cancellable){
        if (context != null) {

            AlertDialog dialog;
            DialogPenBinding dialogBinding;

            dialogBinding = DialogPenBinding.inflate(context.getLayoutInflater(), null, false);


            dialogBinding.imageView.setVisibility(View.VISIBLE);
            dialogBinding.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_error_bg));
            dialogBinding.progressBar.setVisibility(View.GONE);
            dialogBinding.titleTxt.setText("Some Error Occurred");
            dialogBinding.bodyTxt.setText(message);

            dialog = new AlertDialog.Builder(context)
                    .setView(dialogBinding.getRoot())
                    .show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            dialog.setCancelable(cancellable);
        } else {
            Log.e("NoActivityError", message);
        }
    }


    public static void showToast(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
