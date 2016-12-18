package com.liveperson.ephemerals.deploy.probe;

/**
 * Created by waseemh on 9/5/16.
 */
public abstract class Probe {

    private final ProbeType probeType;

    private final int delay;

    private final int timeout;

    private final int period;

    protected Probe(Builder builder) {
        this.probeType = builder.probeType;
        this.delay = builder.delay;
        this.timeout = builder.timeout;
        this.period = builder.period;
    }

    public ProbeType getProbeType() {
        return probeType;
    }

    public int getDelay() {
        return delay;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getPeriod() {
        return period;
    }

    public abstract static class Builder<T extends Probe> {

        private ProbeType probeType = ProbeType.HEALTH;

        private int delay = 10;

        private int timeout = 60;

        private int period = 60;

        public Builder withType(ProbeType type) {
            this.probeType = type;
            return this;
        }

        public Builder withDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public Builder withTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder withPeriod(int period) {
            this.period = period;
            return this;
        }

        public abstract T build();

    }

    public enum ProbeType {
        HEALTH, READINESS;
    }

}