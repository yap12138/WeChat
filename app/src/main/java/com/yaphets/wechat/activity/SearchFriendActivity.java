package com.yaphets.wechat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.asynctask.SearchFriendTask;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.util.HttpCallbackListener;

public class SearchFriendActivity extends AppCompatActivity {
    private EditText _searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
        initData();
    }

    private void initView() {
        _searchText = findViewById(R.id.nfa_et_input);
        TextView myInfo = findViewById(R.id.sfa_tv_self);
        String label = "我的微信号：" + ClientApp._username;
        myInfo.setText(label);
    }

    private void initData() {
        _searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //数据库查找Friend
                    String search_username = _searchText.getText().toString();
                    if ("".equals(search_username))     //未输入则不查找
                        return true;
                    new SearchFriendTask(new HttpCallbackListener<Friend>() {
                        @Override
                        public void onFinish(Friend friend) {
                            Intent intent = new Intent(SearchFriendActivity.this, FriendDetailActivity.class);
                            intent.putExtra("friend", friend);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(Friend paramType) {
                            Toast.makeText(SearchFriendActivity.this, "未找到用户", Toast.LENGTH_SHORT).show();
                        }
                    }).execute(search_username);

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }
}
