package com.sty.ne.eventbus3;

import android.app.Application;

import org.greenrobot.eventbus.EventBus;

/**
 * https://greenrobot.org/eventbus/documentation/subscriber-index/
 * @Author: tian
 * @UpdateDate: 2020/9/12 6:01 PM
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }
}
