package com.cloud4magic.freecast;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cloud4magic.freecast.ui.ConfigureActivity;
import com.cloud4magic.freecast.ui.LibraryActivity;
import com.cloud4magic.freecast.ui.DeclareActivity;
import com.cloud4magic.freecast.ui.VersionActivity;
import com.cloud4magic.freecast.utils.Fglass;
import com.cloud4magic.freecast.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主界面
 * Date   2017/6/29
 * Editor  Misuzu
 */
public class MainActivity extends AppCompatActivity {

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
    @BindView(R.id.main_bg)
    ImageView mMainBg;
    @BindView(R.id.main_layout)
    View mainLayout;
    @BindView(R.id.black_view)
    View blackView;
    @BindView(R.id.side_menu)
    View side_menu;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startLogoAnime();
        ActionBarDrawerToggle toggle = new DrawerListener(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        StatusBarUtil.setTranslucentForDrawerLayout(this, mDrawerLayout);
        mMainBg.post(new Runnable() {
            @Override
            public void run() {
                Fglass.blur(mMainBg, blackView, 2, 8);
            }
        });
    }

    private class DrawerListener extends ActionBarDrawerToggle {

        public DrawerListener(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            super.onDrawerSlide(drawerView, slideOffset);
            WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            mActivityMainContent.layout(mNavView.getRight(), 0, mNavView.getRight() + display.getWidth(), display.getHeight());
        }
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


    /**
     * 主界面点击事件
     */
    @OnClick({R.id.main_live_view, R.id.main_setting, R.id.main_browse, R.id.side_menu})
    public void onViewClicked(final View view) {

        switch (view.getId()) {
            case R.id.main_live_view: // 连接设备播放
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
                break;
            case R.id.main_setting: // 设置界面
                startActivity(new Intent(MainActivity.this, ConfigureActivity.class));
                break;
            case R.id.main_browse:  // 图片和视频
                startActivity(new Intent(MainActivity.this, LibraryActivity.class));
                break;
            case R.id.side_menu:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }

    /**
     * Logo动画
     */
    private void startLogoAnime() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0.9f, 1, 1.1f, 1);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0.9f, 1, 1.1f, 1);
        final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mainLogo, scaleX, scaleY);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(2000);
        animator.start();
    }

    /**
     * 侧边栏点击事件
     */
    @OnClick({R.id.version, R.id.disclaimer, R.id.privacy_policy, R.id.copyright})
    public void onMenuClicked(View view) {

        switch (view.getId()) {
            case R.id.version:
                startActivity(new Intent(this, VersionActivity.class));
                break;
            case R.id.disclaimer:
                DeclareActivity.startActivity(this, 0);
                break;
            case R.id.privacy_policy:
                DeclareActivity.startActivity(this, 1);
                break;
            case R.id.copyright:
                DeclareActivity.startActivity(this, 2);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }
}
