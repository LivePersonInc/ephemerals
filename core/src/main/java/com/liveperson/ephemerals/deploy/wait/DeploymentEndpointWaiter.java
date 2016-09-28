package com.liveperson.ephemerals.deploy.wait;

import com.liveperson.ephemerals.deploy.Deployment;
import com.liveperson.ephemerals.deploy.DeploymentEndpoints;

/**
 * Created by waseemh on 9/16/16.
 */
public abstract class DeploymentEndpointWaiter extends Waiter {

    protected Deployment deployment;

    public DeploymentEndpointWaiter(Deployment deployment ) {
        this.deployment = deployment;
    }

    protected abstract DeploymentEndpoints.Endpoint getDeploymentEndpoint();

    @Override
    protected boolean isConditionMet() {
        DeploymentEndpoints.Endpoint endpoint = getDeploymentEndpoint();
        return endpoint!=null;
    }

}