package com.cloud4magic.freecast.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.adapter.PhotoAdapter;
import com.cloud4magic.freecast.bean.LibraryBean;
import com.cloud4magic.freecast.bean.PhotoBean;
import com.cloud4magic.freecast.utils.Logger;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Library Photo
 * Date    2017/7/10
 * Author  xiaomao
 */
public class PhotoFragment extends Fragment {

    private Unbinder mUnbinder;
    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;
    private List<PhotoBean> mList;
    private PhotoAdapter mAdapter;

    private static final int LOAD_SUCCESS = 100;
    private MyHandler mHandler;
    private static class MyHandler extends Handler {

        private SoftReference<PhotoFragment> mSoftReference;

        public MyHandler(PhotoFragment fragment) {
            mSoftReference = new SoftReference<PhotoFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PhotoFragment fragment = mSoftReference.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {
                case LOAD_SUCCESS:
                    Logger.d("xmzd", "load success ");
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
        mAdapter = new PhotoAdapter(getContext());
        mAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String path) {
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                File file = new File(path);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(intent);
                /*Intent intent = new Intent(getActivity(), ShowImageActivity.class);
                intent.putExtra(ShowImageActivity.INTENT_FLAG_PATH, path);
                startActivity(intent);*/
            }

            @Override
            public void onItemSelected(boolean selected, int position, String path) {
                Logger.e("xmzd", " " + selected + " --- " + path + " --- " + position);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        // load
        loadPhoto();
    }

    public void setSelect(boolean select) {
        if (mAdapter != null) {
            mAdapter.setSelect(select);
        }
    }

    /**
     * load file in work thread
     */
    private void loadPhoto() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getFiles();
            }
        });
        thread.start();
    }

    /**
     * get photo from sd
     */
    private void getFiles() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/FREESTREAM/Photo";
        if (TextUtils.isEmpty(path)) {
            Logger.e("xmzd", "Photo file path is null");
            return;
        }
        File dir = new File(path);
        if (!dir.exists()) {
            Logger.e("xmzd", "Photo file is not exists");
            return;
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            Logger.e("xmzd", "photo is not found in the path");
            return;
        }
        mList = new ArrayList<PhotoBean>();
        List<String> keys = new ArrayList<String>();
        List<LibraryBean> beans = new ArrayList<LibraryBean>();
        for (File file : files) {
            if (file != null) {
                String name = file.getName();
                if (!TextUtils.isEmpty(name) && name.startsWith("IMG_")) {
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
                    PhotoBean photoBean = new PhotoBean();
                    photoBean.setName(key);
                    photoBean.setList(list);
                    mList.add(photoBean);
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

    public static PhotoFragment getInstance() {
        return new PhotoFragment();
    }
}
