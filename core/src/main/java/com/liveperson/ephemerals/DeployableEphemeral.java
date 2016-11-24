package com.liveperson.ephemerals;

import com.liveperson.ephemerals.deploy.*;
import com.liveperson.ephemerals.deploy.unit.DeploymentUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by waseemh on 9/4/16.
 */
public abstract class DeployableEphemeral<T> implements Ephemeral<T> {

    private final static Logger logger = LoggerFactory.getLogger(DeployableEphemeral.class);

    private final DeploymentContext deploymentContext;

    private Deployment deployment;

    private T object;

    protected DeployableEphemeral(Builder builder) {
        this.deploymentContext = builder.deploymentContext;
        this.deployment = null;
        this.object = null;
    }

    public T get() {

        if(object!=null) {
            return object;
        }

        // Deploy ephemeral unit and fetch deployment endpoints
        logger.info("Creating deployment unit");
        DeploymentUnit deploymentUnit = createDeploymentUnit();
        logger.info("Creating deployment");
        deployment = new Deployment.Builder(deploymentUnit, deploymentContext.getDeploymentConfiguration())
                .build();

        // Do deploy!
        DeploymentEndpoints endpoints;
        try {
            logger.info("Deploying deployment {}",deployment.getId());
            endpoints = deploymentContext.getDeploymentHandler().deploy(deployment);
            logger.info("Deployment {} done",deployment.getId());
            logger.info("Endpoints found for deployment {}: {}",deployment.getId(),endpoints);
        } catch(Exception e) {
            logger.error("Error while deploying {}",deployment.getId(),e);
            throw new DeploymentException(e);
        }

        // Create object using endpoints
        try {
            object = createObject(endpoints);
            return object;
        } catch (Exception e) {
            logger.error("Error while creating object for deployment {}",deployment.getId(),e);
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
        if(deployment==null) {
            logger.warn("Nothing to destroy...");
            return;
        }
        try {
            logger.info("Destroying deployment {}",deployment.getId());
            deploymentContext.getDeploymentHandler().destroy(deployment);
            logger.info("Deployment {} destroyed successfully",deployment.getId());
            deployment=null;
            object=null;
        } catch (Exception e) {
            logger.error("Error while destroying deployment",e);
            throw new DeploymentException(e);
        }
    }

    /**
     * Concrete DeployableEphemeral must define its {@link DeploymentUnit}.
     *
     * @return DeploymentUnit deployment unit
     */
    protected abstract DeploymentUnit createDeploymentUnit();

    /**
     * Concrete DeployableEphemeral must define its {@link Object}, based on {@link DeploymentEndpoints}
     *
     * Default value: null
     *
     * @return T ephemeral object
     */
    protected abstract T createObject(DeploymentEndpoints endpoints);

    /**
     * {@link DeployableEphemeral} builder
     */
    public abstract static class Builder<B extends Builder,T> {

        private DeploymentContext deploymentContext;

        public Builder (DeploymentContext deploymentContext) {
            this.deploymentContext = deploymentContext;
        }

        public abstract DeployableEphemeral<T> build();
    }

}