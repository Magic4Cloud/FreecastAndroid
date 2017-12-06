package com.cloud4magic.freecast.pop;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cloud4magic.freecast.R;

/**
 * Date    06/12/2017
 * Author  WestWang
 * 网络链接检测
 */

public class CheckNetworkPopupWindow extends PopupWindow {

    private Activity mActivity;

    public CheckNetworkPopupWindow(Activity activity) {
        mActivity = activity;
        initParameters();
        initView();
    }

    private void initParameters() {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                recoverAlpha();
            }
        });
        update();
    }

    private void initView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.loading_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.loading_text);
        textView.setText(R.string.check_network_connection);
        setContentView(view);
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
        showAsDropDown(getContentView());
        setActivityAlpha();
    }

    public void close() {
        if (isShowing()) {
            dismiss();
            recoverAlpha();
        }
    }
}
