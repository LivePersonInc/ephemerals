package com.liveperson.ephemerals.deploy.volume;

/**
 * Created by waseemh on 11/21/16.
 */
public abstract class Volume {

    public abstract static class Builder<T extends Volume> {

        public abstract T build();

    }

}