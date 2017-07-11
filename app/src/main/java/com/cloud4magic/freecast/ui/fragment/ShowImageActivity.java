package com.cloud4magic.freecast.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cloud4magic.freecast.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Date    2017/7/11
 * Author  xiaomao
 */
public class ShowImageActivity extends AppCompatActivity {

    public static final String INTENT_FLAG_PATH = "intent_flag_path";
    private Unbinder mUnbinder;
    @BindView(R.id.show_image)
    ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        mUnbinder = ButterKnife.bind(this);
        String path = getIntent().getStringExtra(INTENT_FLAG_PATH);
        if (TextUtils.isEmpty(path)) {
            finish();
        } else {
            Glide.with(this).load(path).into(mImageView);
        }
    }

    @OnClick(R.id.show_back)
    protected void actionBack() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
