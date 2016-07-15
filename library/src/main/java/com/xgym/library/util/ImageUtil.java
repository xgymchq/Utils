package com.xgym.library.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;

/**
 * 图形相关操作
 */
public final class ImageUtil {
    public static final int SCALE_MODE_NOT_LESS_THEN = 0; // 缩放后的图片尺寸不小于指定尺寸（某一边可能大于指定尺寸）
    public static final int SCALE_MODE_NOT_MORE_THEN = 1; // 缩放后的图片尺寸不大于指定尺寸（某一边可能小于指定尺寸）
    public static final int SCALE_MODE_X = 2; // 按宽度缩放
    public static final int SCALE_MODE_Y = 3; // 按高度缩放
    public static final int SCALE_MODE_STRETCH = 4; // 拉伸（宽高各自按照自己的缩放比缩放，得到的图片可能变形）
    private ArrayList<ImageProcess> imageProcesses = new ArrayList<ImageProcess>();
    private Bitmap sourceBitmap;

    private ImageUtil(Bitmap bitmap) {
        sourceBitmap = bitmap;
    }

    public static Bitmap byteArray2Bitmap(byte[] data) {
        if (data == null) {
            return null;
        } else {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
    }

    /**
     * 创建指定大小的纯色图片
     *
     * @param color  图片颜色
     * @param width  图片宽度
     * @param height 图片高度
     * @return 纯色图片
     */
    public static Bitmap createColorBitmap(int color, int width, int height) {
        Bitmap result = Bitmap.createBitmap(Math.max(width, 1), Math.max(height, 1), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(color);
        return result;
    }

    /**
     * 黑白图
     * RGB平均值Avg ＝ (R + G + B) / 3，如果Avg >= 100，则新的颜色值为R＝G＝B＝255；
     * 如果Avg < 100，则新的颜色值为R＝G＝B＝0；255就是白色，0就是黑色
     *
     * @param srcBitmap 原图片
     * @return 黑白图
     */
    public static Bitmap toBlackWhiteBitmap(Bitmap srcBitmap) {
        if (srcBitmap == null) {
            return null;
        }
        Bitmap targetBitmap = srcBitmap.copy(srcBitmap.getConfig(), true);
        int width = targetBitmap.getWidth();
        int height = targetBitmap.getHeight();
        int[] argb;
        int color;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                argb = argb(srcBitmap.getPixel(x, y));
                if ((argb[1] + argb[2] + argb[3]) / 3 >= 100) {
                    color = Color.argb(argb[0], 255, 255, 255);
                } else {
                    color = Color.argb(argb[0], 0, 0, 0);
                }
                targetBitmap.setPixel(x, y, color);
            }
        }
        return targetBitmap;
    }

