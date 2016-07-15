package com.xgym.library.helper;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.internal.ButterKnifeProcessor;

public class ButterKnifeHelper {
    private boolean isButterKnife;

    public static ButterKnifeHelper get() {
        return new ButterKnifeHelper();
    }

    public void bind(@NonNull Activity activity) {
        isButterKnife = isUseButterKnife(activity);
        if (isButterKnife) {
            ButterKnife.bind(activity);
        }
    }

    private boolean isUseButterKnife(@NonNull Object obj) {
        String name = obj.getClass().getName() + ButterKnifeProcessor.SUFFIX;
        try {
            Class<?> clazz = Class.forName(name);
            return clazz != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void bind(@NonNull View view) {
        isButterKnife = isUseButterKnife(view);
        if (isButterKnife) {
            ButterKnife.bind(view);
        }
    }

    public void bind(@NonNull Object targetObj, @NonNull Dialog dialog) {
        isButterKnife = isUseButterKnife(targetObj);
        if (isButterKnife) {
            ButterKnife.bind(targetObj, dialog);
        }
    }

    public void bind(@NonNull Object targetObj, @NonNull Activity activity) {
        isButterKnife = isUseButterKnife(targetObj);
        if (isButterKnife) {
            ButterKnife.bind(targetObj, activity);
        }
    }

    public void bind(@NonNull Object targetObj, @NonNull View view) {
        isButterKnife = isUseButterKnife(targetObj);
        if (isButterKnife) {
            ButterKnife.bind(targetObj, view);
        }
    }

    public void unbind(@NonNull Object obj) {
        if (isButterKnife) {
            ButterKnife.unbind(obj);
        }
    }
}
