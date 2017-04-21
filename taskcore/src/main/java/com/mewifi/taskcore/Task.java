package com.mewifi.taskcore;

/**
 * Created by David on 2017/4/21.
 */

public class Task {

    public static <T> TaskInfo<T> create(Action<T> action){

      return create(action,null);
    }

    public static <T> TaskInfo<T> create(Action<T> action,TaskOption option){

        return (TaskInfo<T>) TaskLoader.getInstance().createTask(action,option);
    }


}
