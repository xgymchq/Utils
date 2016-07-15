package com.xgym.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xgym.library.util.ImageUtil;


public class CropImageView extends View {
    private Bitmap sourceImage = null;
    private DisplayImage displayImage;
    private Paint mPaint = new Paint();
    private boolean isDrag = false;
    private int borderColor = 0xFFFF0000;
    private int borderSize = 2;
    private int centerX = -1;
    private int centerY = -1;
    private int cropHeight = -1;
    private int cropWidth = -1;
    private int cropWinColor = 0X00000000;
    private int displayCropHeight;
    private int displayCropWidth;
    private int maskColor = 0x60000000;
    private int touchX;
    private int touchY;

    public CropImageView(Context context) {
        super(context);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int left = centerX - displayCropWidth / 2;
        int right = left + displayCropWidth;
        int top = centerY - displayCropHeight / 2;
        int bottom = top + displayCropHeight;
        // 绘制图片
        drawSourceImage(canvas);
        // 绘制遮罩层
        mPaint.setColor(maskColor);
        canvas.drawRect(0, 0, width, top, mPaint);
        canvas.drawRect(0, top, left, bottom, mPaint);
        canvas.drawRect(0, bottom, width, height, mPaint);
        canvas.drawRect(right, top, width, bottom, mPaint);

        // 绘制边框线
        mPaint.setColor(borderColor);
        // Top
        canvas.drawRect(left, top - borderSize, right, top, mPaint);
        // Right
        canvas.drawRect(right, top - borderSize, right + borderSize, bottom + borderSize, mPaint);
        // Bottom
        canvas.drawRect(left, bottom, right, bottom + borderSize, mPaint);
        // Left
        canvas.drawRect(left - borderSize, top - borderSize, left, bottom + borderSize, mPaint);

        // 绘制窗口
        mPaint.setColor(cropWinColor);
        canvas.drawRect(left, top, right, bottom, mPaint);
    }

    private void drawSourceImage(Canvas canvas) {
        if (sourceImage == null) {
            return;
        }
        if (displayImage == null) {
            displayImage = new DisplayImage(sourceImage, canvas);
            cropWinMove(0, 0, true);
            scaleCropWindow();
        }
        displayImage.draw(canvas);
    }

