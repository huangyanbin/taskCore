package com.mewifi.taskcore;


import android.os.Handler;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 后台处理任务
 * @param <T>
 */
final class BackgroundTask<T> implements Runnable {


    private final TaskEngine engine;
    private final TaskInfo<T> taskInfo;
    private final Handler handler;
    private final TaskOption options;


    public BackgroundTask(TaskEngine engine, TaskInfo<T> taskInfo) {
        this.engine = engine;
        this.taskInfo = taskInfo;
        this.handler = taskInfo.options.getHandler();
        options = taskInfo.options;
    }

    /**
     * 执行任务
     */
    @Override
    public void run() {

        if(cancel()) return;
        onStart();
        if (waitIfPaused()) return;
        if (delayBeforeIfNeed()) return; //延迟
        if(cancel()) return;
        T t = null;
        try {
            t = taskInfo.action.onHandle();
        } catch (Exception e){

        }
        if(cancel()) return;
        if (delayAfterIfNeed()) return; //延迟
        if ( checkTaskIsInterrupted()) return;
        if(cancel()) return;
        CallBackTask<T> callBackTask = new CallBackTask<>(taskInfo,engine ,t);
        handler.post(callBackTask);
        onFinish();
    }

    /**
     * 任务开始回调
     */
    public void onStart(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                taskInfo.result.onStart();
            }
        });
    }

    /**
     * 任务完成回调
     */
    public void onFinish(){

        taskInfo.setFinish(true);
        TaskLoader.getInstance().onFinishTask(taskInfo);
        handler.post(new Runnable() {
            @Override
            public void run() {
                taskInfo.result.onFinish();
            }
        });
    }

    /**
     * 任务取消
     * @return 取消
     */
    public boolean cancel(){

        if(taskInfo.isCancel()){
           TaskLoader.getInstance().onFinishTask(taskInfo);
            return true;
        }
        return false;
    }

    /**
     * 是否暂停
     * @return
     */
    private boolean waitIfPaused() {
        AtomicBoolean pause = engine.getPause();
        synchronized (pause) {
            if (pause.get()) {
                try {
                    pause.wait();
                } catch (InterruptedException e) {
                    onFinish();
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * 延迟在任务执行之前
     * @return
     */
    private boolean delayBeforeIfNeed() {
        if (options.shouldDelayBeforeLoading()) {
            try {
                Thread.sleep(options.getDelayBeforeWork());
            } catch (InterruptedException e) {
                onFinish();
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 延迟在任务执行之后
     * @return
     */
    private boolean delayAfterIfNeed() {
        if (options.shouldDelayAfterLoading()) {
            try {
                Thread.sleep(options.getDelayAfterWork());
            } catch (InterruptedException e) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 检查线程是否被终止
     * @return
     */
    private boolean checkTaskIsInterrupted() {
        boolean interrupted =  Thread.interrupted();
        if(interrupted){
            onFinish();
            return true;
        }
        return false;
    }


}
