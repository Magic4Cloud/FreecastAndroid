package com.cloud4magic.freecast.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloud4magic.freecast.MyAplication;
import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.api.ParametersConfig;
import com.cloud4magic.freecast.utils.Logger;
import com.cloud4magic.freecast.utils.ToastUtil;
import com.cloud4magic.freecast.widget.BubbleSeekBar;
import com.cloud4magic.freecast.widget.LoadingDialogFragment;
import com.demo.sdk.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.Map;

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
    LoadingDialogFragment mLoadingDialogFragment;
    View lastViewSelected;
    Unbinder unbinder;
    int fps;
    int resolution;
    int quality;
    float bitRate;

    private String mDeviceId = "";
    public static String mDeviceIp = "";
    private String mDeviceName = "";
    private String mDevicePassword = "";
    public static boolean  isInitDevice = false;


    private ParametersConfig mParametersConfig = null;

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
        mResolutionSeekbar.setUnit("P");
        mBitrateSeekbar.setUnit("M");
        mFrameRateSeekbar.setUnit("FPS");
        mBitrateSeekbar.setProgress(2);
        mLoadingDialogFragment = LoadingDialogFragment.newInstance();
        mLoadingDialogFragment.show(getActivity().getFragmentManager(),"");
        switchCheck(mSmoothPart);
        scanDevice();
        setOnProgressChange();
    }


    /**
     * scan device
     */
    private void scanDevice() {
        if (isInitDevice) {
            return;
        }
        Scanner scanner = new Scanner(getContext());
        scanner.setOnScanOverListener(new Scanner.OnScanOverListener() {
            @Override
            public void onResult(Map<InetAddress, String> map, InetAddress inetAddress) {
                // found device
                if (map != null) {
                    boolean found = false;
                    for (Map.Entry<InetAddress, String> entry : map.entrySet()) {
                        if (entry != null) {
                            mDeviceId = entry.getValue();
                            mDeviceIp = entry.getKey().getHostAddress();
                            mDeviceName = mDeviceId;
                            int indexStart = mDeviceId.indexOf(".");
                            if (indexStart != -1) {
                                int indexEnd = mDeviceId.indexOf(".", indexStart + 1);
                                if (indexEnd != -1) {
                                    mDeviceName = mDeviceId.substring(indexStart + 1, indexEnd);
                                }
                            }
                            Logger.e("Misuzu", "device id: " + mDeviceId);
                            Logger.e("Misuzu", "device ip: " + mDeviceIp);
                            Logger.e("Misuzu", "device name: " + mDeviceName);
                            found = true;
                            isInitDevice = true;
                            connectDevice();
                            break;
                        }
                    }
                    if (!found) {
                        ToastUtil.show(MyAplication.INSTANCE, MyAplication.INSTANCE.getString(R.string.device_not_found));
                        mLoadingDialogFragment.dismiss();
                    }
                } else {
                    // device not found
                    ToastUtil.show(MyAplication.INSTANCE, MyAplication.INSTANCE.getString(R.string.device_not_found));
                    mLoadingDialogFragment.dismiss();
                }
            }
        });
        scanner.scanAll();
    }


    /**
     * connect device
     */
    private void connectDevice() {
        mDevicePassword = "admin";
        mParametersConfig = new ParametersConfig(mDeviceIp + ":" + 80, mDevicePassword);
        mParametersConfig.setOnResultListener(mConfigListener);
        mParametersConfig.getResolution(0);
        mParametersConfig.getFps(0);
        mParametersConfig.getQuality(0);
    }

    /**
     * ParametersConfig.OnResultListener
     */
    private ParametersConfig.OnResultListener mConfigListener = new ParametersConfig.OnResultListener() {
        @Override
        public void onResult(ParametersConfig.Response result) {
            if (result == null) {
                return;
            }

            switch (result.type) {
                case ParametersConfig.GET_USERNAME_PASSWORD:
                    Logger.e("Misuzu", "ParametersConfig.GET_USERNAME_PASSWORD");
                    break;
                case ParametersConfig.GET_QUALITY:
                    Logger.e("Misuzu", "ParametersConfig.GET_QUALITY");
                    quality = praseJson(result.body);
                    bitRate = getBitRate(quality);
                    if (mBitrateSeekbar != null)
                        initSeekBar();
                    break;
                case ParametersConfig.GET_FPS:
                    Logger.e("Misuzu", "ParametersConfig.GET_FPS");
                    fps = praseJson(result.body);
                    break;
                case ParametersConfig.GET_VERSION:
                    Logger.e("Misuzu", "ParametersConfig.GET_VERSION");
                    break;
                case ParametersConfig.GET_RESOLUTION:
                    Logger.e("Misuzu", "ParametersConfig.GET_RESOLUTION");
                    resolution = praseJson(result.body);
                    break;
                case ParametersConfig.SET_RESOLUTION:
                    ToastUtil.show(MyAplication.INSTANCE,MyAplication.INSTANCE.getString(R.string.modify_video_sucess));
                    break;

            }
        }
    };


    /**
     * 获取码率
     */
    private float getBitRate(int quality)
    {
        float value = quality*3000/52.0f;
        value = value/1000;
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(1,BigDecimal.ROUND_DOWN);
        value = bigDecimal.floatValue();
        Logger.e("Misuzu","biteRate ---> "+value +" real quality --->"+quality +" quality -->"+getQuality(value));
        return value;
    }

    /**
     * 获取质量
     */
    private int getQuality(float bitRate)
    {
        float value = bitRate*1000;
        value = value*52/3000;
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(0,BigDecimal.ROUND_HALF_UP);
        value = bigDecimal.intValue();
        return (int) value;
    }


    /**
     * 解析数据
     */
    public int praseJson(String json)
    {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getInt("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 初始化进度条
     */
    public void initSeekBar()
    {
        if (resolution == 1 && (int)(bitRate*10) == 8 && fps == 20)
        {
            switchCheck(mSmoothPart);
        }else if (resolution == 2 && (int)(bitRate*10) == 15 && fps == 25)
        {
            switchCheck(mGoodPart);
        }
        else if (resolution == 3 && bitRate == 5 && fps == 30)
        {
            switchCheck(mBestPart);
        }else
        {
            switchCheck(mCustomPart);
        }
        mLoadingDialogFragment.dismiss();
    }


    public static VideoSettingFragment newInstance() {

        return new VideoSettingFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 切换显示质量块
     */
    @OnClick({R.id.smooth_part, R.id.good_part, R.id.best_part, R.id.custom_part})
    public void onViewClicked(View view) {
            switchCheck(view);
    }

    /**
     * 修改提交点击
     */
    @OnClick(R.id.modify_button)
    public void onModifyButtonClicked(View view)
    {
        if (isInitDevice && mParametersConfig != null)
        {
            mParametersConfig.setFps(0,fps);
            mParametersConfig.setQuality(0,getQuality(bitRate));
            mParametersConfig.setResolution(0,resolution);
        }else
        {
            ToastUtil.show(MyAplication.INSTANCE,MyAplication.INSTANCE.getString(R.string.plz_connect));
        }

    }

    /**
     * 切换视频质量
     */
    private void switchCheck(View view)
    {
        view.setSelected(true);
        if (lastViewSelected != view)
            lastViewSelected.setSelected(false);
        lastViewSelected = view;
        // 切换选项块
        if (view.getId() == R.id.custom_part)
        {
            mFrameRateSeekbar.getConfigBuilder()
                    .thumbColor(ContextCompat.getColor(getContext(),R.color.text_blue))
                    .build();
            mBitrateSeekbar.getConfigBuilder()
                    .thumbColor(ContextCompat.getColor(getContext(),R.color.text_blue))
                    .build();
            mResolutionSeekbar.getConfigBuilder()
                    .thumbColor(ContextCompat.getColor(getContext(),R.color.text_blue))
                    .build();
            mFrameRateSeekbar.setEnabled(true);
            mBitrateSeekbar.setEnabled(true);
            mResolutionSeekbar.setEnabled(true);
        }else
        {
            mFrameRateSeekbar.getConfigBuilder()
                    .thumbColor(ContextCompat.getColor(getContext(),R.color.text_gray))
                    .build();
            mBitrateSeekbar.getConfigBuilder()
                    .thumbColor(ContextCompat.getColor(getContext(),R.color.text_gray))
                    .build();
            mResolutionSeekbar.getConfigBuilder()
                    .thumbColor(ContextCompat.getColor(getContext(),R.color.text_gray))
                    .build();
            mFrameRateSeekbar.setEnabled(false);
            mBitrateSeekbar.setEnabled(false);
            mResolutionSeekbar.setEnabled(false);
        }

        // 设置进度条
        switch (view.getId()) {
            case R.id.smooth_part:
                mResolutionSeekbar.setProgress(480);
                mBitrateSeekbar.setProgress(0.8f);
                mFrameRateSeekbar.setProgress(20);
                resolution = 1;
                fps = 20;
                bitRate = 0.8f;
                break;
            case R.id.good_part:
                mResolutionSeekbar.setProgress(780);
                mBitrateSeekbar.setProgress(1.5f);
                mFrameRateSeekbar.setProgress(25);
                resolution = 2;
                fps = 25;
                bitRate = 1.5f;
                break;
            case R.id.best_part:
                mResolutionSeekbar.setProgress(1080);
                mBitrateSeekbar.setProgress(5);
                mFrameRateSeekbar.setProgress(30);
                resolution = 3;
                fps = 30;
                bitRate = 5f;
                break;
            case R.id.custom_part:
                if (resolution == 0)
                {
                    resolution = 1;
                    fps = 25;
                    bitRate = 4.4f;
                    mResolutionSeekbar.setProgress(780);
                    mBitrateSeekbar.setProgress(4.4f);
                    mFrameRateSeekbar.setProgress(25);
                }else
                {
                    if (resolution == 1)
                        mResolutionSeekbar.setProgress(480);
                    else if (resolution == 2)
                        mResolutionSeekbar.setProgress(780);
                    else if (resolution == 3)
                        mResolutionSeekbar.setProgress(1080);

                    mBitrateSeekbar.setProgress(bitRate);
                    mFrameRateSeekbar.setProgress(fps);
                }
                break;
        }
    }

    /**
     * 监听滑块进度
     */
    private void setOnProgressChange()
    {
        mResolutionSeekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                    if (progress == 480)
                        resolution = 1;
                    else if (progress == 780)
                        resolution = 2;
                    else
                        resolution = 3;
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });

        mFrameRateSeekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                    fps = progress;
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });

        mBitrateSeekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                    bitRate = progressFloat;
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });
    }
}
