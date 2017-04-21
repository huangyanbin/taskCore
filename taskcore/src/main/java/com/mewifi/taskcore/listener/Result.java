package com.mewifi.taskcore.listener;

/**
 * Created by David on 2017/4/21.
 */

public interface Result<T> {

     void onStart();
    void onGetData(T t);
    void onFinish();
}
