package com.wsh.tinker.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

/**
 * Todo
 * Created by wangshanhai on 2017/2/15.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        initHotFix();

    }

    private void initHotFix(){

        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Bugly.init(getApplicationContext(), "4c54fdca98", true);
    }




    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        Beta.installTinker();
    }

}
