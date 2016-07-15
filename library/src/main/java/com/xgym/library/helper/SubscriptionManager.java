package com.xgym.library.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import rx.Subscription;

public class SubscriptionManager {
    private static final SubscriptionManager mManager = new SubscriptionManager();
    private Map<Object, ArrayList<Subscription>> subscriptionMapper = new WeakHashMap<Object, ArrayList<Subscription>>();

    public static SubscriptionManager getManager() { 
        return mManager;
    }

    public void put(Object tag, Subscription subscription) {
        if (!subscription.isUnsubscribed()) {
            ArrayList<Subscription> subscriptionList = subscriptionMapper.get(tag);
            if (subscriptionList == null) {
                subscriptionList = new ArrayList<Subscription>();
                subscriptionMapper.put(tag, subscriptionList);
            }
            if (!subscriptionList.contains(subscription)) {
                subscriptionList.add(subscription);
            }
        }
        clean();
    }

    private void clean() {
        Iterator<Map.Entry<Object, ArrayList<Subscription>>> iterator = subscriptionMapper.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, ArrayList<Subscription>> entry = iterator.next();
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue().isEmpty()) {
                if (entry.getValue() != null) {
                    for (Subscription subscription : entry.getValue()) {
                        if (!subscription.isUnsubscribed()) {
                            subscription.unsubscribe();
                        }
                    }
                    entry.getValue().clear();
                }
                iterator.remove();
            }
        }
    }

    public void remove(Object tag) {
        ArrayList<Subscription> subscriptions = subscriptionMapper.get(tag);
        if (subscriptions != null) {
            for (Subscription subscription : subscriptions) {
                if (!subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
            }
            subscriptionMapper.remove(tag);
        }
        clean();
    }

    public void unsubscribe(Subscription subscription) {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        for (Map.Entry<Object, ArrayList<Subscription>> entry : subscriptionMapper.entrySet()) {
            if (entry.getValue().contains(subscription)) {
                entry.getValue().remove(subscription);
            }
        }
        clean();
    }
}
