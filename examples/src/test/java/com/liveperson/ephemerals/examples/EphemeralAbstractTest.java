package com.liveperson.ephemerals.examples;

import com.liveperson.ephemerals.provider.kubernetes.KubernetesService;

/**
 * Created by waseemh on 9/26/16.
 */
public abstract class EphemeralAbstractTest {

    protected static KubernetesService getKubernetesService() {
        return null;
    }

}
