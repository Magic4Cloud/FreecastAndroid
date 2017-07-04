package com.cloud4magic.freecast.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloud4magic.freecast.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_setting_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
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
