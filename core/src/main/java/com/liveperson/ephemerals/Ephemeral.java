package com.liveperson.ephemerals;

/**
 * Created by waseemh on 9/16/16.
 */
public interface Ephemeral<T> {

    T get();

    void destroy();

}