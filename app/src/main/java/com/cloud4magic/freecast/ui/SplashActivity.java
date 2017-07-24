package com.cloud4magic.freecast.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.cloud4magic.freecast.MainActivity;
import com.cloud4magic.freecast.R;

import java.lang.ref.SoftReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 启动页
 * Date   2017/6/30
 * Editor  Misuzu
 */

public class SplashActivity extends AppCompatActivity {

    private Unbinder mUnbinder;
    @BindView(R.id.splash_progress)
    ProgressBar mProgressBar;
    private MyHandler mHandler;
    private int mProgress = 0;
    private float mTimeDelta;

    private static final int UPDATE_PROGRESS = 100;

    private static class MyHandler extends Handler {

        private SoftReference<SplashActivity> mSoftReference;

        MyHandler(SplashActivity activity) {
            mSoftReference = new SoftReference<SplashActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivity activity = mSoftReference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    activity.mProgressBar.setProgress(activity.mProgress);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mUnbinder = ButterKnife.bind(this);
        // update progress
        updateProgress();
        mHandler = new MyHandler(this);
        mHandler.postDelayed(mRunnable, 2000);
    }

    /**
     * Runnable, to MainActivity
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    };

    /**
     * update ProgressBar
     */
    private void updateProgress() {
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
        final long startTime = System.currentTimeMillis();
        mTimeDelta = System.currentTimeMillis() - startTime;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mTimeDelta <= 1600) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mProgress = (int) (mTimeDelta / 1600f * 100f);
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(UPDATE_PROGRESS);
                    }
                    mTimeDelta = System.currentTimeMillis() - startTime;
                }
                mProgress = 100;
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(UPDATE_PROGRESS);
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
