package com.cloud4magic.freecast.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cloud4magic.freecast.MainActivity;
import com.cloud4magic.freecast.R;

/**
 * 启动页
 * Date   2017/6/30
 * Editor  Misuzu
 */

public class SplashActivity extends AppCompatActivity{

    Handler mHandler;
    Runnable mRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.temp_splash_img);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        setContentView(imageView);
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },2000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(mRunnable);
    }
}
