package com.liveperson.ephemerals.deploy;

/**
 *
 * Created by waseemh on 9/5/16.
 */
public class DeploymentConfiguration {

    /**
     * Number of deployment replicas
     */
    private final int replicas;

    /**
     * Maximum number of deployment retries
     */
    private final int maxDeploymentRetries;

    protected DeploymentConfiguration (Builder builder) {
        this.replicas = builder.replicas;
        this.maxDeploymentRetries = builder.maxDeploymentRetries;
    }

    public int getMaxDeploymentRetries() {
        return maxDeploymentRetries;
    }

    public int getReplicas() {
        return replicas;
    }

    public static class Builder<T extends Builder> {

        private int replicas = 1;
        private int maxDeploymentRetries = 3;

        public T withReplicas(int replicas) {
            this.replicas = replicas;
            return (T) this;
        }

        public T withMaxDeploymentRetries(int maxDeploymentRetries) {
            this.maxDeploymentRetries = maxDeploymentRetries;
            return (T) this;
        }

        public DeploymentConfiguration build() {
            return new DeploymentConfiguration(this);
        }
    }

}