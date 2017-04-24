taskCore
========
***taskCore 一个类似rxjava 简单版的任务切换，在Android中常使用到。***
- - -
- 功能说明：

***1.异步任务执行完,主动切换到UI线程回调相关数据***

***2.支持设置异步线程执行前后暂停时间***

***3.设置线程池执行模式，数量、等级（支持FIFO, LIFO）***

***4.设置回调Handler线程，默认回调到UI线程***


- - -
- 使用说明：
**集成：**

第一步：

root gradle:

	allprojects {
    	     repositories {
        	maven { url 'https://jitpack.io' }
        	jcenter()
    	    }
	}

第二步：

	compile 'com.github.huangyanbin:taskCore:e3d7f38b04'

**1.首先初始化建议在Application onCreate();**

TaskLoader.getInstance().init();

**2.使用Task创建一个任务Action，并执行回调 Result,onHandle 在异步线程，onGetData在UI线程。支持设置Tag取消任务（不设置回调任务不会执行）**
        
	Task.create(new Action<String>() {
            @Override
            public String onHandle() {
                return "异步处理数据";
            }
        }, new TaskOption.Builder().setDelayAfter(5 * 1000).setDelayBefore(5 * 1000).build(),tag)
                .execute(new BaseResult<String>() {
                    @Override
                    public void onStart() {
                        Toast.makeText(MainActivity.this, "开始处理数据了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onGetData(String s) {
                        Toast.makeText(MainActivity.this, "完成处理了", Toast.LENGTH_SHORT).show();
                    }
					@Override
                    public void onFinish() {
                        Toast.makeText(MainActivity.this, "完成处理了", Toast.LENGTH_SHORT).show();
                    }

                });

				
**3.也可以使用TaskLoader创建一个任务Action,并执行回调 Result（注意不设置回调任务也会执行）**		

        TaskLoader.getInstance().execute(new Action<String>() {
                                             @Override
                                             public String onHandle() {

                                                 return "开始处理数据了2";
                                             }
                                         }
                , new BaseResult<String>() {

                    @Override
                    public void onGetData(String s) {
                        Toast.makeText(MainActivity.this, "完成处理了2", Toast.LENGTH_SHORT).show();
                    }
                });

**4.取消任务执行**

	    Task.cancel(tag);

**5.更多功能**
