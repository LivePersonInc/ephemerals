package com.liveperson.ephemerals.deploy;

/**
 * Created by waseemh on 9/26/16.
 */
public class DeploymentContext  {

    private final DeploymentHandler deploymentHandler;

    private final DeploymentConfiguration deploymentConfiguration;

    public DeploymentContext(DeploymentHandler deploymentHandler, DeploymentConfiguration deploymentConfiguration) {
        this.deploymentHandler = deploymentHandler;
        this.deploymentConfiguration = deploymentConfiguration;
    }

    public DeploymentHandler getDeploymentHandler() {
        return deploymentHandler;
    }

    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

}