package com.yaphets.wechat.ui.fragment;


import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yaphets.wechat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoveryFragment extends BaseFragment {


    public DiscoveryFragment() {
        // Required empty public constructor
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

}
