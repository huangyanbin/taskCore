package com.mewifi.taskcore;

import com.mewifi.taskcore.listener.Result;
import com.mewifi.taskcore.listener.SimpleTaskResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by David on 2017/4/21.
 */

public class TaskLoader {

    private TaskConfiguration configuration;
    private TaskEngine engine;
    public int taskCounter;
    private volatile static TaskLoader instance;

    private static final String ERROR_INIT_CONFIG_WITH_NULL = "请先配置任务参数";
    private final List<TaskInfo> taskInfos;
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
        this.taskInfos = new ArrayList<>();
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
    public void execute(Action<?> action,Object tag,TaskOption options, Result<?> result) {
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
        TaskInfo taskInfo = new TaskInfo(action, options, result,tag);
        exeTask(taskInfo);

    }
    /**
     * 创建任务
     */
     TaskInfo<?> createTask(Action<?> action,TaskOption options,Object tag) {
        checkConfiguration();
         taskCounter++;
         if(tag == null) {
             taskCounter++;
             tag = "task:"+taskCounter;
         }
         if (options == null) {
             options = configuration.defaultTaskOptions;
         }
        TaskInfo<?> taskInfo = new TaskInfo(action,options, tag);
        return taskInfo;
    }


    /**
     * 执行任务
     * @param taskInfo
     */
    void exeTask(TaskInfo<?> taskInfo){
         taskInfos.add(taskInfo);
         BackgroundTask<?> displayTask = new BackgroundTask(engine, taskInfo);
         engine.submit(displayTask);
     }


    /**
     * 任务完成通知
     * @param taskInfo
     */
    void onFinishTask(TaskInfo taskInfo){
         taskInfos.remove(taskInfo);
     }

    /**
     * 取消任务执行
     */
    public void cancelTask(TaskInfo taskInfo){
        if(taskInfos.contains(taskInfo)) {
            taskInfo.setCancel(true);
        }
    }
    /**
     * 取消任务执行
     */
    public void cancelTask(Object tag){

        for(TaskInfo taskInfo :taskInfos){
            if(taskInfo != null && taskInfo.getTag() != null) {
                if (taskInfo.getTag().equals(tag)) {
                    taskInfo.setCancel(true);
                }
            }
        }
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
