package com.yaphets.wechat.util;

import android.app.Activity;

import java.lang.ref.WeakReference;

public class MyActivityManager {
    private static MyActivityManager mMyActivityManager;
    // 采用弱引用持有 Activity ，避免造成 内存泄露
    private WeakReference<Activity> mCurrentActivityWeakRef;

    private MyActivityManager() {
    }

    public static MyActivityManager getInstance() {
        if (mMyActivityManager == null) {
            mMyActivityManager = new MyActivityManager();
        }

        return mMyActivityManager;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (mCurrentActivityWeakRef != null) {
            currentActivity = mCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        mCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }
}
