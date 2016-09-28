package com.liveperson.ephemerals.provider.kubernetes.wait;

import com.liveperson.ephemerals.deploy.Deployment;
import com.liveperson.ephemerals.deploy.DeploymentEndpoints;
import com.liveperson.ephemerals.deploy.DeploymentPort;
import com.liveperson.ephemerals.deploy.wait.DeploymentEndpointWaiter;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created by waseemh on 9/16/16.
 */
public class KubernetesDeploymentEndpointWaiter extends DeploymentEndpointWaiter {

    private KubernetesClient kubernetesClient;

    private DeploymentPort deploymentPort;

    private DeploymentEndpoints.Endpoint endpoint;

    private final static Logger logger = LoggerFactory.getLogger(KubernetesDeploymentEndpointWaiter.class);

    public KubernetesDeploymentEndpointWaiter(KubernetesClient client, Deployment deployment, DeploymentPort deploymentPort) {
        super(deployment);
        this.kubernetesClient = client;
        this.deploymentPort = deploymentPort;
    }

    @Override
    protected DeploymentEndpoints.Endpoint getDeploymentEndpoint() {

        String ip = null;
        int port = 0;

        try {
            String serviceType = kubernetesClient.services().withName(deployment.getId()).get().getSpec().getType();

            if (serviceType.equals("LoadBalancer")) {
                ip = kubernetesClient.services().withName(deployment.getId()).get().getStatus().getLoadBalancer().getIngress().get(0).getIp();
            } else { //nodeport
                List<ServicePort> servicePortList = kubernetesClient.services().withName(deployment.getId()).get().getSpec().getPorts();
                for (ServicePort servicePort : servicePortList) {
                    if (servicePort.getPort().equals(deploymentPort.getPort())) {
                        port = servicePort.getNodePort();
                    }
                }
                List<NodeAddress> nodeAddressList = kubernetesClient.nodes().list().getItems().get(0).getStatus().getAddresses();
                for (NodeAddress nodeAddress : nodeAddressList) {
                    if (nodeAddress.getType().equals("ExternalIP")) {
                        ip = nodeAddress.getAddress();
                    }
                }
            }

            if (ip == null) {
                return null;
            } else {
                logger.info("External endpoint found...");
                logger.info(String.format("Checking connection to external endpoint IP %s and port %d", ip, port));
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(ip, port), 2 * 1000);
                    logger.info("Endpoint is reachable");
                    endpoint = new DeploymentEndpoints.Endpoint(deploymentPort.getName(),ip,port);
                    return endpoint;
                } catch (IOException e) {
                    logger.warn("Endpoint is unreachable");
                    return null; // Either timeout or unreachable or failed DNS lookup.
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    public DeploymentEndpoints.Endpoint getEndpoint() {
        return endpoint;
    }

}
