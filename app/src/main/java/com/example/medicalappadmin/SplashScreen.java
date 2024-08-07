package com.example.medicalappadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;

import com.example.medicalappadmin.Tools.Const;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.databinding.ActivitySplashBinding;



public class SplashScreen extends AppCompatActivity {

    ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen().setOnExitAnimationListener(splashScreenView -> {
                ObjectAnimator animator = ObjectAnimator.ofFloat(splashScreenView, "alpha", 1f, 0f);
                animator.setInterpolator(new AnticipateInterpolator());
                animator.setDuration(200);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                       splashScreenView.remove();
                    }
                });
                animator.start();
            });
        }
        setContentView(binding.getRoot());
        Methods.setStatusBarColor(Color.TRANSPARENT, SplashScreen.this);
        binding.getRoot().post(()->startAnimation());
    }

    private void startAnimation() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        float scaleX = (float) screenWidth / binding.circleView.getWidth();
        float scaleY = (float) screenHeight / binding.circleView.getHeight();
        float actualScale = 1.5f * Math.max(scaleX, scaleY);
        binding.circleView.animate()
                        .alpha(1f)
                        .setStartDelay(100)
                        .setDuration(450)
                        .start();
        binding.circleView.animate()
                .scaleX(actualScale)
                .scaleY(actualScale)
                .setInterpolator(new AccelerateInterpolator(2.5f))
                .setDuration(950)
                .setStartDelay(200)
                .setStartDelay(0)
                .start();

        ObjectAnimator colorAnimation = ObjectAnimator.ofArgb(binding.logoView, "colorFilter",
                getColor(R.color.natural50),
                getColor(R.color.primary700));
        colorAnimation
                .setDuration(700)
                .setStartDelay(250);

        colorAnimation.start();



        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startApp();
            }
        };
        handler.postDelayed(runnable, 1500);
    }

    private void startApp() {
        String jwt = SplashScreen.this.getSharedPreferences("MY_PREF", MODE_PRIVATE)
                .getString("JWT_TOKEN","");

        if(!jwt.equals("")){
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            boolean isWalkThrough = SplashScreen.this.getSharedPreferences("MY_PREF", MODE_PRIVATE).getBoolean(Const.isWalkThroughComplete, false);
            if (isWalkThrough){
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, WalkThroughActivity.class));
            }
        }
        finish();
    }
}