    private void scaleCropWindow() {
        if (cropWidth > 0) {
            displayCropWidth = (int) (cropWidth * displayImage.scale);
        }
        if (cropHeight > 0) {
            displayCropHeight = (int) (cropHeight * displayImage.scale);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initCropWinRect();
    }

    private void initCropWinRect() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (width <= 0 || height <= 0) {
            return;
        }
        if (centerX < 0) {
            centerX = width / 2;
        }
        if (centerY < 0) {
            centerY = height / 2;
        }
        if (cropWidth < 0) {
            cropWidth = width * 3 / 4;
        }
        if (cropHeight < 0) {
            cropHeight = height * 3 / 4;
        }
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDrag) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchX = (int) event.getX();
                    touchY = (int) event.getY();
                    if (!isTouchCropWin(touchX, touchY)) {
                        touchX = -1;
                        touchY = -1;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int curX = (int) event.getX();
                    int curY = (int) event.getY();
                    if (isTouchCropWin(curX, curY)) {
                        cropWinMove(curX - touchX, curY - touchY, true);
                        postInvalidate();
                    }
                    touchX = curX;
                    touchY = curY;
                    break;
            }
            return true;
        }
        return false;
    }

    private boolean isTouchCropWin(int x, int y) {
        int left = centerX - displayCropWidth / 2;
        int right = left + displayCropWidth;
        int top = centerY - displayCropHeight / 2;
        int bottom = top + displayCropHeight;
        return x >= left && x <= right && y >= top && y <= bottom;
    }

    private void cropWinMove(int dx, int dy, boolean touch) {
        centerX += dx;
        centerY += dy;
        if (touch) {
            if (dx <= 0) {
                cropWinMove(displayImage.getOutOfRangeLeft(centerX - displayCropWidth / 2), 0, false);
            } else if (dx > 0) {
                cropWinMove(displayImage.getOutOfRangeRight(centerX + displayCropWidth / 2), 0, false);
            }
            if (dy <= 0) {
                cropWinMove(0, displayImage.getOutOfRangeTop(centerY - displayCropHeight / 2), false);
            } else if (dy > 0) {
                cropWinMove(0, displayImage.getOutOfRangeBottom(centerY + displayCropHeight / 2), false);
            }
        }
    }

    public Bitmap getCroppedImage() {
        int left = centerX - displayCropWidth / 2;
        int top = centerY - displayCropHeight / 2;
        int width = cropWidth;
        int height = cropHeight;
        left = (int) (sourceImage.getWidth() * (left - displayImage.left) * 1.0F / displayImage.dstWidth);
        top = (int) (sourceImage.getHeight() * (top - displayImage.top) * 1.0F / displayImage.dstHeight);
        if (top < 0) top = 0;
        if (left < 0) left = 0;
        if (top + height > sourceImage.getHeight()) height = sourceImage.getHeight() - top;
        if (left + width > sourceImage.getWidth()) width = sourceImage.getWidth() - left;
        return ImageUtil.on(sourceImage).crop(left, top, width, height).create();
    }

    public CropImageView borderColor(final int borderColor) {
        if (this.borderColor != borderColor) {
            this.borderColor = borderColor;
            postInvalidate();
        }
        return this;
    }

    public CropImageView borderSize(final int borderSize) {
        if (this.borderSize != borderSize) {
            this.borderSize = borderSize;
            postInvalidate();
        }
        return this;
    }

    public CropImageView frameColor(final int frameColor) {
        if (this.cropWinColor != frameColor) {
            this.cropWinColor = frameColor;
            postInvalidate();
        }
        return this;
    }

    public CropImageView maskColor(final int maskColor) {
        if (this.maskColor != maskColor) {
            this.maskColor = maskColor;
            postInvalidate();
        }
        return this;
    }

    public CropImageView setCropHeight(int height) {
        if (cropHeight <= 0 || cropHeight != height) {
            this.cropHeight = height;
            if (displayImage != null) {
                displayCropHeight = (int) (cropHeight * displayImage.scale);
            }
            postInvalidate();
        }
        return this;
    }

    public CropImageView setCropWidth(int width) {
        if (cropWidth <= 0 || cropWidth != width) {
            this.cropWidth = width;
            if (displayImage != null) {
                displayCropWidth = (int) (cropWidth * displayImage.scale);
            }
            postInvalidate();
        }
        return this;
    }

    public void isDrag(boolean drag) {
        isDrag = drag;
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.sourceImage = bitmap;
        this.displayImage = null;
        postInvalidate();
    }

    public void setImageResource(@DrawableRes int resID) {
        sourceImage = BitmapFactory.decodeResource(getResources(), resID);
        this.displayImage = null;
        postInvalidate();
    }

    private static class DisplayImage {
        private Bitmap displayBitmap;
        private float left;
        private float scale;
        private float top;
        private int dstHeight;
        private int dstWidth;

        public DisplayImage(Bitmap sourceImage, Canvas canvas) {
            this(sourceImage, canvas.getWidth(), canvas.getHeight());
        }

        public DisplayImage(Bitmap sourceImage, int maxWidth, int maxHeight) {
            int sourceW = sourceImage.getWidth();
            float scaleW = maxWidth * 1.0F / sourceW;

            int sourceH = sourceImage.getHeight();
            float scaleH = maxHeight * 1.0F / sourceH;

            scale = Math.min(scaleW, scaleH);
            dstWidth = (int) (sourceW * scale);
            dstHeight = (int) (sourceH * scale);
            displayBitmap = Bitmap.createScaledBitmap(sourceImage, dstWidth, dstHeight, false);

            left = (maxWidth - dstWidth) / 2;
            top = (maxHeight - dstHeight) / 2;
        }

        public int getOutOfRangeBottom(int y) {
            return y > top + dstHeight ? (int) (top + dstHeight - y) : 0;
        }

        public int getOutOfRangeLeft(int x) {
            return x < left ? (int) (left - x) : 0;
        }

        public int getOutOfRangeRight(int x) {
            return x > left + dstWidth ? (int) (left + dstWidth - x) : 0;
        }

        public int getOutOfRangeTop(int y) {
            return y < top ? (int) (top - y) : 0;
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(displayBitmap, left, top, null);
        }
    }
}
