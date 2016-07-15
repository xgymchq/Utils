package com.xgym.library.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * 获取 GSON 对象
 */
public class GSonHelper {
    private static Gson gson;

    private GSonHelper() {
    }

    public static Gson builder() {
        if (gson == null) {
            synchronized (GSonHelper.class) {
                if (gson == null) {
                    gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL)
                            .create();
                }
            }
        }
        return gson;
    }

    public static <T> String toJSon(T req) {
        return builder().toJson(req);
    }

    public static <T> String toJSon(T req, Type type) {
        return builder().toJson(req, type);
    }

    public static <T> T fromJSon(String res, Type type) {
        return builder().fromJson(res, type);
    }

    public static <T> T fromJSon(String res, Class<T> clazz) {
        return builder().fromJson(res, clazz);
    }
}
