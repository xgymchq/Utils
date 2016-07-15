package com.xgym.library.helper;

import java.lang.reflect.Modifier;

import android.support.annotation.NonNull;

import com.xgym.library.util.Reflect;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class EventBusHelper {
    private static EventBusHelper mInstence;
    private EventBus eventBus;

    public static EventBusHelper get() {
        if (mInstence == null) {
            synchronized (EventBusHelper.class) {
                if (mInstence == null) {
                    mInstence = new EventBusHelper();
                }
            }
        }
        return mInstence;
    }

    public EventBus getEventBus() {
        if (eventBus == null) {
            synchronized (EventBusHelper.class) {
                if (eventBus == null) {
                    eventBus = EventBus.getDefault();
                }
            }
        }
        return eventBus;
    }

    public void setEventBus(@NonNull EventBus eventBus) {
        synchronized (EventBusHelper.class) {
            this.eventBus = eventBus;
        }
    }

    public void register(@NonNull Object obj) {
        if (Reflect.on(obj).methods().withoutMofifier(Modifier.PRIVATE).withAnnotation(Subscribe.class).count() > 0) {
            getEventBus().register(obj);
        }
    }

    public void unregister(@NonNull Object obj) {
        if (getEventBus().isRegistered(obj)) {
            getEventBus().unregister(obj);
        }
    }

    public void post(@NonNull Object event) {
        getEventBus().post(event);
    }

    public void postSticky(@NonNull Object event) {
        getEventBus().postSticky(event);
    }
}
