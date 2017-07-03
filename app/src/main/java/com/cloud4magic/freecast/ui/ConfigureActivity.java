package com.cloud4magic.freecast.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloud4magic.freecast.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 配置界面
 * Date   2017/7/3
 * Editor  Misuzu
 */

public class ConfigureActivity extends AppCompatActivity {

    @BindView(R.id.configure_back)
    ImageView mConfigureBack;
    @BindView(R.id.video_part)
    TextView mVideoPart;
    @BindView(R.id.password_part)
    TextView mPasswordPart;
    @BindView(R.id.configure_content)
    FrameLayout mConfigureContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_layout);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.configure_back, R.id.video_part, R.id.password_part})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.configure_back:
                finish();
                break;
            case R.id.video_part:
                mVideoPart.setSelected(true);
                mPasswordPart.setSelected(false);
                break;
            case R.id.password_part:
                mPasswordPart.setSelected(true);
                mVideoPart.setSelected(false);
                break;
        }
    }
}
