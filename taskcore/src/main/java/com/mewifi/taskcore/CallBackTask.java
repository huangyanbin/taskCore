package com.mewifi.taskcore;

/**
 * 前台处理数据
 * @param
 */
final class CallBackTask<T> implements Runnable {

    private TaskInfo<T> taskInfo;
    private TaskEngine engine;
    private T t;

    public CallBackTask(TaskInfo<T> taskInfo, TaskEngine engine, T t) {
        this.taskInfo = taskInfo;
        this.engine = engine;
        this.t = t;
    }


    public void run() {
        taskInfo.result.onGetData(t);
    }
}
