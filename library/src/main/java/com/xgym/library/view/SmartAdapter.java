package com.xgym.library.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;

/**
 * 列表适配器
 * 避免列表数据变化及列表更新发生在不同线程而造成的错误
 */
public abstract class SmartAdapter<T> extends BaseAdapter {
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private List<T> data = new ArrayList<T>();

    public SmartAdapter(@Nullable List<T> list) {
        setData(list);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int i) {
        return i >= 0 && i < getCount() ? data.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    public List<T> getData() {
        return Collections.unmodifiableList(data);
    }

    private void setData(@Nullable List<T> list) {
        data.clear();
        if (list != null) {
            data.addAll(list);
        }
    }

    public void notifyDataSetChanged(@Nullable final List<T> list) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                setData(list);
                notifyDataSetChanged();
            }
        });
    }
}
