package com.liveperson.ephemerals;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.liveperson.ephemerals.deploy.*;
import com.liveperson.ephemerals.deploy.probe.HttpProbe;
import com.liveperson.ephemerals.deploy.unit.DeploymentUnit;
import com.liveperson.ephemerals.deploy.unit.DockerDeploymentUnit;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by waseemh on 9/20/16.
 */
public final class WireMockEphemeral extends DeployableEphemeral<URL> {

    private final static int WIREMOCK_PORT = 8080;

    private final String stubMapping;

    protected WireMockEphemeral(Builder builder) {
        super(builder);
        this.stubMapping = builder.stubMapping;
    }

    @Override
    protected DeploymentUnit createDeploymentUnit() {
        return new DockerDeploymentUnit.Builder("wiremock","rodolpheche/wiremock")
                .withCpu(0.5)
                .withMem(512)
                .withHealthProbe(new HttpProbe.Builder()
                        .withPort(WIREMOCK_PORT)
                        .build())
                .withPort(new DeploymentPort.Builder("wiremock-server",WIREMOCK_PORT)
                        .build())
                .build();
    }

    @Override
    protected URL createObject(DeploymentEndpoints endpoints) {
        for(DeploymentEndpoints.Endpoint endpoint : endpoints.list()) {
            if(endpoint.getName().equals("wiremock-server")) {
                String host = endpoint.getHost();
                int port = endpoint.getPort();
                WireMock wireMock = new WireMock(host,port);
                wireMock.register(StubMapping.buildFrom(stubMapping));
                try {
                    return new URL(String.format("http://%s:%s", host, port));
                } catch (MalformedURLException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static class Builder extends DeployableEphemeral.Builder<Builder,URL> {

        private String stubMapping;

        public Builder (DeploymentContext deploymentContext) {
            super(deploymentContext);
        }

        public Builder withStubMapping(String stubMapping) {
            this.stubMapping = stubMapping;
            return this;
        }

        public WireMockEphemeral build() {
            return new WireMockEphemeral(this);
        }

    }


}
