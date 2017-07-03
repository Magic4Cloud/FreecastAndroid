package com.cloud4magic.freecast;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cloud4magic.freecast.ui.ConfigureActivity;
import com.cloud4magic.freecast.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主界面
 * Date   2017/6/29
 * Editor  Misuzu
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.main_live_view)
    FrameLayout mMainLiveView;
    @BindView(R.id.main_setting)
    FrameLayout mMainSetting;
    @BindView(R.id.main_browse)
    FrameLayout mMainBrowse;
    @BindView(R.id.activity_main_content)
    FrameLayout mActivityMainContent;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.main_logo)
    ImageView mainLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startLogoAnime();
        mNavView.setNavigationItemSelectedListener(this);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                mActivityMainContent.layout(mNavView.getRight(), 0, mNavView.getRight() + display.getWidth(), display.getHeight());
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        StatusBarUtil.setTranslucentForDrawerLayout(this, mDrawerLayout);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 主界面点击事件
     */
    @OnClick({R.id.main_live_view, R.id.main_setting, R.id.main_browse})
    public void onViewClicked(final View view) {

        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1, 0.9f, 1);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1, 0.9f, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
        animatorSet.setDuration(300);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                switch (view.getId()) {
                    case R.id.main_live_view: // 连接设备播放
                        startActivity(new Intent(MainActivity.this, PlayerActivity.class));
                        break;
                    case R.id.main_setting: // 设置界面
                        startActivity(new Intent(MainActivity.this, ConfigureActivity.class));
                        break;
                    case R.id.main_browse:  // 图片和视频
                        break;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    private void startLogoAnime() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0.9f, 1, 1.1f, 1);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0.9f, 1, 1.1f, 1);
        final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mainLogo, scaleX, scaleY);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(2000);
        animator.start();
    }

}
