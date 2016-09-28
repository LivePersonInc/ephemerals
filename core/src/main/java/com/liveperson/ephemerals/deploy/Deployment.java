package com.liveperson.ephemerals.deploy;

import com.liveperson.ephemerals.deploy.unit.DeploymentUnit;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Deployment is a major entity which holds information about the deployment spec and process of a deployment unit.
 * {@link DeploymentHandler} uses Deployment objects to perform and delete the deployment, based on its specification.
 *
 * Created by waseemh on 9/5/16.
 */
public final class Deployment {

    /*
     * Deployment ID: composed by DeploymentUnit name + random string.
     */
    private final String id;

    private final DeploymentUnit deploymentUnit;

    private final DeploymentConfiguration deploymentConfiguration;

    public Deployment(Builder builder) {
        this.deploymentUnit = builder.deploymentUnit;
        this.deploymentConfiguration = builder.deploymentConfiguration;
        this.id = DeploymentIdentifierGenerator.generate(builder.deploymentUnit.getName());
    }

    public String getId() {
        return id;
    }

    public DeploymentUnit getDeploymentUnit() {
        return deploymentUnit;
    }

    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    /**
     *
     */
    public static class DeploymentIdentifierGenerator {

        private final static SecureRandom random = new SecureRandom();

        public static String generate(String prefix) {
            return prefix + "-" + new BigInteger(32, random).toString(32);
        }
    }

    public static class Builder {

        private DeploymentUnit deploymentUnit;
        private DeploymentConfiguration deploymentConfiguration;

        public Builder (DeploymentUnit deploymentUnit, DeploymentConfiguration deploymentConfiguration) {
            this.deploymentUnit = deploymentUnit;
            this.deploymentConfiguration = deploymentConfiguration;
        }

        public Deployment build() {
            return new Deployment(this);
        }
    }

}