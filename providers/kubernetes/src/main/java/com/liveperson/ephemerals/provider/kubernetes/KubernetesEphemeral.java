package com.liveperson.ephemerals.provider.kubernetes;

/**
 *
 * Facade API for creating KubernetesDeploymentContext instances
 *
 * Created by waseemh on 11/23/16.
 */
public class KubernetesEphemeral {

    /**
     *
     * @param kubernetesHost
     * @param username
     * @param password
     * @param namespace
     * @param trustCerts
     * @param kubernetesServiceType
     * @return
     *
     */
    public static KubernetesDeploymentContext create(String kubernetesHost, String username, String password, String namespace, boolean trustCerts, KubernetesServiceType kubernetesServiceType) {
        return new KubernetesDeploymentContext(
                new KubernetesDeploymentHandler.Builder(
                        new KubernetesService.Builder()
                                .withHost(kubernetesHost)
                                .withUsername(username)
                                .withPassword(password)
                                .withNamespace(namespace)
                                .withTrustCerts(trustCerts)
                                .build()).build()
                , new KubernetesDeploymentConfiguration.Builder()
                .withServiceType(kubernetesServiceType)
                .build());

    }

    /**
     *
     * @param kubernetesHost
     * @param username
     * @param password
     * @param namespace
     * @return
     */
    public static KubernetesDeploymentContext create(String kubernetesHost, String username, String password, String namespace) {
        return create(kubernetesHost,username,password,namespace,true,KubernetesServiceType.NODE_PORT);
    }

    /**
     *
     * @param kubernetesHost
     * @param username
     * @param password
     * @return
     */
    public static KubernetesDeploymentContext create(String kubernetesHost, String username, String password) {
        return create(kubernetesHost,username,password,"default");
    }

    /**
     *
     * @param kubernetesHost
     * @param username
     * @param password
     * @param kubernetesServiceType
     * @return
     */
    public static KubernetesDeploymentContext create(String kubernetesHost, String username, String password, KubernetesServiceType kubernetesServiceType) {
        return create(kubernetesHost,username,password,"default",true,kubernetesServiceType);
    }

}
