package com.cloud4magic.freecast;

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
import android.widget.FrameLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
        StatusBarUtil.setTranslucentForDrawerLayout(this,mDrawerLayout);
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
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_live_view: // 连接设备播放
                startActivity(new Intent(this,PlayerActivity.class));
                break;
            case R.id.main_setting: // 设置界面
                break;
            case R.id.main_browse:  // 图片和视频
                break;
        }
    }
}
