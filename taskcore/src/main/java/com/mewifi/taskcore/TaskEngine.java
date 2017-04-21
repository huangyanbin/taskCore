package com.mewifi.taskcore;

import com.mewifi.taskcore.pool.DefaultConfigurationFactory;

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
    private final Map<String, ReentrantLock> uriLocks = new WeakHashMap<>();

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


    void pause() {
        paused.set(true);
    }

    void resume() {
        synchronized (paused) {
            paused.set(false);
            paused.notifyAll();
        }
    }

    void stop() {

        ((ExecutorService) taskExecutor).shutdownNow();
        uriLocks.clear();
    }

    private void initExecutorsIfNeed() {
        if (((ExecutorService) taskExecutor).isShutdown()) {
            taskExecutor = createTaskExecutor();
        }
    }

    private Executor createTaskExecutor() {
        return DefaultConfigurationFactory.createExecutor(configuration.threadPoolSize,
                configuration.threadPriority, configuration.tasksProcessingType);
    }

    AtomicBoolean getPause() {
        return paused;
    }

    ReentrantLock getLockForUri(String uri) {
        ReentrantLock lock = uriLocks.get(uri);
        if (lock == null) {
            lock = new ReentrantLock();
            uriLocks.put(uri, lock);
        }
        return lock;
    }
}
