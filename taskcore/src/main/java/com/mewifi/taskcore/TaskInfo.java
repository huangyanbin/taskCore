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
    private Object tag;
    private boolean isCancel;
    private boolean isFinish;

    public TaskInfo(Action<T> taskRunnable, TaskOption options,Object tag) {
        this.options = options;
        this.action = taskRunnable;
        this.tag = tag;
    }

    public TaskInfo(Action<T> taskRunnable,TaskOption options, Result<T> result,Object tag) {
        this.options = options;
        this.result = result;
        this.action = taskRunnable;
        this.tag = tag;
    }

    public void execute(Result<T> result){
        this.result = result;
        TaskLoader.getInstance().exeTask(this);
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    public Object getTag() {
        return tag;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

}
