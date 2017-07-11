package com.cloud4magic.freecast.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloud4magic.freecast.R;
import com.cloud4magic.freecast.adapter.PhotoAdapter;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Library Video
 * Date    2017/7/10
 * Author  xiaomao
 */
public class VideoFragment extends Fragment {

    private Unbinder mUnbinder;
    private PhotoAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {

    }

    public void setSelect(boolean select) {
        if (mAdapter != null) {
            mAdapter.setSelect(select);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public static VideoFragment getInstance() {
        return new VideoFragment();
    }
}
