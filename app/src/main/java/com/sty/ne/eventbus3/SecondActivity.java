package com.sty.ne.eventbus3;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 界面未初始化/延时消费
 * @Author: tian
 * @UpdateDate: 2020/9/12 5:34 PM
 */
public class SecondActivity extends AppCompatActivity {
    private Button btnPostEvent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        EventBus.getDefault().register(this);

        btnPostEvent = findViewById(R.id.btn_post_event);
        btnPostEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("哈哈哈哈哈");
            }
        });
    }

    @Subscribe(sticky = true)
    public void event2(String str) {
        Log.e("sty", "--second-> " + str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //正确的反注册位置
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
