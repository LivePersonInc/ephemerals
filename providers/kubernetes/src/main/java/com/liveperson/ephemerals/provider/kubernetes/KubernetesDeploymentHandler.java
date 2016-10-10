package com.liveperson.ephemerals.provider.kubernetes;

import com.liveperson.ephemerals.deploy.*;
import com.liveperson.ephemerals.provider.kubernetes.wait.KubernetesDeploymentEndpointWaiter;
import com.liveperson.ephemerals.provider.kubernetes.wait.KubernetesDeploymentStatusWaiter;
import com.liveperson.ephemerals.provider.kubernetes.wait.KubernetesLoadBalancerWaiter;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by waseemh on 9/4/16.
 */
public final class KubernetesDeploymentHandler implements DeploymentHandler {

    private final static Logger logger = LoggerFactory.getLogger(KubernetesDeploymentHandler.class);

    public final static String DEPLOYMENT_LABEL_KEY = "ephemeral-id";

    private KubernetesClient kubernetesClient;

    private KubernetesDeploymentStrategy kubernetesDeploymentStrategy;

    private KubernetesDeploymentHandler(Builder builder) {
        this.kubernetesClient = builder.kubernetesService.getClient();
        this.kubernetesDeploymentStrategy = builder.kubernetesDeploymentStrategy;
    }

    @Override
    public DeploymentEndpoints deploy(Deployment deployment) {

        // Create service on KubernetesServiceBuilder cluster
        Service service = kubernetesDeploymentStrategy.service(deployment);
        kubernetesClient.services().inNamespace(kubernetesClient.getNamespace()).create(service);

        // Create pods and replication controller
        ReplicationController replicationController = kubernetesDeploymentStrategy.replicationController(deployment);
        kubernetesClient.replicationControllers().inNamespace
                (kubernetesClient.getNamespace()).create(replicationController);

        // Wait for deployment to finish by polling KubernetesServiceBuilder and waiting for 'FINISHED' status
        KubernetesDeploymentStatusWaiter deploymentStatusWaiter =
                new KubernetesDeploymentStatusWaiter(kubernetesClient, deployment, DeploymentStatus.FINISHED);
        try {
            deploymentStatusWaiter.start();
        } catch (TimeoutException e) {
            throw new DeploymentException(e);
        }

        DeploymentEndpoints endpoints = new DeploymentEndpoints();

        for(DeploymentPort deploymentPort : deployment.getDeploymentUnit().getPorts()) {

            KubernetesDeploymentEndpointWaiter kubernetesDeploymentEndpointWaiter =
                    new KubernetesDeploymentEndpointWaiter(kubernetesClient,deployment,deploymentPort);
            try {
                kubernetesDeploymentEndpointWaiter.start();
                endpoints.add(kubernetesDeploymentEndpointWaiter.getEndpoint());
            } catch (TimeoutException e) {
                throw new DeploymentException(e);
            }
        }

        return endpoints;

    }

    @Override
    public void destroy(Deployment deployment) {

        List<ReplicationController> apps =
                kubernetesClient.replicationControllers()
                        .withLabel(DEPLOYMENT_LABEL_KEY,deployment.getId())
                        .list().getItems();

        for (ReplicationController rc : apps) {

            String deploymentId = rc.getMetadata().getName();

            logger.info("Deleting service, replication controller and pods for deployment: {}", deploymentId);

            Service svc = kubernetesClient.services().withName(deploymentId).get();
            if(svc.getSpec().getType().equals(KubernetesServiceType.LOAD_BALANCER.getValue())) {

                KubernetesLoadBalancerWaiter kubernetesLoadBalancerWaiter =
                        new KubernetesLoadBalancerWaiter(kubernetesClient, deployment);
                try {
                    kubernetesLoadBalancerWaiter.start();
                } catch (TimeoutException e) {
                    throw new DeploymentException(e);
                }
            }

            Boolean svcDeleted = kubernetesClient.services().withName(deploymentId).delete();
            logger.info("Deleted service for deployment: {} {}", deploymentId, svcDeleted);

            Boolean rcDeleted = kubernetesClient.replicationControllers().withName(deploymentId).delete();
            logger.info("Deleted replication controller for deployment: {} {}", deploymentId, rcDeleted);

            Map<String, String> selector = new HashMap<>();
            selector.put(DEPLOYMENT_LABEL_KEY, deploymentId);
            Boolean podDeleted = kubernetesClient.pods().withLabels(selector).delete();
            logger.info("Deleted pods for deployment: {} {}", deploymentId, podDeleted);

        }
    }

    public static class Builder {

        private KubernetesService<KubernetesClient> kubernetesService;

        private KubernetesDeploymentStrategy kubernetesDeploymentStrategy = new DefaultKubernetesDeploymentStrategy();

        public Builder (KubernetesService kubernetesService) {
            this.kubernetesService = kubernetesService;
        }

        public Builder withKubernetesDeploymentStategy(KubernetesDeploymentStrategy kubernetesDeploymentStrategy) {
            this.kubernetesDeploymentStrategy = kubernetesDeploymentStrategy;
            return this;
        }

        public KubernetesDeploymentHandler build() {
            return new KubernetesDeploymentHandler(this);
        }

    }


}