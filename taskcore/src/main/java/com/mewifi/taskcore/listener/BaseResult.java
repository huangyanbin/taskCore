package com.mewifi.taskcore.listener;

import com.mewifi.taskcore.listener.Result;

/**
 * Created by David on 2017/4/21.
 */

public abstract class BaseResult<T> implements Result<T> {

    @Override
    public void onStart() {
    }

    @Override
    public void onFinish() {

    }
}
