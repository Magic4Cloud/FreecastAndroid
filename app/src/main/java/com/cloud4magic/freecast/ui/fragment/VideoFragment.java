package com.cloud4magic.freecast.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.adapter.MediaAdapter;
import com.cloud4magic.freecast.bean.LibraryBean;
import com.cloud4magic.freecast.bean.MediaBean;
import com.cloud4magic.freecast.utils.Logger;
import com.cloud4magic.freecast.utils.ToastUtil;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Library Video
 * Date    2017/7/10
 * Author  xiaomao
 */
public class VideoFragment extends Fragment {

    private Unbinder mUnbinder;
    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;
    private List<MediaBean> mList;
    private MediaAdapter mAdapter;
    private List<String> mSelectedData;
    private AlertDialog mAlertDialog;

    private static final int LOAD_SUCCESS = 100;
    private MyHandler mHandler;
    private static class MyHandler extends Handler {

        private SoftReference<VideoFragment> mSoftReference;

        public MyHandler(VideoFragment fragment) {
            mSoftReference = new SoftReference<VideoFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VideoFragment fragment = mSoftReference.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {
                case LOAD_SUCCESS:
                    if (fragment.mAdapter != null) {
                        fragment.mAdapter.setData(fragment.mList);
                    }
                    break;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    /**
     * init
     */
    private void initView() {
        mHandler = new MyHandler(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MediaAdapter(getContext());
        mAdapter.setOnItemClickListener(new MediaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String path) {
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                File file = new File(path);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "video/*");
                startActivity(intent);
            }

            @Override
            public void onItemSelected(boolean selected, int position, String path) {
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                if (mSelectedData == null) {
                    mSelectedData = new ArrayList<String>();
                }
                if (selected) {
                    // add to list
                    if (!mSelectedData.contains(path)) {
                        mSelectedData.add(path);
                    }
                } else {
                    // remove form list
                    if (mSelectedData.contains(path)) {
                        mSelectedData.remove(path);
                    }
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        // load
        loadVideo();
    }

    public void setSelect(boolean select) {
        if (mAdapter != null) {
            mAdapter.setSelect(select);
        }
    }

    /**
     * delete video form sd
     */
    public void delete() {
        if (mSelectedData == null || mSelectedData.size() == 0) {
            ToastUtil.show(getContext(), getResources().getString(R.string.delete_no_video));
            return;
        }
        if (mAlertDialog == null) {
            mAlertDialog = getAlertDialog();
        }
        mAlertDialog.show();
    }

    /**
     * share video to other platforms
     */
    public void share() {

    }

    /**
     * create AlertDialog
     */
    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.delete_notice);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (String path : mSelectedData) {
                    if (!TextUtils.isEmpty(path)) {
                        File file = new File(path);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                ToastUtil.show(getContext(), getResources().getString(R.string.delete_success));
                loadVideo();
            }
        });
        return builder.create();
    }

    /**
     * load file in work thread
     */
    private void loadVideo() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getFiles();
            }
        });
        thread.start();
    }

    /**
     * get video from sd
     */
    private void getFiles() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/FREESTREAM/Video";
        if (TextUtils.isEmpty(path)) {
            Logger.e("xmzd", "Video file path is null");
            return;
        }
        File dir = new File(path);
        if (!dir.exists()) {
            Logger.e("xmzd", "Video file is not exists");
            return;
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            Logger.e("xmzd", "Video is not found in the path");
            return;
        }
        mList = new ArrayList<MediaBean>();
        List<String> keys = new ArrayList<String>();
        List<LibraryBean> beans = new ArrayList<LibraryBean>();
        for (File file : files) {
            if (file != null) {
                String name = file.getName();
                if (!TextUtils.isEmpty(name) && name.startsWith("VIDEO_")) {
                    String key = name.split("_")[1];
                    if (!keys.contains(key)) {
                        keys.add(key);
                    }
                    LibraryBean bean = new LibraryBean();
                    bean.setName(key);
                    bean.setPath(file.getPath());
                    beans.add(bean);
                }
            }
        }
        for (String key : keys) {
            if (!TextUtils.isEmpty(key)) {
                List<String> list = new ArrayList<String>();
                for (LibraryBean bean : beans) {
                    if (bean != null && key.equals(bean.getName())) {
                        list.add(bean.getPath());
                    }
                }
                if (list.size() > 0) {
                    MediaBean mediaBean = new MediaBean();
                    mediaBean.setName(key);
                    mediaBean.setList(list);
                    mList.add(mediaBean);
                }
            }
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessage(LOAD_SUCCESS);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    public static VideoFragment getInstance() {
        return new VideoFragment();
    }
}
