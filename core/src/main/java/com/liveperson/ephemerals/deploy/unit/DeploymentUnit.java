package com.liveperson.ephemerals.deploy.unit;

import com.liveperson.ephemerals.deploy.DeploymentPort;
import com.liveperson.ephemerals.deploy.probe.Probe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Specification of deployment resources, runtime, network and health check.
 *
 * Created by waseemh on 9/4/16.
 */
public class DeploymentUnit {

    /**
     * Namespace of deployment unit
     */
    private final String name;

    /**
     * Health Probe
     */
    private final Probe healthProbe;

    /**
     * Readiness Probe
     */
    private final Probe readinessProbe;

    /**
     * Deployment unit ports
     */
    private final List<DeploymentPort> ports;

    /**
     * CPU resource requirements
     */
    private final double cpu;

    /**
     * Memory resource requirements
     */
    private final int mem;

    /**
     * Commandline arguments
     */
    private final Map<String,String> cmdArgs;

    /**
     * Environment variables
     */
    private final Map<String,String> envVars;

    public DeploymentUnit(Builder builder ) {
        this.name = builder.name;
        this.healthProbe = builder.healthProbe;
        this.readinessProbe = builder.readinessProbe;
        this.ports = builder.ports;
        this.cpu = builder.cpu;
        this.mem = builder.mem;
        this.cmdArgs = builder.cmdArgs;
        this.envVars = builder.envVars;
    }

    public double getCpu() {
        return cpu;
    }

    public int getMem() {
        return mem;
    }

    public Map<String, String> getCmdArgs() {
        return cmdArgs;
    }

    public Map<String, String> getEnvVars() {
        return envVars;
    }

    public String getName() {
        return name;
    }

    public Probe getHealthProbe() {
        return healthProbe;
    }

    public Probe getReadinessProbe() {return readinessProbe; }

    public List<DeploymentPort> getPorts() {
        return ports;
    }

    public static class Builder {

        private String name;
        private Probe healthProbe;
        private Probe readinessProbe;
        private List<DeploymentPort> ports = new ArrayList<>();
        private double cpu = 0.5;
        private int mem = 1024;
        private Map<String, String> cmdArgs = new HashMap<>();
        private Map<String, String> envVars = new HashMap<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder withHealthProbe(Probe probe) {
            this.healthProbe = probe;
            return this;
        }

        public Builder withReadinessProbe(Probe probe) {
            this.readinessProbe = probe;
            return this;
        }

        public Builder withPorts(List<DeploymentPort> ports) {
            this.ports.addAll(ports);
            return this;
        }

        public Builder withPort(DeploymentPort port) {
            this.ports.add(port);
            return this;
        }

        public Builder withCpu(double cpu) {
            this.cpu = cpu;
            return this;
        }

        public Builder withMem(int mem) {
            this.mem = mem;
            return this;
        }

        public Builder withCmdArgs(Map<String, String> cmdArgs) {
            this.cmdArgs.putAll(cmdArgs);
            return this;
        }

        public Builder withCmdArg(String key, String value) {
            this.cmdArgs.put(key,value);
            return this;
        }

        public Builder withEnvVars(Map<String, String> envVars) {
            this.envVars.putAll(envVars);
            return this;
        }

        public Builder withEnvVar(String key, String value) {
            this.envVars.put(key,value);
            return this;
        }

        public DeploymentUnit build() {
            return new DeploymentUnit(this);
        }
    }

}