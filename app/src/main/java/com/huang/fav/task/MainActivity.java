package com.huang.fav.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mewifi.taskcore.Action;
import com.mewifi.taskcore.Task;
import com.mewifi.taskcore.TaskLoader;
import com.mewifi.taskcore.TaskOption;
import com.mewifi.taskcore.listener.BaseResult;

/**
 * Created by David on 2017/4/21.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskLoader.getInstance().init();
        Task.create(new Action<String>() {
            @Override
            public String onHandle() {
                return "1111";
            }
        }, new TaskOption.Builder().setDelayAfter(5 * 1000).setDelayBefore(5 * 1000).build(),this)
                .execute(new BaseResult<String>() {
                    @Override
                    public void onStart() {
                        Toast.makeText(MainActivity.this, "onStart", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onGetData(String s) {
                        Toast.makeText(MainActivity.this, "onGetData1", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(MainActivity.this, "onFinish", Toast.LENGTH_SHORT).show();
                        super.onFinish();
                    }
                });
    }


    @Override
    protected void onDestroy() {
        Task.cancel(this);
        super.onDestroy();
    }
}
