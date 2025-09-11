package com.clang.game;

import android.app.Application;

import com.xingzhao.gamesdk.ClGameSdk;

/**
 * @Author: 张鹏飞
 * @Date: 2025/9/11
 * @Desc:
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        ClGameSdk.setContext(this);
        super.onCreate();
    }
}
