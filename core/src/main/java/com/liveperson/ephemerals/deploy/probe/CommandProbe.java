package com.liveperson.ephemerals.deploy.probe;

/**
 * Created by waseemh on 9/5/16.
 */
public final class CommandProbe extends Probe {

    private final String cmd;

    private CommandProbe(Builder builder) {
        super(builder);
        this.cmd = builder.cmd;
    }

    public String getCmd() {
        return cmd;
    }

    public static class Builder extends Probe.Builder {

        private String cmd;

        public Builder(String cmd) {
            this.cmd = cmd;
        }

        public Probe build() {
            return new CommandProbe(this);
        }

    }
}