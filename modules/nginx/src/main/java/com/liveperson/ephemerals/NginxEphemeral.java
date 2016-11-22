package com.liveperson.ephemerals;

import com.liveperson.ephemerals.deploy.DeploymentContext;
import com.liveperson.ephemerals.deploy.DeploymentEndpoints;
import com.liveperson.ephemerals.deploy.DeploymentPort;
import com.liveperson.ephemerals.deploy.probe.HttpProbe;
import com.liveperson.ephemerals.deploy.unit.DeploymentUnit;
import com.liveperson.ephemerals.deploy.unit.DockerDeploymentUnit;
import com.liveperson.ephemerals.deploy.volume.Volume;
import com.liveperson.ephemerals.deploy.volume.VolumeMount;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by waseemh on 9/20/16.
 */
public class NginxEphemeral extends DeployableEphemeral<URL> {

    private final static int NGINX_PORT = 80;

    private final Volume staticContentVolume;

    protected NginxEphemeral(Builder builder) {
        super(builder);
        this.staticContentVolume = builder.staticContentVolume;
    }

    @Override
    protected DeploymentUnit createDeploymentUnit() {

        DeploymentUnit.Builder builder =  new DockerDeploymentUnit.Builder("nginx","nginx:1.9.4")
                .withCpu(0.5)
                .withMem(512)
                .withHealthProbe(new HttpProbe.Builder()
                        .withPort(NGINX_PORT)
                        .build())
                .withPort(new DeploymentPort.Builder("nginx-server",NGINX_PORT)
                        .build());

        if(staticContentVolume!=null) {
            builder.withVolume(new VolumeMount("static-content","/usr/share/nginx/html"),staticContentVolume);
        }

        return builder.build();

    }

    @Override
    protected URL createObject(DeploymentEndpoints endpoints) {
        for(DeploymentEndpoints.Endpoint endpoint : endpoints.list()) {
            if(endpoint.getName().equals("nginx-server")) {
                String host = endpoint.getHost();
                int port = endpoint.getPort();
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

        private Volume staticContentVolume;

        public Builder (DeploymentContext deploymentContext) {
            super(deploymentContext);
        }

        public Builder withStaticContent(Volume staticContentVolume) {
            this.staticContentVolume = staticContentVolume;
            return this;
        }

        public NginxEphemeral build() {
            return new NginxEphemeral(this);
        }

    }
}
