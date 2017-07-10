package com.cloud4magic.freecast.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.api.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 各种声明界面
 * Date   2017/7/10
 * Editor  Misuzu
 */

public class DeclareActivity extends AppCompatActivity {

    @BindView(R.id.declare_back)
    ImageView mDeclareBack;
    @BindView(R.id.declare_title)
    TextView mDeclareTitle;
    @BindView(R.id.declare_content)
    TextView mDeclareContent;
    int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declare_layout);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra(Constants.TYPE,0);
        loadStringByType(type);
    }

    public static void startActivity(Activity activity,int type)
    {
        Intent intent = new Intent(activity,DeclareActivity.class);
        intent.putExtra(Constants.TYPE,type);
        activity.startActivity(intent);
    }

    private void loadStringByType(int type)
    {
        switch (type)
        {
            case 0:
                mDeclareContent.setText(Constants.DISCLAIMER);
                mDeclareTitle.setText(getResources().getText(R.string.disclaimer));
                break;
            case 1:
                mDeclareContent.setText(Constants.PRIVACY_POLICY);
                mDeclareTitle.setText(getResources().getText(R.string.privacy_policy));
                break;
            case 2:
                mDeclareContent.setText(Constants.COPY_RIGHT);
                mDeclareTitle.setText(getResources().getText(R.string.copyright));
                break;
        }
    }

    @OnClick(R.id.declare_back)
    public void onViewClicked() {
        finish();
    }
}
