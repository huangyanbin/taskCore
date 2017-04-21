package com.mewifi.taskcore;

import android.os.Handler;

/**
 * Created by David on 2017/4/21.
 * 任务配置
 */

public class TaskOption {

    private int delayBeforeWork = 0;
    private int delayAfterWork = 0;
    private final Handler handler;

    private TaskOption(Builder builder) {
        delayBeforeWork = builder.delayBeforeWork;
        delayAfterWork = builder.delayAfterWork;
        handler = builder.handler;
    }

    public Handler getHandler() {
        return (handler == null ? new Handler() : handler);
    }

    /**
     * 工作之前延迟
     * @return
     */
    public boolean shouldDelayBeforeLoading(){
        if(delayBeforeWork >0){
            return true;
        }
        return false;
    }

    /**
     * 工作之后延迟
     * @return
     */
    public boolean shouldDelayAfterLoading(){
        if(delayAfterWork >0){
            return true;
        }
        return false;
    }

    public int getDelayBeforeWork() {
        return delayBeforeWork;
    }

    public int getDelayAfterWork() {
        return delayAfterWork;
    }

    public static TaskOption createSimple() {
        return new Builder().build();
    }

    public static class Builder{
        private int delayBeforeWork = 0;
        private int delayAfterWork = 0;
        private Handler handler = null;

        public Builder setDelayBefore(int delayBeforeTime) {
            this.delayBeforeWork = delayBeforeTime;
            return this;
        }

        public Builder setDelayAfter(int delayAfterTime) {
            this.delayAfterWork = delayAfterTime;
            return this;
        }

        public TaskOption build(){
            return  new TaskOption(this) ;
        }

        public Builder setHandler(Handler handler) {
            this.handler = handler;
            return this;
        }
    }
}
