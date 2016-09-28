package com.liveperson.ephemerals.junit;

import com.liveperson.ephemerals.Ephemeral;
import org.junit.rules.ExternalResource;

/**
 * Created by waseemh on 9/21/16.
 */
public class EphemeralResource<T> extends ExternalResource {

    private Ephemeral<T> ephemeral;

    public EphemeralResource(Ephemeral ephemeral) {
        this.ephemeral = ephemeral;
    }

    @Override
    protected void before() throws Throwable {
        ephemeral.get();
    }

    @Override
    protected void after() {
        ephemeral.destroy();
    }

    public Ephemeral<T> getEphemeral() {
        return ephemeral;
    }

    public T get() {
        return getEphemeral().get();
    }

}