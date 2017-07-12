package com.cloud4magic.freecast.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.ui.fragment.PhotoFragment;
import com.cloud4magic.freecast.ui.fragment.VideoFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Library Photo Videos
 * Date    2017/7/10
 * Author  xiaomao
 */
public class LibraryActivity extends AppCompatActivity {

    private Unbinder mUnbinder;
    @BindView(R.id.library_photo)
    TextView mPhotoView;
    @BindView(R.id.library_video)
    TextView mVideoView;
    @BindView(R.id.library_edit)
    TextView mEditView;
    @BindView(R.id.library_bottom)
    RelativeLayout mBottomView;

    private FragmentManager mFragmentManager;
    private PhotoFragment mPhotoFragment;
    private VideoFragment mVideoFragment;

    private boolean mSelected = false;
    private boolean mIsPhoto = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        mUnbinder = ButterKnife.bind(this);
        // init
        mFragmentManager = getSupportFragmentManager();
        mPhotoFragment = PhotoFragment.getInstance();
        mVideoFragment = VideoFragment.getInstance();
        // default select PhotoFragment
        mIsPhoto = true;
        mPhotoView.setSelected(true);
        mVideoView.setSelected(false);
        mFragmentManager.beginTransaction().add(R.id.library_content, mPhotoFragment).commit();
        setSelectDisable();
    }

    @OnClick(R.id.library_back)
    protected void actionBack() {
        finish();
    }

    @OnClick(R.id.library_photo)
    protected void actionPhoto() {
        if (mIsPhoto) {
            return;
        }
        mIsPhoto = true;
        mPhotoView.setSelected(true);
        mVideoView.setSelected(false);
        mFragmentManager.beginTransaction().show(mPhotoFragment).hide(mVideoFragment).commit();
        resetSelect();
    }

    @OnClick(R.id.library_video)
    protected void actionVideo() {
        if (!mIsPhoto) {
            return;
        }
        mIsPhoto = false;
        mVideoView.setSelected(true);
        mPhotoView.setSelected(false);
        if (mVideoFragment.isAdded()) {
            mFragmentManager.beginTransaction().show(mVideoFragment).hide(mPhotoFragment).commit();
        } else {
            mFragmentManager.beginTransaction().hide(mPhotoFragment).add(R.id.library_content, mVideoFragment).commit();
        }
        resetSelect();
    }

    @OnClick(R.id.library_edit)
    protected void actionEdit() {
        if (mEditView.isSelected()) {
            setSelectDisable();
        } else {
            setSelectEnable();
        }
    }

    @OnClick(R.id.library_delete)
    protected void actionDelete() {
        if (mIsPhoto) {
            if (mPhotoFragment != null) {
                mPhotoFragment.delete();
            }
        } else {
            if (mVideoFragment != null) {
                mVideoFragment.delete();
            }
        }
    }

    @OnClick(R.id.library_share)
    protected void actionShare() {
        if (mIsPhoto) {
            if (mPhotoFragment != null) {
                mPhotoFragment.share();
            }
        } else {
            if (mVideoFragment != null) {
                mVideoFragment.share();
            }
        }
    }

    /**
     * select enable
     */
    private void setSelectEnable() {
        mEditView.setText(getResources().getString(R.string.library_cancel));
        mEditView.setSelected(true);
        mSelected = true;
        if (mBottomView != null) {
            mBottomView.setVisibility(View.VISIBLE);
        }
        notifyAdapter();
    }

    /**
     * select disable
     */
    private void setSelectDisable() {
        mEditView.setText(getResources().getString(R.string.library_select));
        mEditView.setSelected(false);
        mSelected = false;
        if (mBottomView != null) {
            mBottomView.setVisibility(View.GONE);
        }
        notifyAdapter();
    }

    /**
     * notify MediaAdapter
     */
    private void notifyAdapter() {
        if (mIsPhoto) {
            // PhotoFragment
            if (mPhotoFragment != null) {
                mPhotoFragment.setSelect(mSelected);
            }
        } else {
            // VideoFragment
            if (mVideoFragment != null) {
                mVideoFragment.setSelect(mSelected);
            }
        }
    }

    /**
     * reset select
     */
    private void resetSelect() {
        mEditView.setText(getResources().getString(R.string.library_select));
        mEditView.setSelected(false);
        mSelected = false;
        if (mBottomView != null) {
            mBottomView.setVisibility(View.GONE);
        }
        if (mPhotoFragment != null) {
            mPhotoFragment.setSelect(false);
        }
        if (mVideoFragment != null) {
            mVideoFragment.setSelect(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (mSelected) {
            setSelectDisable();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
