package com.liveperson.ephemerals.provider.kubernetes;

import com.liveperson.ephemerals.deploy.DeploymentContext;

/**
 * Created by waseemh on 9/26/16.
 */
public class KubernetesDeploymentContext extends DeploymentContext {

    public KubernetesDeploymentContext(KubernetesDeploymentHandler kubernetesDeploymentHandler) {
        super(kubernetesDeploymentHandler, new KubernetesDeploymentConfiguration.Builder().build());
    }

    public KubernetesDeploymentContext(KubernetesDeploymentHandler kubernetesDeploymentHandler, KubernetesDeploymentConfiguration kubernetesDeploymentConfiguration) {
        super(kubernetesDeploymentHandler, kubernetesDeploymentConfiguration);
    }

}