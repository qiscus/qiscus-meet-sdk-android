package com.qiscus.rtc.sample.utils;

/**
 * Created by rajapulau on 3/13/18.
 */

public interface Action<T> {
    void call(T t);
}

