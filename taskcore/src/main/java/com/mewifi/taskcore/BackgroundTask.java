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

    @Override
    public void run() {
        onStart();
        if (waitIfPaused()) return;
        if (delayBeforeIfNeed()) return; //延迟
        ReentrantLock loadFromUriLock = taskInfo.loadFromUriLock;
        loadFromUriLock.lock();
        T t = null;
        try {
            t = taskInfo.action.onHandle();
        } finally {
            loadFromUriLock.unlock();
        }
        if (delayAfterIfNeed()) return; //延迟
        if ( checkTaskIsInterrupted()) return;
        CallBackTask<T> callBackTask = new CallBackTask<>(taskInfo,engine ,t);
        handler.post(callBackTask);
    }

    public void onStart(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                taskInfo.result.onStart();
            }
        });
    }

    public void onFinish(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                taskInfo.result.onFinish();
            }
        });
    }

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

    private boolean checkTaskIsInterrupted() {
        boolean interrupted =  Thread.interrupted();
        if(interrupted){
            onFinish();
            return true;
        }
        return false;
    }


}
