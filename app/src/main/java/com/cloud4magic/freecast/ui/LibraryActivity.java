package com.cloud4magic.freecast.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cloud4magic.freecast.MyApplication;
import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.pop.CheckNetworkPopupWindow;
import com.cloud4magic.freecast.pop.SharePopupWindow;
import com.cloud4magic.freecast.ui.fragment.PhotoFragment;
import com.cloud4magic.freecast.ui.fragment.VideoFragment;
import com.cloud4magic.freecast.utils.Logger;
import com.cloud4magic.freecast.utils.NetworkUtils;
import com.cloud4magic.freecast.utils.ToastUtil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.lang.ref.SoftReference;

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

    private static final String TAG = LibraryActivity.class.getSimpleName();

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

    private CheckNetworkPopupWindow mNetworkPopupWindow;
    private SharePopupWindow mSharePopupWindow;
    private boolean mNetworkOnline = false;
    // facebook
    private ShareDialog mShareDialogFacebook;
    private CallbackManager mCallbackManager;

    private static final int CHECK_NETWORK_END = 100;
    private MyHandler mHandler;
    private static class MyHandler extends Handler {
        private SoftReference<LibraryActivity> softReference;

        public MyHandler (LibraryActivity activity) {
            softReference = new SoftReference<LibraryActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LibraryActivity activity = softReference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case CHECK_NETWORK_END:
                    openShareWindow(activity);
                    break;
            }
        }
    }

    private static void openShareWindow(LibraryActivity activity) {
        activity.mNetworkPopupWindow.close();
        // network disable
        if (!activity.mNetworkOnline) {
            ToastUtil.show(activity, activity.getResources().getString(R.string.network_disable));
            return;
        }
        if (activity.mIsPhoto) {
            if (activity.mPhotoFragment != null && activity.mPhotoFragment.canShare()) {
                activity.showSharePopupWindow(activity.mPhotoFragment.getSharePath());
            }
        } else {
            if (activity.mVideoFragment != null && activity.mVideoFragment.canShare()) {
                activity.showSharePopupWindow(activity.mVideoFragment.getSharePath());
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        mUnbinder = ButterKnife.bind(this);
        mHandler = new MyHandler(this);
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
        // init facebook
        initFacebook();
    }

    @OnClick(R.id.library_back)
    protected void actionBack() {
        if (mSelected) {
            setSelectDisable();
        } else {
            finish();
        }
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
        // show dialog, and check the network connection
        if (!mNetworkOnline) {
            if (mNetworkPopupWindow == null) {
                mNetworkPopupWindow = new CheckNetworkPopupWindow(this);
            }
            mNetworkPopupWindow.showAtBottom();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean isConnected = NetworkUtils.isNetworkConnected(getApplicationContext());
                    boolean isAvailable = NetworkUtils.isNetworkAvailable(getApplicationContext());
//                    boolean isOnline = NetworkUtils.isNetworkOnline();
                    mNetworkOnline = isConnected && isAvailable;// && isOnline;
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(CHECK_NETWORK_END);
                    }
                }
            }).start();
        } else {
            if (mIsPhoto) {
                if (mPhotoFragment != null && mPhotoFragment.canShare()) {
                    showSharePopupWindow(mPhotoFragment.getSharePath());
                }
            } else {
                if (mVideoFragment != null && mVideoFragment.canShare()) {
                    showSharePopupWindow(mVideoFragment.getSharePath());
                }
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

    /**
     * display popup window of share
     */
    private void showSharePopupWindow(String path) {
        MyApplication.getKeyHash(getApplicationContext());
        // show share window
        if (mSharePopupWindow == null) {
            mSharePopupWindow = new SharePopupWindow(LibraryActivity.this);
            mSharePopupWindow.setOnPlatformListener(new SharePopupWindow.OnPlatformListener() {
                @Override
                public void onYoutube(String path) {
                    if (TextUtils.isEmpty(path)) {
                        ToastUtil.show(LibraryActivity.this, getResources().getString(R.string.share_to_youtube_failure));
                        Logger.d(TAG, "the share path is null");
                        return;
                    }
                    shareVideoToYoutube(path);
                }

                @Override
                public void onFacebook(String path) {
                    if (TextUtils.isEmpty(path)) {
                        ToastUtil.show(LibraryActivity.this, getResources().getString(R.string.share_to_facebook_failure));
                        Logger.d(TAG, "the share path is null");
                        return;
                    }
                    if (mIsPhoto) {
                        sharePhotoToFacebook(path);
                    } else {
                        shareVideoToFacebook(path);
                    }
                }

                @Override
                public void onInstagram(String path) {
                    if (TextUtils.isEmpty(path)) {
                        ToastUtil.show(LibraryActivity.this, getResources().getString(R.string.share_to_instagram_failure));
                        Logger.d(TAG, "the share path is null");
                        return;
                    }
                    if (mIsPhoto) {
                        sharePhotoToInstagram(path);
                    } else {
                        shareVideoToInstagram(path);
                    }
                }
            });
        }
        mSharePopupWindow.update(path, mIsPhoto);
        mSharePopupWindow.showAtBottom();
    }

    /**
     * init facebook share
     */
    private void initFacebook() {
        mShareDialogFacebook = new ShareDialog(this);
        mCallbackManager = CallbackManager.Factory.create();
        mShareDialogFacebook.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                ToastUtil.show(LibraryActivity.this, getResources().getString(R.string.share_to_facebook_success));
                Logger.e(TAG, "share to facebook success: postId == " + result.getPostId());
            }

            @Override
            public void onCancel() {
                ToastUtil.show(LibraryActivity.this, getResources().getString(R.string.share_to_facebook_failure));
                Logger.e(TAG, "cancel share to facebook");
            }

            @Override
            public void onError(FacebookException error) {
                ToastUtil.show(LibraryActivity.this, getResources().getString(R.string.share_to_facebook_failure));
                Logger.e(TAG, error.toString());
                Logger.e(TAG, error.getMessage());
                Logger.e(TAG, error.getLocalizedMessage());
            }
        });
    }

    /**
     * share photo to facebook
     * size < 12M
     */
    private void sharePhotoToFacebook(String path) {
        Glide.with(this).load(path).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                if (bitmap != null) {
                    SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(bitmap).build();
                    SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder().addPhoto(sharePhoto).build();
                    if (mShareDialogFacebook != null && ShareDialog.canShow(SharePhotoContent.class)) {
                        mShareDialogFacebook.show(sharePhotoContent);
                    }
                }
            }
        });
    }

    /**
     * share video to facebook
     * size < 12M
     */
    private void shareVideoToFacebook(String path) {
        Uri uri = Uri.fromFile(new File(path));
        ShareVideo shareVideo = new ShareVideo.Builder().setLocalUrl(uri).build();
        ShareVideoContent shareVideoContent = new ShareVideoContent.Builder().setVideo(shareVideo).build();
        if (mShareDialogFacebook != null && ShareDialog.canShow(ShareVideoContent.class)) {
            mShareDialogFacebook.show(shareVideoContent);
        }
    }

    /**
     * share video to youtube
     */
    private void shareVideoToYoutube(String path) {
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);
        // Set package of Instagram
        share.setPackage("com.google.android.youtube");
        // Set the MIME type
        share.setType("video/*");
        // Create the URI from the media
        File media = new File(path);
        Uri uri = Uri.fromFile(media);
        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

    /**
     * share photo to instagram
     */
    private void sharePhotoToInstagram(String path) {
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);
        // Set package of Instagram
        share.setPackage("com.instagram.android");
        // Set the MIME type
        share.setType("image/*");
        // Create the URI from the media
        File media = new File(path);
        Uri uri = Uri.fromFile(media);
        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

    /**
     * share video to instagram
     * Minimum Duration	3 seconds
     * Maximum Duration	10 minutes
     * Video Format	mkv, mp4
     * Minimum Dimensions	640x640 pixels
     */
    private void shareVideoToInstagram(String path) {
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);
        // Set package of Instagram
        share.setPackage("com.instagram.android");
        // Set the MIME type
        share.setType("video/*");
        // Create the URI from the media
        File media = new File(path);
        Uri uri = Uri.fromFile(media);
        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCallbackManager != null) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
