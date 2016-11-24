package com.liveperson.ephemerals.deploy.wait;

import com.liveperson.ephemerals.deploy.Deployment;
import com.liveperson.ephemerals.deploy.DeploymentEndpoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by waseemh on 9/16/16.
 */
public abstract class DeploymentEndpointWaiter extends Waiter {

    private final static Logger logger = LoggerFactory.getLogger(DeploymentEndpointWaiter.class);

    protected Deployment deployment;

    public DeploymentEndpointWaiter(Deployment deployment ) {
        this.deployment = deployment;
    }

    protected abstract DeploymentEndpoints.Endpoint getDeploymentEndpoint();

    @Override
    protected boolean isConditionMet() {

        try {
            DeploymentEndpoints.Endpoint endpoint = getDeploymentEndpoint();
            if(endpoint!=null) {
                logger.info("Deployment {} endpoint: {}", deployment.getId(), endpoint);
                return true;
            }
            else {
                logger.info("Deployment {} endpoint not ready", deployment.getId());
                return false;
            }
        } catch(Exception e) {
            logger.warn("Unable to fetch deployment endpoint");
            return false;
        }
    }

}