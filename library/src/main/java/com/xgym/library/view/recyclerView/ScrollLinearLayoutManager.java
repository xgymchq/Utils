package com.xgym.library.view.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class ScrollLinearLayoutManager extends LinearLayoutManager {
    private OnRecyclerViewScrollListener mListener;

    public ScrollLinearLayoutManager(Context context) {
        super(context);
    }

    public ScrollLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setOnRecyclerViewScrollListener(@NonNull RecyclerView recyclerView, @Nullable OnRecyclerViewScrollListener listener) {
        this.mListener = listener;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mListener != null) {
                    mListener.onScrolled(recyclerView, dx, dy);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mListener != null) {
                    mListener.onScrollStateChanged(recyclerView, newState);
                    if (findFirstVisibleItemPosition() == 0) {
                        mListener.onScrollToTop(recyclerView);
                    }
                    if (findLastCompletelyVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1) {
                        mListener.onScrollToBottom(recyclerView);
                    }
                }
            }
        });
    }
}
