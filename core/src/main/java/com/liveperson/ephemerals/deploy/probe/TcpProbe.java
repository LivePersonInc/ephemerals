package com.liveperson.ephemerals.deploy.probe;

/**
 * Created by waseemh on 9/5/16.
 */
public final class TcpProbe extends Probe {

    private final int port;

    protected TcpProbe(Builder builder) {
        super(builder);
        this.port = builder.port;
    }

    public int getPort() {
        return port;
    }

    public static class Builder extends Probe.Builder {

        private int port;

        public Builder(int port) {
            this.port = port;
        }

        public TcpProbe build() {
            return new TcpProbe(this);
        }
    }
}