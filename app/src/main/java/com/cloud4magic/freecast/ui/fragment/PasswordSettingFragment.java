package com.cloud4magic.freecast.ui.fragment;

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
import com.cloud4magic.freecast.utils.Logger;
import com.cloud4magic.freecast.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.cloud4magic.freecast.ui.fragment.VideoSettingFragment.mDeviceIp;
import static com.cloud4magic.freecast.ui.fragment.VideoSettingFragment.mInitDevice;

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
    Unbinder unbinder;

    private ParametersConfig mParametersConfig = null;
    private String mDevicePassword = "admin";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_setting_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView()
    {
        if (mInitDevice)
        {
            mParametersConfig = new ParametersConfig(mDeviceIp + ":" + 80, mDevicePassword);
            mParametersConfig.setOnResultListener(mConfigListener);
            mParametersConfig.getUsernameAndPassword();
        }
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
                    mDevicePassword = praseJson(result.body);
                    mCurrentPasswordEdit.setText(mDevicePassword);
                    break;
                case ParametersConfig.UPDATE_USERNAME_PASSWORD:
                    ToastUtil.show(MyAplication.INSTANCE,MyAplication.INSTANCE.getString(R.string.modify_password_sucess));
                    mComfirmEdit.setText("");
                    mNewPasswordEdit.setText("");
                    mCurrentPasswordEdit.setText(mDevicePassword);
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

    @OnClick({R.id.display_status_current, R.id.display_status_new, R.id.display_status_comfirm, R.id.modify_button, R.id.forgot_password_button})
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
            case R.id.modify_button:
                break;
            case R.id.forgot_password_button:
                break;
        }
    }

    /**
     * 修改提交点击
     */
    @OnClick(R.id.modify_button)
    public void onModifyButtonClicked(View view)
    {
        if (mInitDevice && mParametersConfig != null)
        {
            submit();
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

        mParametersConfig.updateUsernameAndPassword("admin",newPass);
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
}
