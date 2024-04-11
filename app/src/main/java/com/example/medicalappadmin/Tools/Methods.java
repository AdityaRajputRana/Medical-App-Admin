package com.example.medicalappadmin.Tools;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalappadmin.LoginActivity;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.databinding.DialogPenBinding;
import com.example.medicalappadmin.rest.api.HashUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Methods {

    private static boolean isLogOutShown = false;

    public static void shareText(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        if (sendIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(sendIntent, "Share using"));
        } else {
            Toast.makeText(context, "No app can handle this action", Toast.LENGTH_SHORT).show();
        }
    }

    public static void showForceLogOutDialog(AppCompatActivity context){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()->mainUIForceLogoutDialog(context));
    }

    private static void mainUIForceLogoutDialog(AppCompatActivity context){
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
    public static void showInvalidSearchTermSignature(AppCompatActivity activity){
        new AlertDialog.Builder(activity)
                .setTitle(HashUtils.decode(t))
                .setMessage(HashUtils.decode(m))
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static void showFailedAlert(AppCompatActivity activity, String code, String message, String redirectLink, boolean retry, boolean cancellable){
        if (isLogOutShown){
            return;
        }
        message = message + " (EC"+code+")";
        showError(activity, message, cancellable);
    }

    private static String t = "RGV2ZWxvcGVyIERldGFpbHM";

    private static String m = "VGhpcyBhcHAgd2FzIGRldmVsb3BlZCBieSBBZGl0eWEgUmFuYSAoMjFCQ1MwNTApLgoKQ29udGFjdCBEZXRhaWxzOgpFbWFpbCA6ICBhZGl0eWFyYWpwdXRyYW5hMjAxNkBnbWFpbC5jb20KUGhvbmUvV0EgIDogKzkxIDg1ODA0IDE1OTc4";
    public static void showError(AppCompatActivity context, String message, boolean cancellable){
        if (context != null) {

            AlertDialog dialog;
            DialogPenBinding dialogBinding;

            dialogBinding = DialogPenBinding.inflate(context.getLayoutInflater(), null, false);

            dialogBinding.titleTxt.setVisibility(View.VISIBLE);
            dialogBinding.bodyTxt.setVisibility(View.VISIBLE);

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


    public static void setStatusBarColor(int color, AppCompatActivity activity) {
        if (color == Color.TRANSPARENT){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window w = activity.getWindow();
                w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(color);
        }
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String convertDate(long timestamp) {
        Date date = new Date(timestamp);
        Date today = new Date();
        long oneDay = 24 * 60 * 60 * 1000; // Milliseconds in a day
        long oneWeek = 7 * oneDay; // Milliseconds in a week

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
        timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String timeString = timeFormat.format(date);

        long diffDays = Math.round(Math.abs((today.getTime() - date.getTime()) / oneDay));

        if (diffDays == 0) {
            return "TODAY, " + timeString;
        } else if (diffDays == 1) {
            return "YESTERDAY, " + timeString;
        } else if (diffDays > 1 && diffDays < 7) {
            SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.US);
            return dayOfWeekFormat.format(date) + ", " + timeString;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            return dateFormat.format(date) + ", " + timeString;
        }
    }
}
