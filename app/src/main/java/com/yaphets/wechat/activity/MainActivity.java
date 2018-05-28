package com.yaphets.wechat.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.asynctask.PullMsgTask;
import com.yaphets.wechat.database.entity.UserInfo;
import com.yaphets.wechat.service.HeartBeatService;
import com.yaphets.wechat.ui.fragment.BaseFragment;
import com.yaphets.wechat.ui.fragment.FragmentFactory;
import com.yaphets.wechat.ui.view.HTextBadge;
import com.yaphets.wechat.ui.view.MyViewPager;
import com.yaphets.wechat.util.RequestParam;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int CODE_MODIFY_DATA = 1;

    private String[] mTilte = {"微信", "通讯录", "发现"};
    private int[] mIcon = {R.drawable.tab_message, R.drawable.tab_contact, R.drawable.tab_discovery};

    private ViewPager mViewPager;
    private BottomNavigationBar mBottomNavigationBar;
    private HTextBadge mMsgNotifyBadge;
    private HTextBadge mApplyNotifyBadge;

    private UserInfo _userInfo;
    private View _headerView;

    private DrawerLayout _drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

        Intent intent = new Intent(this, HeartBeatService.class);
        startService(intent);
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(getDrawable(R.drawable.ma_tb_overflow));
        setSupportActionBar(toolbar);

        _drawerLayout = findViewById(R.id.ma_drawer_layout);

        //BottomNavigationBar 和 ViewPager
        mBottomNavigationBar = findViewById(R.id.ma_navigation_bar);

        mViewPager = findViewById(R.id.ma_viewpager);
        ClientApp.setAttribute("viewPager", mViewPager);
    }

    private void initData() {
        //tool bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ma_tb_menu);
        }

        //init drawer layout
        _userInfo = ClientApp.get_loginUserinfo();

        _drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                ((MyViewPager)ClientApp.getAttribute("viewPager")).setNoScroll(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                ((MyViewPager)ClientApp.getAttribute("viewPager")).setNoScroll(false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        NavigationView navView = findViewById(R.id.nav_view);
        _headerView = navView.getHeaderView(0);
        CircleImageView vThumb = _headerView.findViewById(R.id.nav_thumb);
        TextView vNickname = _headerView.findViewById(R.id.nav_nickname);
        TextView vDesc = _headerView.findViewById(R.id.nav_description);
        vThumb.setImageBitmap(_userInfo.get_thumb());
        vNickname.setText(_userInfo.get_nickname());
        vDesc.setText(_userInfo.get_desc());

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //TODO 处理菜单点击逻辑
                switch (item.getItemId()) {
                    case R.id.nav_individualData:
                        Intent intent = new Intent(MainActivity.this, IndividualActivity.class);
                        startActivityForResult(intent, CODE_MODIFY_DATA);
                        break;
                    case R.id.nav_commonSetting:
                        break;
                    case R.id.nav_about:
                        break;
                    case R.id.nav_logout:
                        onLogout();
                        break;
                    default:
                }

                _drawerLayout.closeDrawers();
                return true;
            }
        });

        //navigation bar
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        mMsgNotifyBadge = new HTextBadge();
        mMsgNotifyBadge.hide().setBackgroundColor(Color.RED);//设置角标内容
        mApplyNotifyBadge = new HTextBadge();
        mApplyNotifyBadge.hide().setBackgroundColor(Color.RED);

        ClientApp.setMsgNotifyBadge(mMsgNotifyBadge);
        ClientApp.setApplyNotifyBadge(mApplyNotifyBadge);

        mBottomNavigationBar
                .setActiveColor(R.color.colorSelected) //设置选中的颜色
                .setInActiveColor(R.color.colorPrimary);//未选中颜色

        mBottomNavigationBar.addItem(new BottomNavigationItem(mIcon[0], mTilte[0]).setBadgeItem(mMsgNotifyBadge))
                .addItem(new BottomNavigationItem(mIcon[1], mTilte[1]).setBadgeItem(mApplyNotifyBadge))
                .addItem(new BottomNavigationItem(mIcon[2], mTilte[2]))
                .initialise();
        //设置点击事件
        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){

            @Override
            public void onTabSelected(int position) {
                if (position == 0) {
                    mMsgNotifyBadge.setNumber(0);
                    if (!mMsgNotifyBadge.isHidden())
                        mMsgNotifyBadge.hide();
                }
                if (mViewPager.getCurrentItem() != position) {
                    mViewPager.setCurrentItem(position, false);
                }

            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mBottomNavigationBar.getCurrentSelectedPosition() != position) {
                    mBottomNavigationBar.selectTab(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ClientApp._handler.postDelayed(new PullMsgTask()::execute, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent.getBooleanExtra("receiveMsg", false)) {
            //跳转到消息页面
            //mViewPager.setCurrentItem(1);
            mBottomNavigationBar.selectTab(0);
        }

        Log.d(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*Intent intent = new Intent(this, HeartBeatService.class);
        stopService(intent);*/

        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_MODIFY_DATA:
                    CircleImageView vThumb = _headerView.findViewById(R.id.nav_thumb);
                    TextView vNickname = _headerView.findViewById(R.id.nav_nickname);
                    TextView vDesc = _headerView.findViewById(R.id.nav_description);
                    vThumb.setImageBitmap(_userInfo.get_thumb());
                    vNickname.setText(_userInfo.get_nickname());
                    vDesc.setText(_userInfo.get_desc());
                    break;
                default:
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ma_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(this, "搜索功能敬请期待", Toast.LENGTH_SHORT).show();
                break;
            case R.id.addfriend:
                //添加好友
                Intent intent = new Intent(MainActivity.this, SearchFriendActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                onLogout();
                Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "设置功能敬请期待", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                _drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    /**
     * 防止pendingIntent传递的intent收不到
     * @param intent
     *      新intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * back键不关闭activity
     * @param event
     * 虚拟键事件
     * @return
     * 是否处理完成
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            //具体的操作代码
            moveTaskToBack(false);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 注销当前用户
     */
    private void onLogout() {
        SharedPreferences shared = getSharedPreferences("lastest_login", Context.MODE_PRIVATE);
        String ac = shared.getString(RequestParam.USERNAME,"");
        SharedPreferences saveShared = getSharedPreferences(ac, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = saveShared.edit();
        //清除在线状态
        editor.putInt(RequestParam.STATUS, RequestParam.OFFLINE);
        //清除密码
        editor.putString(RequestParam.PASSWORD, null);
        editor.apply();

        ClientApp.set_loginUserinfo(null);
        ClientApp._password = null;

        //清除数据
        ClientApp.clearCacheData();

        Intent stopitent = new Intent(this, HeartBeatService.class);
        stopService(stopitent);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {


        MainPagerAdapter(FragmentManager fm) {
            super(fm);
            //mTilte = getResources().getStringArray(R.array.tab_short_Title);
            //images = new int[]{R.drawable.tab_home, R.drawable.tab_message};
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public BaseFragment getItem(int position) {
            return FragmentFactory.createFragment(position);
        }

        @Override
        public int getCount() {
            return FragmentFactory.FRAGMENT_COUNT;
        }
    }
}
