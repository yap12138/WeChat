package com.yaphets.wechat.util.listener;

public interface HttpCallbackListener<T> {
    void onFinish(T paramType);
    void onError(T paramType);
}
