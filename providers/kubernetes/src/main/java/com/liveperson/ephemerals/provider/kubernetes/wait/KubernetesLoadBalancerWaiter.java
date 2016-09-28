package com.liveperson.ephemerals.provider.kubernetes.wait;

import com.liveperson.ephemerals.deploy.Deployment;
import com.liveperson.ephemerals.deploy.wait.Waiter;
import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.List;

/**
 * Created by waseemh on 9/17/16.
 */
public class KubernetesLoadBalancerWaiter extends Waiter {

    private KubernetesClient kubernetesClient;

    private Deployment deployment;

    public KubernetesLoadBalancerWaiter(KubernetesClient kubernetesClient, Deployment deployment) {
        this.kubernetesClient = kubernetesClient;
        this.deployment = deployment;
    }

    @Override
    protected boolean isConditionMet() {
        Service service = kubernetesClient.services().withName(deployment.getId()).get();
        List<LoadBalancerIngress> loadBalancerIngress = service.getStatus().getLoadBalancer().getIngress();
        if (loadBalancerIngress!=null && loadBalancerIngress.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }

}
