package com.liveperson.ephemerals.deploy;

/**
 *
 * DeploymentPort defines the ports to be handled by a {@link DeploymentHandler},
 * and eventually be exposed as endpoints.
 *
 * Created by waseemh on 9/16/16.
 */
public final class DeploymentPort {

    private final int port;

    private final int externalPort;

    private final String name;

    public DeploymentPort(Builder builder) {
        this.port = builder.port;
        this.externalPort = builder.externalPort;
        this.name = builder.name;
    }

    public int getPort() {
        return port;
    }

    public int getExternalPort() {
        return externalPort;
    }

    public String getName() {
        return name;
    }

    public static class Builder {

        private int port;

        private int externalPort;

        private String name;

        public Builder(String name, int port) {
            this.name = name;
            this.port = port;
            this.externalPort = port;
        }

        public Builder withExternalPort(int port) {
            this.externalPort = port;
            return this;
        }

        public DeploymentPort build() {
            return new DeploymentPort(this);
        }

    }

}
