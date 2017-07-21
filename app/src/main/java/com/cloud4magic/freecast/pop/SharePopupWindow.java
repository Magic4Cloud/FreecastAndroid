package com.cloud4magic.freecast.pop;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.cloud4magic.freecast.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date    2017/7/20
 * Author  xiaomao
 */

public class SharePopupWindow extends PopupWindow {

    private Activity mActivity;
    private OnPlatformListener mListener;
    private String mPath;
    @BindView(R.id.share_youtube)
    LinearLayout mYoutubeView;
    @BindView(R.id.share_facebook)
    LinearLayout mFacebookView;
    @BindView(R.id.share_instagram)
    LinearLayout mInstagramView;

    public SharePopupWindow(Activity activity) {
        mActivity = activity;
        initParameters();
        initView();
    }

    private void initParameters() {
        setAnimationStyle(R.style.PopupWindowAnimation);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        int screenWidth = mActivity.getResources().getDisplayMetrics().widthPixels;
        setWidth(screenWidth - dp2px(mActivity, 64));
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                recoverAlpha();
            }
        });
        setOutsideTouchable(true);
        update();
    }

    private void initView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.popup_window_share, null);
        setContentView(view);
        ButterKnife.bind(this, view);
        // check instagram
        if (isAppInstalled(mActivity, "com.instagram.android")) {
            mInstagramView.setVisibility(View.VISIBLE);
        } else {
            mInstagramView.setVisibility(View.GONE);
        }
    }

    public void update(String path, boolean isPhoto) {
        mPath = path;
        // check youtube
        if (mYoutubeView != null) {
            if (isPhoto) {
                mYoutubeView.setVisibility(View.GONE);
            } else {
                if (isAppInstalled(mActivity, "com.google.android.youtube")) {
                    mYoutubeView.setVisibility(View.VISIBLE);
                } else {
                    mYoutubeView.setVisibility(View.GONE);
                }
            }
        }
    }

    @OnClick(R.id.share_youtube)
    protected void actionYoutube() {
        if (mListener != null) {
            mListener.onYoutube(mPath);
        }
        close();
    }

    @OnClick(R.id.share_facebook)
    protected void actionFacebook() {
        if (mListener != null) {
            mListener.onFacebook(mPath);
        }
        close();
    }

    @OnClick(R.id.share_instagram)
    protected void actionInstagram() {
        if (mListener != null) {
            mListener.onInstagram(mPath);
        }
        close();
    }

    @OnClick(R.id.share_cancel)
    protected void actionCancel() {
        close();
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * check the app is installed
     */
    private boolean isAppInstalled(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * set alpha background
     */
    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = alpha;
        if (alpha == 1) {
            // 不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            // 此行代码主要是解决在华为手机上半透明效果无效的bug
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        mActivity.getWindow().setAttributes(lp);
    }

    /**
     * set alpha of Activity
     */
    private void setActivityAlpha() {
        setBackgroundAlpha(0.6f);
    }

    /**
     * recover the alpha of Activity
     */
    private void recoverAlpha() {
        setBackgroundAlpha(1f);
    }

    public void showAtBottom() {
        showAtLocation(getContentView(), Gravity.BOTTOM, 0, dp2px(mActivity, 42));
        setActivityAlpha();
    }

    public void close() {
        if (isShowing()) {
            dismiss();
            recoverAlpha();
        }
    }

    public void setOnPlatformListener(OnPlatformListener listener) {
        mListener = listener;
    }

    public interface OnPlatformListener {
        void onYoutube(String path);

        void onFacebook(String path);

        void onInstagram(String path);
    }
}
