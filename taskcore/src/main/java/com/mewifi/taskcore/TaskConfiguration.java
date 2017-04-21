package com.mewifi.taskcore;


import android.os.Handler;

import com.mewifi.taskcore.pool.DefaultConfigurationFactory;
import com.mewifi.taskcore.pool.QueueProcessingType;

import java.util.concurrent.Executor;

/**
 * Created by David on 2017/4/21.
 * 任务配置
 */

public class TaskConfiguration {

    public static final int DEFAULT_THREAD_POOL_SIZE = 3;

    public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1;

    public static final QueueProcessingType DEFAULT_TASK_PROCESSING_TYPE = QueueProcessingType.FIFO;
    final TaskOption defaultTaskOptions;
    final int threadPoolSize;
    final int threadPriority;
    final QueueProcessingType tasksProcessingType;
    final Executor taskExecutor;

    private TaskConfiguration(final Builder builder) {

        taskExecutor = builder.taskExecutor;
        threadPoolSize = builder.threadPoolSize;
        threadPriority = builder.threadPriority;
        tasksProcessingType = builder.tasksProcessingType;
        defaultTaskOptions = builder.taskOption;
    }

    public static class Builder {

        private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
        private int threadPriority = DEFAULT_THREAD_PRIORITY;
        private Executor taskExecutor = null;
        private QueueProcessingType tasksProcessingType = DEFAULT_TASK_PROCESSING_TYPE;
        private TaskOption taskOption = null;
        private Handler mHandler ;

        public Builder threadPoolSize(int threadPoolSize) {
            if (taskExecutor != null) {
            }
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public Builder setHandler(Handler handler) {
            this.mHandler = handler;
            return this;
        }

        public Builder threadPriority(int threadPriority) {
            if (taskExecutor != null) {
            }

            if (threadPriority < Thread.MIN_PRIORITY) {
                this.threadPriority = Thread.MIN_PRIORITY;
            } else {
                if (threadPriority > Thread.MAX_PRIORITY) {
                    threadPriority = Thread.MAX_PRIORITY;
                } else {
                    this.threadPriority = threadPriority;
                }
            }
            return this;
        }

        public TaskConfiguration build() {
            if (taskExecutor == null) {
                taskExecutor = DefaultConfigurationFactory.createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
            }
            if (taskOption == null) {
                taskOption = TaskOption.createSimple();
            }
            return new TaskConfiguration(this);
        }
        public Builder defaultTaskOptions(TaskOption defaultTaskOptions) {
            this.taskOption = defaultTaskOptions;
            return this;
        }
    }
}
