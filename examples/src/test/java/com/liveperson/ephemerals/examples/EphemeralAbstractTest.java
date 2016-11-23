package com.liveperson.ephemerals.examples;

import com.liveperson.ephemerals.provider.kubernetes.KubernetesDeploymentContext;
import com.liveperson.ephemerals.provider.kubernetes.KubernetesEphemeral;

/**
 * Created by waseemh on 9/26/16.
 */
public abstract class EphemeralAbstractTest {

    private static KubernetesDeploymentContext kubernetesDeploymentContext;

    protected static KubernetesDeploymentContext getKubernetesDeploymentContext() {

        if(kubernetesDeploymentContext!=null) {
            return kubernetesDeploymentContext;
        }

        String kubernetesHost = System.getProperty("ephemerals.kubernetes.host");
        String username = System.getProperty("ephemerals.kubernetes.username");
        String password = System.getProperty("ephemerals.kubernetes.password");

        kubernetesDeploymentContext = KubernetesEphemeral.create(kubernetesHost,username,password);
        return kubernetesDeploymentContext;
    }

}
