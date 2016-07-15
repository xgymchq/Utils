package com.xgym.library.ui;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import com.xgym.library.helper.ButterKnifeHelper;
import com.xgym.library.helper.EventBusHelper;
import com.xgym.library.helper.SubscriptionManager;
import com.xgym.library.network.OkHttpManager;

import rx.Observable;
import rx.Subscription;
import rx.android.view.ViewObservable;
import rx.functions.Action1;


public class BaseAppCompatActivity extends AppCompatActivity {

    protected final int THROTTLE_TIME = 500;


    protected final String OKHTTP_TAG = UUID.randomUUID().toString();
    protected final String SUBSCRIPTION_TAG = UUID.randomUUID().toString();
    private ButterKnifeHelper butterKnifeHelper = ButterKnifeHelper.get();

    @SuppressWarnings("Duplicates")
    @Override
    protected void onDestroy() {
        butterKnifeHelper.unbind(this);
        EventBusHelper.get().unregister(this);
        SubscriptionManager.getManager().remove(SUBSCRIPTION_TAG);
        OkHttpManager.cancel(OKHTTP_TAG);
        super.onDestroy();
    }



    protected void throttleFirst(View view, Action1 onNext) {
        Subscription subscribe = ViewObservable.clicks(view).throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS).subscribe(onNext);
        SubscriptionManager.getManager().put(SUBSCRIPTION_TAG, subscribe);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusHelper.get().register(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        butterKnifeHelper.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        butterKnifeHelper.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        butterKnifeHelper.bind(this);
    }

}
