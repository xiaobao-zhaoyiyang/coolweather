package com.coolweather.util;

/**
 * Created by yo on 2016/6/15.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
