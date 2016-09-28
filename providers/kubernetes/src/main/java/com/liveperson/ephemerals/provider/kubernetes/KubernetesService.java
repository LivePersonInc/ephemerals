package com.liveperson.ephemerals.provider.kubernetes;

import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * Created by waseemh on 9/26/16.
 */
public class KubernetesService<T> {

    private T client;

    public KubernetesService(T client) {
        this.client = client;
    }

    public T getClient() {
        return client;
    }

    public static class Builder{

        private ConfigBuilder configBuilder = new ConfigBuilder();

        public Builder withHost(String host) {
            configBuilder.withMasterUrl(host);
            return this;
        }

        public Builder withTrustCerts(boolean trustCerts) {
            configBuilder.withTrustCerts(trustCerts);
            return this;
        }

        public Builder withUsername(String username) {
            configBuilder.withUsername(username);
            return this;
        }

        public Builder withPassword(String password) {
            configBuilder.withPassword(password);
            return this;
        }

        public Builder withNamespace(String namespace) {
            configBuilder.withNamespace(namespace);
            return this;
        }

        public KubernetesService build() {

            return new KubernetesService<KubernetesClient>(
                    new DefaultKubernetesClient(configBuilder.build()));
        }

    }

}