    private static int[] argb(int color) {
        return new int[]{Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color)};
    }

    /**
     * 灰度图
     * 灰度处理一般有三种算法：
     * 1 最大值法：即新的颜色值R＝G＝B＝Max(R，G，B)，这种方法处理后的图片看起来亮度值偏高。
     * 2 平均值法：即新的颜色值R＝G＝B＝(R＋G＋B)／3，这样处理的图片十分柔和
     * 3 加权平均值法：即新的颜色值R＝G＝B＝(R ＊ Wr＋G＊Wg＋B＊Wb)，一般由于人眼对不同颜色的敏感度不一样，
     * 所以三种颜色值的权重不一样，一般来说绿色最高，红色其次，蓝色最低，最合理的取值分别为Wr ＝ 30％，Wg ＝ 59％，Wb ＝ 11％
     *
     * @param srcBitamp 原图片
     * @return 灰度图
     */
    public static Bitmap toGrayBitmap(Bitmap srcBitamp) {
        if (srcBitamp == null) {
            return null;
        }
        Bitmap target = srcBitamp.copy(srcBitamp.getConfig(), true);
        int[] argb;
        int color, t_color;
        int width = target.getWidth();
        int height = target.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                argb = argb(target.getPixel(x, y));
                color = (int) (argb[1] * 0.3 + argb[2] * 0.59 + argb[3] * 0.11);
                t_color = Color.argb(argb[0], color, color, color);
                target.setPixel(x, y, t_color);
            }
        }
        return target;
    }

    public static ImageUtil on(@NonNull Bitmap bitmap) {
        return new ImageUtil(bitmap);
    }

    public static byte[] bitmap2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        IOUtil.close(stream);
        return data;
    }

    public Bitmap create() {
        Bitmap result = sourceBitmap;
        Bitmap bitmap;
        for (ImageProcess imageProcess : imageProcesses) {
            bitmap = imageProcess.process(result);
            if (result != sourceBitmap) {
                result.recycle();
            }
            result = bitmap;
        }
        return result;
    }

    public ImageUtil circle(float radius) {
        imageProcesses.add(new RoundBitmap(-1, -1, (int) (radius * 2), (int) (radius * 2), radius));
        return this;
    }

    public ImageUtil circle(int centerX, int centerY, float radius) {
        imageProcesses.add(new RoundBitmap((int) (centerX - radius), (int) (centerY - radius), (int) (radius * 2), (int) (radius * 2), radius));
        return this;
    }

    public ImageUtil crop(int x, int y, int width, int height) {
        ImageCrop imageCrop = new ImageCrop(x, y, width, height);
        imageProcesses.add(imageCrop);
        return this;
    }

    public ImageUtil cropCenter(int width, int height) {
        ImageCrop imageCrop = new ImageCrop(-1, -1, width, height, CropType.HORIZONTAL_CENTER & CropType.VERTICAL_CENTER);
        imageProcesses.add(imageCrop);
        return this;
    }

    public ImageUtil crop(int width, int height, int type) {
        ImageCrop imageCrop = new ImageCrop(-1, -1, width, height, type);
        imageProcesses.add(imageCrop);
        return this;
    }

    public ImageUtil height(int height) {
        MatrixProcess process = getMatrixProcess();
        process.setTargetHeight(height);
        return this;
    }

    public ImageUtil rotate(float degrees) {
        MatrixProcess process = getMatrixProcess();
        process.setRotate(degrees);
        return this;
    }

    public ImageUtil rotate(float x, float y, float degrees) {
        MatrixProcess process = getMatrixProcess();
        process.setRotate(degrees, x, y);
        return this;
    }

    public ImageUtil roundRect(int x, int y, int width, int height, float radius) {
        imageProcesses.add(new RoundBitmap(x, y, width, height, radius));
        return this;
    }

    public ImageUtil roundRectCenter(int width, int height, float radius) {
        imageProcesses.add(new RoundBitmap(-1, -1, width, height, radius));
        return this;
    }

    public ImageUtil scale(float scale) {
        MatrixProcess process = getMatrixProcess();
        process.setScale(scale);
        return this;
    }

    public ImageUtil size(int width, int height, int scaleMode) {
        MatrixProcess process = getMatrixProcess();
        process.setTargetSize(width, height, scaleMode);
        return this;
    }

    private MatrixProcess getMatrixProcess() {
        MatrixProcess process;
        if (imageProcesses.isEmpty()) {
            process = new MatrixProcess();
            imageProcesses.add(process);
        } else {
            ImageProcess imageProcess = imageProcesses.get(imageProcesses.size() - 1);
            if (imageProcess instanceof MatrixProcess) {
                process = (MatrixProcess) imageProcess;
            } else {
                process = new MatrixProcess();
                imageProcesses.add(process);
            }
        }
        return process;
    }

    public ImageUtil width(int width) {
        MatrixProcess process = getMatrixProcess();
        process.setTargetWidth(width);
        return this;
    }

    private interface ImageProcess {
        Bitmap process(@NonNull Bitmap sourceBitmap);
    }

    private interface MatrixSet {
        void set(@NonNull Matrix matrix, @NonNull Bitmap bitmap);
    }

    public static interface CropType {
        int TOP = 1;
        int VERTICAL_CENTER = TOP << 1;
        int BOTTOM = VERTICAL_CENTER << 1;
        int LEFT = 16;
        int HORIZONTAL_CENTER = LEFT << 1;
        int RIGHT = HORIZONTAL_CENTER << 1;
    }

    private static class ImageCrop implements ImageProcess {
        private int width, height;
        private int x = -1, y = -1;
        private int type;

        public ImageCrop(int x, int y, int width, int height, int type) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.type = type;
        }

        public ImageCrop(int x, int y, int width, int height) {
            this(x, y, width, height, CropType.VERTICAL_CENTER & CropType.HORIZONTAL_CENTER);
        }

        @SuppressLint("Assert")
        @Override
        public Bitmap process(@NonNull Bitmap bitmap) {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            if (x < 0) {
                if ((type & CropType.LEFT) == CropType.LEFT) {
                    x = 0;
                } else if ((type & CropType.HORIZONTAL_CENTER) == CropType.HORIZONTAL_CENTER) {
                    x = (bitmapWidth - width) / 2;
                } else if ((type & CropType.RIGHT) == CropType.RIGHT) {
                    x = bitmapWidth - width;
                }
            }
            if (y < 0) {
                if ((type & CropType.TOP) == CropType.TOP) {
                    y = 0;
                } else if ((type & CropType.VERTICAL_CENTER) == CropType.VERTICAL_CENTER) {
                    y = (bitmapHeight - height) / 2;
                } else if ((type & CropType.BOTTOM) == CropType.BOTTOM) {
                    y = bitmapHeight - height;
                }
            }
            assert x >= 0 && x <= bitmapWidth : "crop x must be >= 0 and <= " + bitmapWidth;
            assert y >= 0 && y <= bitmapHeight : "crop y must be >= 0 and <= " + bitmapHeight;
            width = Math.min(width, bitmapWidth - x);
            height = Math.min(height, bitmapHeight - y);
            return Bitmap.createBitmap(bitmap, x, y, width, height);
        }

    }

    private static class MatrixProcess implements ImageProcess {
        private ArrayList<MatrixSet> matrixSets = new ArrayList<MatrixSet>();

        @Override
        public Bitmap process(Bitmap bitmap) {
            Matrix matrix = new Matrix();
            for (MatrixSet matrixSet : matrixSets) {
                matrixSet.set(matrix, bitmap);
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        public MatrixProcess setRotate(float degrees) {
            matrixSets.add(new MatrixRotate(degrees, -1, -1));
            return this;
        }

        public MatrixProcess setRotate(float degrees, float px, float py) {
            matrixSets.add(new MatrixRotate(degrees, px, py));
            return this;
        }

        public MatrixProcess setScale(float scale) {
            matrixSets.add(new MatrixScale(0, 0, 0, scale));
            return this;
        }

        public MatrixProcess setTargetHeight(int height) {
            matrixSets.add(new MatrixScale(0, height, SCALE_MODE_NOT_LESS_THEN, -1));
            return this;
        }

        public MatrixProcess setTargetSize(int width, int height, int scaleMode) {
            matrixSets.add(new MatrixScale(width, height, scaleMode, -1));
            return this;
        }

        public MatrixProcess setTargetWidth(int width) {
            matrixSets.add(new MatrixScale(width, 0, SCALE_MODE_NOT_LESS_THEN, -1));
            return this;
        }
    }

    private static class MatrixRotate implements MatrixSet {
        private float degrees = 0;
        private float px = -1, py = -1;

        public MatrixRotate(float degrees, float px, float py) {
            this.degrees = degrees;
            this.px = px;
            this.py = py;
        }

        @Override
        public void set(@NonNull Matrix matrix, @NonNull Bitmap bitmap) {
            if (px < 0) {
                px = bitmap.getWidth() / 2;
            }
            if (py < 0) {
                py = bitmap.getHeight() / 2;
            }
            matrix.postRotate(degrees, px, py);
        }
    }

    private static class MatrixScale implements MatrixSet {
        private final float scale;
        private final int height;
        private final int scaleMode;
        private final int width;

        public MatrixScale(int width, int heigt, int scaleMode, float scale) {
            this.width = width;
            this.height = heigt;
            this.scaleMode = scaleMode;
            this.scale = scale;
        }

        @Override
        public void set(@NonNull Matrix matrix, @NonNull Bitmap bitmap) {
            if (scale > 0) {
                matrix.postScale(scale, scale);
            } else {
                float scaleWidth = width * 1.0F / bitmap.getWidth();
                float scaleHeight = height * 1.0F / bitmap.getHeight();
                switch (scaleMode) {
                    case SCALE_MODE_NOT_LESS_THEN:
                        float scaleMax = Math.max(scaleWidth, scaleHeight);
                        matrix.postScale(scaleMax, scaleMax);
                        break;
                    case SCALE_MODE_NOT_MORE_THEN:
                        float scaleMin = Math.min(scaleWidth, scaleHeight);
                        matrix.postScale(scaleMin, scaleMin);
                        break;
                    case SCALE_MODE_X:
                        matrix.postScale(scaleWidth, scaleWidth);
                        break;
                    case SCALE_MODE_Y:
                        matrix.postScale(scaleHeight, scaleHeight);
                        break;
                    default:
                        matrix.postScale(scaleWidth, scaleHeight);
                        break;
                }
            }
        }
    }

    private static class RoundBitmap implements ImageProcess {
        private float radius;
        private int width, height;
        private int x, y;

        public RoundBitmap(int x, int y, int width, int height, float radius) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.radius = radius;
        }

        @Override
        public Bitmap process(@NonNull Bitmap bitmap) {
            if (width < 0) width = bitmap.getWidth();
            if (height < 0) height = bitmap.getHeight();
            if (x < 0) x = (bitmap.getWidth() - width) / 2;
            if (y < 0) y = (bitmap.getHeight() - height) / 2;

            Bitmap.Config config = bitmap.getConfig();
            if (config == Bitmap.Config.RGB_565 || config == Bitmap.Config.ALPHA_8) {
                config = Bitmap.Config.ARGB_4444;
            }
            Bitmap result = Bitmap.createBitmap(width, height, config);
            Canvas canvas = new Canvas(result);

            Rect rect = new Rect();
            rect.left = x;
            rect.top = y;
            rect.right = x + width;
            rect.bottom = y + height;

            RectF rectF = new RectF();
            rectF.left = 0;
            rectF.top = 0;
            rectF.right = width;
            rectF.bottom = height;

            canvas.drawARGB(0, 0, 0, 0);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawRoundRect(rectF, radius, radius, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rectF, paint);
            return result;
        }
    }
}
