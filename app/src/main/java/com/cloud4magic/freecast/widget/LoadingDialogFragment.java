package com.cloud4magic.freecast.widget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloud4magic.freecast.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 加载框
 * Date   2017/7/6
 * Editor  Misuzu
 */

public class LoadingDialogFragment extends DialogFragment {

    private static final String ARGS_LOADING_TEXT = "args_loading_text";
    private Unbinder mUnbinder;

    @BindView(R.id.loading_logo)
    ImageView mIvLogo;
    @BindView(R.id.loading_text)
    TextView mTvText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.loading_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String text = bundle.getString(ARGS_LOADING_TEXT);
            if (!TextUtils.isEmpty(text)) {
                mTvText.setText(text);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        if (win != null) {
            // 一定要设置Background，如果不设置，window属性设置无效
            win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

            WindowManager.LayoutParams params = win.getAttributes();
            // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            win.setAttributes(params);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    public static LoadingDialogFragment newInstance() {
        return new LoadingDialogFragment();
    }

    public static LoadingDialogFragment newInstance(String loadingText) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_LOADING_TEXT, loadingText);
        LoadingDialogFragment fragment = new LoadingDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
