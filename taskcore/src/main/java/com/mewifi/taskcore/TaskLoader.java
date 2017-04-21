package com.mewifi.taskcore;

import com.mewifi.taskcore.listener.Result;
import com.mewifi.taskcore.listener.SimpleTaskResult;


/**
 * Created by David on 2017/4/21.
 */

public class TaskLoader {

    private TaskConfiguration configuration;
    private TaskEngine engine;
    public int taskCounter;
    private volatile static TaskLoader instance;

    private static final String ERROR_INIT_CONFIG_WITH_NULL = "请先配置任务参数";
    private final Result emptyResult = new SimpleTaskResult();

    /** Returns singleton class instance */
    public static TaskLoader getInstance() {
        if (instance == null) {
            synchronized (TaskLoader.class) {
                if (instance == null) {
                    instance = new TaskLoader();
                }
            }
        }
        return instance;
    }

    protected TaskLoader() {
    }

    /**
     * 初始化默认配置
     */
    public void init(){
       init(new TaskConfiguration.Builder().build());
    }

    /**
     * 初始化配置
     * @param configuration
     */
    public synchronized void init(TaskConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
        }
        if (this.configuration == null) {
            engine = new TaskEngine(configuration);
            this.configuration = configuration;
        } else {
            //L.w(WARNING_RE_INIT_CONFIG);
        }
    }



    public boolean isInited() {
        return configuration != null;
    }


    /**
     * 执行任务
     * @param action

     * @param result
     */
    public void execute(Action action, Result result) {
      execute(action,null,null,result);
    }

    /**
     * 执行任务
     * @param action
     */
    public void execute(Action action) {
        execute(action,null,null,null);
    }


    /**
     * 执行任务
     * @param action
     * @param options
     * @param result
     */
    public void execute(Action action ,TaskOption options, Result result) {
        execute(action,null,options,result);
    }
    /**
     * 执行任务
     * @param tag
     * @param options
     */
    public void execute(Action<?> action,String tag,TaskOption options, Result<?> result) {
        if(action == null){
            return;
        }
        checkConfiguration();
        if (result == null) {
            result = emptyResult;
        }
        if (options == null) {
            options = configuration.defaultTaskOptions;
        }
        if(tag == null) {
            taskCounter++;
            tag = "task:"+taskCounter;
        }
        TaskInfo taskInfo = new TaskInfo(action, options, result, engine.getLockForUri(tag));
        exeTask(taskInfo);

    }
    /**
     * 创建任务
     */
     TaskInfo<?> createTask(Action<?> action,TaskOption options) {
        checkConfiguration();
         taskCounter++;
         String tag = "task:"+taskCounter;
         if (options == null) {
             options = configuration.defaultTaskOptions;
         }
        TaskInfo<?> taskInfo = new TaskInfo(action,options, engine.getLockForUri(tag));
        return taskInfo;
    }

      void exeTask(TaskInfo<?> taskInfo){
         BackgroundTask<?> displayTask = new BackgroundTask(engine, taskInfo);
         engine.submit(displayTask);
     }


    /**
     * 检查配置
     */
    private void checkConfiguration() {
        if (configuration == null) {
            throw new IllegalStateException(ERROR_INIT_CONFIG_WITH_NULL);
        }
    }
}
