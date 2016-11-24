package com.liveperson.ephemerals.deploy;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Network endpoints exposed by a deployment.
 *
 * Usually, {@link DeploymentHandler} will provide such endpoints when deployment is done.
 *
 * Default value is null
 *
 * Created by waseemh on 9/16/16.
 */
public class DeploymentEndpoints {

    List<Endpoint> endpoints = new ArrayList<>();

    public List<Endpoint> list() {
        return endpoints;
    }

    public void add(Endpoint endpoint) {
        endpoints.add(endpoint);
    }

    public static class Endpoint {

        public Endpoint(String name, String host, int port) {
            this.name = name;
            this.host = host;
            this.port = port;
        }

        private String name;

        private int port;

        private String host;

        public String getName() {
            return name;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        @Override
        public String toString() {
            return String.format("EP name=%s, host=%s, port=%s",name,host,port);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Endpoint endpoint : endpoints) {
            stringBuilder.append(endpoint);
            stringBuilder.append(";");
        }
        return stringBuilder.toString();
    }

}