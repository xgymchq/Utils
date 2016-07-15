package com.xgym.library.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ProgressImageView extends FrameLayout {
    public static final int STATE_TYPE_LOADING_BEGIN = 0;
    public static final int STATE_TYPE_LOADING_END = 1;
    private ProgressBar progressBar;
    private ImageView imgView;
    private String imagePath;
    private float percentage = 0;
    private float widthToDepthRatio = 1;
    private OnStateChangedListener onListener;

    public ProgressImageView(Context context) {
        super(context);
        init();
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        imgView = new ImageView(getContext());
        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imgView.setLayoutParams(layoutParams);
        addView(imgView);
        progressBar = new ProgressBar(new ContextThemeWrapper(getContext(), android.R.style.Widget_ProgressBar_Small));
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(layoutParams);
        addView(progressBar);
        showProgressBar(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int height = 0;
        //noinspection Duplicates
        if (percentage > 0 && getParent() != null) {
            int parentWidth = ((ViewGroup) getParent()).getWidth();
            if (parentWidth > 0) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                width = (int) (parentWidth * percentage);
                width -= (layoutParams.leftMargin + layoutParams.rightMargin);
                height = (int) (width / widthToDepthRatio);
                height -= (layoutParams.topMargin + layoutParams.bottomMargin);
            }
        } else {
            width = getMeasuredWidth();
            height = (int) (width / widthToDepthRatio);
        }
        setMeasuredDimension(width, height);

        LayoutParams imgViewLayoutParams = (LayoutParams) imgView.getLayoutParams();
        imgViewLayoutParams.width = width;
        imgViewLayoutParams.height = height;
        imgView.setLayoutParams(imgViewLayoutParams);
    }

    public ProgressImageView widthToDepthRatio(final float widthToDepthRatio) {
        this.widthToDepthRatio = widthToDepthRatio;
        return this;
    }

    public ProgressImageView percentage(final float percentage) {
        this.percentage = percentage;
        return this;
    }

    public void showProgressBar(boolean loading) {
        progressBar.setVisibility(loading ? VISIBLE : GONE);
    }

    public void setImageResource(@DrawableRes int resID) {
        showProgressBar(false);
        Glide.with(getContext())
                .load(resID)
                .centerCrop()
                .into(imgView);
    }

    public void setImagePath(@DrawableRes int defaultResID, @NonNull String path) {
        this.imagePath = path;
        showProgressBar(true);
        if (onListener != null) {
            onListener.onListener(STATE_TYPE_LOADING_BEGIN);
        }
        Glide.with(getContext())
                .load(path)
                .placeholder(defaultResID)
                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                    	showProgressBar(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                        showProgressBar(false);
                        if (onListener != null) {
                            onListener.onListener(STATE_TYPE_LOADING_END);
                        }
                        return false;
                    }
                }).into(imgView);
    }

    public String getImagePath() {
        return imagePath;
    }

    public ProgressImageView onStateChangedListener(@Nullable OnStateChangedListener listener) {
        this.onListener = listener;
        return this;
    }

    public boolean isLoading() {
        return progressBar.getVisibility() == VISIBLE;
    }

    public interface OnStateChangedListener {
        void onListener(int state);
    }
}
