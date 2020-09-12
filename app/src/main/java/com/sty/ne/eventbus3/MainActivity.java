package com.sty.ne.eventbus3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {
    private Button btnJump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //正确的注册位置(单纯地使用该方法注册的话使用的还是反射)
        EventBus.getDefault().register(this);

        btnJump = findViewById(R.id.btn_jump);
        btnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky("sticky");
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         *  错误的注册位置
         *  D/EventBus: No subscribers registered for event class java.lang.String
         *  D/EventBus: No subscribers registered for event class org.greenrobot.eventbus.NoSubscriberEvent
         */
        //EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //错误的反注册位置
        //EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //正确的反注册位置
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void event(String str) {
        Log.e("sty", "--1-> " + str);
    }

    //优先级测试
    @Subscribe(priority = 10)
    public void event2(String str) {
        Log.e("sty", "--2-> " + str);
    }
    // E/sty: --2-> 哈哈哈哈哈
    // E/sty: --1-> 哈哈哈哈哈
}