# `EventBus3.0`的介绍与使用

## 一、介绍

### 1.1 常规的事件传递

> 1. `Intent`意图：跳转+传参，局限性非常大；
> 2. `Handler`：通常用来更新主线程`UI`，使用不当容易出现内存泄漏；
> 3. `Interface`接口：仅局限于同一线程中数据交互；
> 4. `BroadCastReceiver`：有序广播+无序广播；
> 5. `AIDL`：跨进程通信，代码阅读性不友好，维护成本偏高；
> 6. 其它方式，如本地存储...

### 1.2 什么是`EventBus`?

[`EventBus`](https://greenrobot.org/eventbus/) 由`GreenRobot`开发，是一个`Android`优化的`publish/subscribe`消息事件总线，简化了应用程序之间、组件与后台线程间的通信，如`Activitys`、`Fragments`、`Threads`以及`Services`等等。

`EventBus`的`github`地址为：[https://github.com/greenrobot/EventBus](https://github.com/greenrobot/EventBus) 。

官方架构图如下图所示：

![image](https://github.com/tianyalu/NeEventBus3/raw/master/show/eventbus_structure.png)

![image-20200912163500348](/Users/tian/Library/Application Support/typora-user-images/image-20200912163500348.png)

### 1.3 使用场景

比如网络请求，返回时通过`Handler`或者`BroadCastReceiver`通知更新主线程`UI`;

`N`个`Fragment`之间需要通过`Listener`(监听)通信；

以上需求都可以通过`EventBus`完成和实现。

## 二、使用

### 2.1 注册与反注册

#### 2.1.1 错误的注册与反注册位置

```java
    @Override
    protected void onStart() {
        super.onStart();
        // 错误的注册位置
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //错误的反注册位置
        EventBus.getDefault().unregister(this);
    }
```

在此处注册会报如下异常：

```java
D/EventBus: No subscribers registered for event class java.lang.String
D/EventBus: No subscribers registered for event class org.greenrobot.eventbus.NoSubscriberEvent
```

#### 2.1.2 正确的注册与反注册位置

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //正确的注册位置(单纯地使用该方法注册的话使用的还是反射)
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //正确的反注册位置
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
```

#### 2.1.3 注解处理器

尽管2.1.2可以正常使用，但此种方式还是使用了反射机制，没有发挥3.0的优势。

真正正确使用需要添加注解处理器，可以参考：[https://greenrobot.org/eventbus/documentation/subscriber-index/](https://greenrobot.org/eventbus/documentation/subscriber-index/)

`build.gradle`文件：

```groovy
defaultConfig {
  applicationId "com.sty.ne.eventbus3"
  //...
  //给注解处理器传参
  javaCompileOptions {
    annotationProcessorOptions {
      arguments = [ eventBusIndex : 'com.sty.ne.eventbus3.MyEventBusIndex' ]
    }
  }
}

dependencies {
  //...
  def eventbus_version = '3.2.0'
  implementation "org.greenrobot:eventbus:$eventbus_version"
  annotationProcessor "org.greenrobot:eventbus-annotation-processor:$eventbus_version"
}
```

`BaseApplication`文件：

```java
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }
}
```

然后在清单文件中使用`BaseApplication`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.sty.ne.eventbus3">
    <application
        android:name=".BaseApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
				<!-- ... -->
    </application>

</manifest>
```

### 2.2 官方三部曲

#### 2.2.1 定义事件

```java
public static class MessageEvent { /* Additional fields if needed */ }
```

#### 2.2.2 订阅事件

注解+方法指定参数

```java
@Subscribe(threadMode = ThreadMode.MAIN)  
public void onMessageEvent(MessageEvent event) {/* Do something */};
```

### 2.2.3 发布事件

```java
EventBus.getDefault().post(new MessageEvent());
```

### 2.3 粘性事件

粘性事件的作用：延迟消费或者未初始化。

使用粘性事件只需要在`@Subscribe`注解中加入`sticky=true`即可。

```java
@Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
public void onMessageEvent(UserInfo user) {
  Log.e("sty", user.toString());
}
```

#### 2.4 订阅方法执行优先级

如果两个订阅方法可以同时接收相同的`Event`，`priority`数值越大，优先级越高，默认为0。

```java
@Subscribe(threadMode = ThreadMode.MAIN, priority = 1)  
public void onMessageEvent(MessageEvent event) {/* Do something */};

@Subscribe(threadMode = ThreadMode.MAIN, priority = 10)  
public void onMessageEvent2(MessageEvent event) {/* Do something */};
```

















