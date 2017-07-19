package com.cloud4magic.freecast.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloud4magic.freecast.MyAplication;
import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.api.ParametersConfig;
import com.cloud4magic.freecast.api.WLANAPI;
import com.cloud4magic.freecast.utils.Logger;
import com.cloud4magic.freecast.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.cloud4magic.freecast.ui.fragment.VideoSettingFragment.isInitDevice;
import static com.cloud4magic.freecast.ui.fragment.VideoSettingFragment.mDeviceIp;

/**
 * 密码设置
 * Date   2017/7/3
 * Editor  Misuzu
 */

public class PasswordSettingFragment extends Fragment {

    @BindView(R.id.wifi_ssid)
    EditText mWifiSsid;
    @BindView(R.id.wifi_status)
    ImageView mWifiStatus;
    @BindView(R.id.current_password_edit)
    EditText mCurrentPasswordEdit;
    @BindView(R.id.display_status_current)
    ImageView mDisplayStatusCurrent;
    @BindView(R.id.new_password_edit)
    EditText mNewPasswordEdit;
    @BindView(R.id.display_status_new)
    ImageView mDisplayStatusNew;
    @BindView(R.id.comfirm_edit)
    EditText mComfirmEdit;
    @BindView(R.id.display_status_comfirm)
    ImageView mDisplayStatusComfirm;
    @BindView(R.id.modify_button)
    TextView mModifyButton;
    @BindView(R.id.forgot_password_button)
    TextView mForgotPasswordButton;
    boolean isForgotClicked;
    boolean isPrepare;
    Unbinder unbinder;

    private ParametersConfig mParametersConfig = null;
    private String mDevicePassword = "admin";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_setting_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        isPrepare = true;
        getActivity().registerReceiver(mWifiChangedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        getWifiConnection();
        initView();
        return view;
    }

    private void initView()
    {
        if (isInitDevice)
        {
            mParametersConfig = new ParametersConfig(mDeviceIp + ":" + 80, mDevicePassword);
            mParametersConfig.setOnResultListener(mConfigListener);
            mParametersConfig.getUsernameAndPassword();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.e("Misuzu","hidden --->"+hidden);
        if (!hidden)
            initView();
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
                case ParametersConfig.RESET_WIFI_PWD:
                    ToastUtil.show(MyAplication.INSTANCE,MyAplication.INSTANCE.getString(R.string.reset_password_sucess));
                    mComfirmEdit.setText("");
                    mNewPasswordEdit.setText("");
                    mCurrentPasswordEdit.setText("");
                    break;
                case ParametersConfig.UPDATE_WIFI_PWD:
                    ToastUtil.show(MyAplication.INSTANCE,MyAplication.INSTANCE.getString(R.string.modify_password_sucess));
                    mComfirmEdit.setText("");
                    mNewPasswordEdit.setText("");
                    mCurrentPasswordEdit.setText("");
                    break;
            }

        }
    };

    /**
     * 解析数据
     */
    public String praseJson(String json)
    {
        try {
            JSONArray jsonArray = new JSONArray(json);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            return jsonObject.getString("U_PASS");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static PasswordSettingFragment newInstance() {

        return new PasswordSettingFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.display_status_current, R.id.display_status_new, R.id.display_status_comfirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.display_status_current:
                showAndHidePassword(mCurrentPasswordEdit,view);
                break;
            case R.id.display_status_new:
                showAndHidePassword(mNewPasswordEdit,view);
                break;
            case R.id.display_status_comfirm:
                showAndHidePassword(mComfirmEdit,view);
                break;
        }
    }

    /**
     * 修改提交点击
     */
    @OnClick(R.id.modify_button)
    public void onModifyButtonClicked(View view)
    {
        if (isInitDevice && mParametersConfig != null)
        {
            submit();
        }else
        {
            ToastUtil.show(MyAplication.INSTANCE, MyAplication.INSTANCE.getString(R.string.plz_connect));
        }

    }

    @OnClick(R.id.forgot_password_button)
    public void onForgotPassWordClicked(View view)
    {
        if (isInitDevice && mParametersConfig != null)
        {
            mParametersConfig.resetWifiPassword();
        }else
        {
            ToastUtil.show(MyAplication.INSTANCE, MyAplication.INSTANCE.getString(R.string.plz_connect));
        }
    }

    /**
     * 提交修改密码
     */
    private void submit() {
        String newPass = mNewPasswordEdit.getText().toString();
        String comfirm = mComfirmEdit.getText().toString();
//        if (TextUtils.isEmpty(mDevicePassword)) {
//            ToastUtil.show(MyAplication.INSTANCE,MyAplication.INSTANCE.getString(R.string.current_pass));
//            return;
//        }
        if (TextUtils.isEmpty(newPass)) {
            ToastUtil.show(MyAplication.INSTANCE,MyAplication.INSTANCE.getString(R.string.input_newpass));
            return;
        }
        if (TextUtils.isEmpty(comfirm)) {
            ToastUtil.show(MyAplication.INSTANCE,MyAplication.INSTANCE.getString(R.string.input_comfirm));
            return;
        }

        if (!comfirm.equals(newPass))
        {
            ToastUtil.show(MyAplication.INSTANCE,MyAplication.INSTANCE.getString(R.string.input_not_consistent));
            return;
        }

        mParametersConfig.updateWifiPassword(newPass);
        mDevicePassword = newPass;
    }

    /**
     *  隐藏和显示密码
     */
    private void showAndHidePassword(EditText editText,View status)
    {
        if (status.isSelected())
        {
            status.setSelected(false);
            editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else
        {
            status.setSelected(true);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        editText.setSelection(editText.getText().toString().length());
    }

    /**
     * broadcastReceiver, when wifi connection changed
     */
    private BroadcastReceiver mWifiChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getWifiConnection();
        }
    };

    /**
     * wifi connection
     */
    private void getWifiConnection() {
        WLANAPI wlanapi = new WLANAPI(getContext());
        String ssid = wlanapi.getSSID();
        if (!"NULL".equals(ssid) && !TextUtils.isEmpty(ssid) && ssid.length() > 2) {
            ssid = ssid.substring(1, ssid.length() - 1);
            if (!"NULL".equals(ssid) && !TextUtils.isEmpty(ssid) && !ssid.contains("unknown ssid")) {
                mWifiSsid.setText(ssid);
                mWifiStatus.setSelected(true);
            } else {
                mWifiSsid.setText(getResources().getString(R.string.no_wifi));
                mWifiStatus.setSelected(false);
            }
        } else {
            mWifiSsid.setText(getResources().getString(R.string.no_wifi));
            mWifiStatus.setSelected(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mWifiChangedReceiver);
    }
}
