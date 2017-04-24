package com.mewifi.taskcore;

import com.mewifi.taskcore.pool.DefaultConfigurationFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by David on 2017/4/21.
 * 任务引擎
 */

public class TaskEngine {

    final TaskConfiguration configuration;
    private Executor taskExecutor;
    private ExecutorService taskDistributor;
    private final AtomicBoolean paused = new AtomicBoolean(false);


    TaskEngine(TaskConfiguration configuration) {
        this.configuration = configuration;
        taskExecutor = configuration.taskExecutor;
        taskDistributor = Executors.newCachedThreadPool();
    }

    /**
     * 提交任务
     * @param task
     */
    void submit(final BackgroundTask task) {
        taskDistributor.execute(new Runnable() {
            @Override
            public void run() {
                initExecutorsIfNeed();
                taskExecutor.execute(task);
            }
        });
    }


    /**
     * 暂停
     */
    void pause() {
        paused.set(true);
    }

    /**
     * 继续
     */
    void resume() {
        synchronized (paused) {
            paused.set(false);
            paused.notifyAll();
        }
    }

    /**
     * 停止
     */
    void stop() {

        ((ExecutorService) taskExecutor).shutdownNow();
    }

    /**
     * 初始化线程池
     */
    private void initExecutorsIfNeed() {
        if (((ExecutorService) taskExecutor).isShutdown()) {
            taskExecutor = createTaskExecutor();
        }
    }

    /**
     * 创建线程池
     * @return
     */
    private Executor createTaskExecutor() {
        return DefaultConfigurationFactory.createExecutor(configuration.threadPoolSize,
                configuration.threadPriority, configuration.tasksProcessingType);
    }

    /**
     * 是否暂停
     * @return
     */
    AtomicBoolean getPause() {
        return paused;
    }


}
