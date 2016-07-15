package com.xgym.library;

import android.app.Application;
import com.bumptech.glide.Glide;
import com.squareup.okhttp.OkHttpClient;
import com.xgym.library.network.OkHttpManager;
import java.util.concurrent.TimeUnit;

public class LibApplication extends Application {
    private static LibApplication application;
    public static LibApplication getApplication() {
        return application;
    }
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        trimMemory(level);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    protected void trimMemory(int level) {
        Glide glide = Glide.get(this);
        glide.getBitmapPool().clearMemory();
        if (level == TRIM_MEMORY_MODERATE) {
            glide.clearMemory();
        }
        System.gc();
    }

    protected void initOkHttp() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        OkHttpManager.setHttpClient(client);
    }

}
