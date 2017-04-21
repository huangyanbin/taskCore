package com.mewifi.taskcore;


/**
 * Created by David on 2017/4/21.
 */

public interface Action<T> {
    /**
     * 处理事务
     * @return
     */
     T onHandle();
}
