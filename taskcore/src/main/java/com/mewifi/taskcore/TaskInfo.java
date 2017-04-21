package com.mewifi.taskcore;

import com.mewifi.taskcore.listener.Result;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 任务信息
 * @param <T>
 */
public class TaskInfo<T> {


    final Action<T> action;
    final TaskOption options;
    Result<T> result;
    final ReentrantLock loadFromUriLock;

    public TaskInfo(Action<T> taskRunnable, TaskOption options,
                    ReentrantLock loadFromUriLock) {
        this.options = options;
        this.action = taskRunnable;
        this.loadFromUriLock = loadFromUriLock;
    }

    public TaskInfo(Action<T> taskRunnable,TaskOption options, Result<T> result,
                    ReentrantLock loadFromUriLock) {
        this.options = options;
        this.result = result;
        this.action = taskRunnable;
        this.loadFromUriLock = loadFromUriLock;
    }

    public void execute(Result<T> result){
        this.result = result;
        TaskLoader.getInstance().exeTask(this);
    }
}
