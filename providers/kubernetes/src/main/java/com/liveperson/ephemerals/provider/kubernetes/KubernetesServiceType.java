package com.liveperson.ephemerals.provider.kubernetes;

/**
 * Created by waseemh on 9/15/16.
 */
public enum KubernetesServiceType {

    LOAD_BALANCER("LoadBalancer"),NODE_PORT("NodePort"),CLUSTER_IP("ClusterIP");

    private String value;

     KubernetesServiceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static KubernetesServiceType getByValue(String value) {
        for(KubernetesServiceType kubernetesServiceType : KubernetesServiceType.values()) {
            if(kubernetesServiceType.getValue().equals(value)) {
                return kubernetesServiceType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.value;
    }

}