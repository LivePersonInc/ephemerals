package com.liveperson.ephemerals.deploy.wait;

import com.liveperson.ephemerals.deploy.Deployment;
import com.liveperson.ephemerals.deploy.DeploymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by waseemh on 9/16/16.
 */
public abstract class DeploymentStatusWaiter extends Waiter {

    protected Deployment deployment;

    protected DeploymentStatus desiredStatus;

    private final static Logger logger = LoggerFactory.getLogger(DeploymentStatusWaiter.class);

    public DeploymentStatusWaiter(Deployment deployment, DeploymentStatus desiredStatus) {
        this.deployment = deployment;
        this.desiredStatus = desiredStatus;
    }

    public abstract DeploymentStatus getDeploymentStatus();

    @Override
    protected boolean isConditionMet() {
        try {
            DeploymentStatus deploymentStatus = getDeploymentStatus();
            logger.info("Deployment {} status: {} - desired status: {}",deployment.getId(),deploymentStatus,desiredStatus);
            return deploymentStatus.equals(desiredStatus);
        } catch(Exception e) {
            logger.warn("Unable to fetch deployment status");
            return false;
        }
    }

}
