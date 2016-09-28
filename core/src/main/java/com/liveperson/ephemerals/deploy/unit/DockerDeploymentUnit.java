package com.liveperson.ephemerals.deploy.unit;

/**
 * A {@link DeploymentUnit} which runs as a Docker container.
 *
 * Created by waseemh on 9/4/16.
 */
public class DockerDeploymentUnit extends ContainerizedDeploymentUnit {

    public DockerDeploymentUnit(Builder builder) {
        super(builder);
    }

    public static class Builder extends ContainerizedDeploymentUnit.Builder {

        public Builder(String name, String image) {
            super(name,image);
        }

        public DockerDeploymentUnit build() {
            return new DockerDeploymentUnit(this);
        }

    }
}