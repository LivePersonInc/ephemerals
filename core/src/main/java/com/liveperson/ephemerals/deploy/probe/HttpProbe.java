package com.liveperson.ephemerals.deploy.probe;

/**
 * Created by waseemh on 9/5/16.
 */
public final class HttpProbe extends Probe {

    private final String path;

    private final int port;

    public HttpProbe(Builder builder) {
        super(builder);
        this.path = builder.path;
        this.port = builder.port;
    }

    public String getPath() {
        return path;
    }

    public int getPort() {
        return port;
    }

    public static class Builder extends Probe.Builder {

        private String path = "/";
        private int port = 80;

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public HttpProbe build() {
            return new HttpProbe(this);
        }
    }

}