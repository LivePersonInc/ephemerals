package com.liveperson.ephemerals.deploy;

/**
 * DeploymentHandler can take a Deployment entity and deploy it using implementation of deployment providers.
 *
 * DeploymentHandler should handle the whole lifecycle of deployment, including:
 *  - Interpreting deployment configuration
 *  - Connecting to deployment provider
 *  - Installing and destroying deployment unit
 *
 *  {@link DeploymentEndpoints} are the main outcome of a deployment.
 *  Using endpoints, we can connect to and interact with deployment unit.
 *  DeploymentHandler should handle the initalization of DeploymentEndpoints,
 *  according to the exposed endpoints in the deployment provider.
 *
 * Created by waseemh on 9/4/16.
 */
public interface DeploymentHandler {

    DeploymentEndpoints deploy(Deployment deployment);

    void destroy(Deployment deployment);

}