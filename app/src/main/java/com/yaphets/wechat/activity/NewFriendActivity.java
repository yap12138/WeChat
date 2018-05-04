package com.yaphets.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yaphets.wechat.R;
import com.yaphets.wechat.adapter.ApplyAdapter;
import com.yaphets.wechat.database.entity.Apply;

import java.util.List;

public class NewFriendActivity extends BaseActionBarActivity {

    private RecyclerView _applyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        initView();
        initData();
    }

    private void initView() {
        _applyList = findViewById(R.id.rcv_new_friend);
        ConstraintLayout cst = findViewById(R.id.nfa_cst_search);
        cst.setOnClickListener(v -> {
            Intent intent = new Intent(NewFriendActivity.this, SearchFriendActivity.class);
            startActivity(intent);
        });
    }

    private void initData() {
        //取出添加申请
        List<Apply> applyList = getIntent().getParcelableArrayListExtra("applyList");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        _applyList.setLayoutManager(linearLayoutManager);
        ApplyAdapter adapter = new ApplyAdapter(applyList);
        _applyList.setAdapter(adapter);
        _applyList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }
}
