package com.yaphets.wechat.util;

public interface HttpCallbackListener<T> {
    void onFinish(T paramType);
    void onError(T paramType);
}
