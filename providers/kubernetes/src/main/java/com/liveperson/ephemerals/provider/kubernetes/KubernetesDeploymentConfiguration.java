package com.liveperson.ephemerals.provider.kubernetes;

import com.liveperson.ephemerals.deploy.DeploymentConfiguration;

/**
 * Created by waseemh on 9/5/16.
 */
public class KubernetesDeploymentConfiguration extends DeploymentConfiguration {

    private KubernetesServiceType kubernetesServiceType;

    private KubernetesDeploymentConfiguration(Builder builder) {
        super(builder);
        this.kubernetesServiceType = builder.kubernetesServiceType;
    }

    public KubernetesServiceType getKubernetesServiceType() {
        return kubernetesServiceType;
    }

    public static class Builder extends DeploymentConfiguration.Builder<Builder> {

        private KubernetesServiceType kubernetesServiceType = KubernetesServiceType.NODE_PORT;

        public KubernetesDeploymentConfiguration.Builder withServiceType(KubernetesServiceType kubernetesServiceType) {
            this.kubernetesServiceType = kubernetesServiceType;
            return this;
        }

        public KubernetesDeploymentConfiguration build() {
            return new KubernetesDeploymentConfiguration(this);
        }
    }


}