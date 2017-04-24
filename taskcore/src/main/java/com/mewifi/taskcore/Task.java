package com.mewifi.taskcore;

/**
 * Created by David on 2017/4/21.
 */

public class Task {

    public static <T> TaskInfo<T> create(Action<T> action){

      return create(action,null);
    }

    public static <T> TaskInfo<T> create(Action<T> action,Object tag){

        return create(action,null,tag);
    }

    public static <T> TaskInfo<T> create(Action<T> action,TaskOption option,Object tag){

        return (TaskInfo<T>) TaskLoader.getInstance().createTask(action,option,tag);
    }

    /**
     * 取消tag的任务
     * @param tag
     */
    public static void cancel(Object tag){

        TaskLoader.getInstance().cancelTask(tag);
    }


}
