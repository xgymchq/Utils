package com.xgym.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 宽度等于父控件宽度的百分比
 * 按宽高比计算控件高度
 */
public class PercentageImageView extends ImageView {
    private float percentage = 0;
    private float widthToDepthRatio = 1;

    public PercentageImageView(Context context) {
        super(context);
    }

    public PercentageImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentageImageView percentage(final float percentage) {
        if (percentage <= 0) {
            throw new IllegalArgumentException("宽度百分比必须大于 0");
        }
        this.percentage = percentage;
        postInvalidate();
        return this;
    }

    public PercentageImageView widthToDepthRatio(final float widthToDepthRatio) {
        if (widthToDepthRatio <= 0) {
            throw new IllegalArgumentException("宽高比必须大于 0");
        }
        this.widthToDepthRatio = widthToDepthRatio;
        postInvalidate();
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //noinspection Duplicates
        if (percentage > 0 && getParent() != null) {
            int parentWidth = ((ViewGroup) getParent()).getWidth();
            if (parentWidth > 0) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                int width = (int) (parentWidth * percentage);
                width -= (layoutParams.leftMargin + layoutParams.rightMargin);
                int height = (int) (width / widthToDepthRatio);
                height -= (layoutParams.topMargin + layoutParams.bottomMargin);
                setMeasuredDimension(width, height);
            }
        }
    }
}
