package com.cloud4magic.freecast.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.widget.BubbleSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 视频设置
 * Date   2017/7/3
 * Editor  Misuzu
 */

public class VideoSettingFragment extends Fragment {

    @BindView(R.id.smooth_part)
    TextView mSmoothPart;
    @BindView(R.id.good_part)
    TextView mGoodPart;
    @BindView(R.id.best_part)
    TextView mBestPart;
    @BindView(R.id.custom_part)
    TextView mCustomPart;
    @BindView(R.id.resolution_seekbar)
    BubbleSeekBar mResolutionSeekbar;
    @BindView(R.id.bitrate_seekbar)
    BubbleSeekBar mBitrateSeekbar;
    @BindView(R.id.frame_rate_seekbar)
    BubbleSeekBar mFrameRateSeekbar;
    @BindView(R.id.modify_button)
    TextView mModifyButton;
    View lastViewSelected;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_setting_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView()
    {
        mSmoothPart.setSelected(true);
        lastViewSelected = mSmoothPart;
        mResolutionSeekbar.isShowBubble(false);
    }

    public static VideoSettingFragment newInstance() {

        return new VideoSettingFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.smooth_part, R.id.good_part, R.id.best_part, R.id.custom_part})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.smooth_part:
                break;
            case R.id.good_part:
                break;
            case R.id.best_part:
                break;
            case R.id.custom_part:
                break;
            case R.id.modify_button:
                break;
        }
        switchCheck(view);
    }

    private void switchCheck(View view)
    {
        view.setSelected(true);
        lastViewSelected.setSelected(false);
        lastViewSelected = view;
    }
}
