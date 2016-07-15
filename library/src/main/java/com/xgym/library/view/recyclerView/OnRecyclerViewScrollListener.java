package com.xgym.library.view.recyclerView;

import android.support.v7.widget.RecyclerView;

public interface OnRecyclerViewScrollListener {
    void onScrollStateChanged(RecyclerView recyclerView, int newState);

    void onScrolled(RecyclerView recyclerView, int dx, int dy);

    void onScrollToTop(RecyclerView recyclerView);

    void onScrollToBottom(RecyclerView recyclerView);
}
