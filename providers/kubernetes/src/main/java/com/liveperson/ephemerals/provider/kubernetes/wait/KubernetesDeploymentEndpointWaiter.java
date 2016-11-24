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

        String serviceType = kubernetesClient.services().withName(deployment.getId()).get().getSpec().getType();
        logger.debug("Kubernetes service type: " + serviceType);
        if (serviceType.equals("LoadBalancer")) {
            ip = kubernetesClient.services().withName(deployment.getId()).get().getStatus().getLoadBalancer().getIngress().get(0).getIp();
        }

        else { //nodeport
            List<ServicePort> servicePortList = kubernetesClient.services().withName(deployment.getId()).get().getSpec().getPorts();
            for (ServicePort servicePort : servicePortList) {
                if (servicePort.getPort().equals(deploymentPort.getPort())) {
                    port = servicePort.getNodePort();
                }
            }

            /**
             * Fetch Node IP address:
             *  - External IP takes precedence over internal IP
             *  - If external IP isn't found, return internal IP
             *  - If both IPs not found, return null
             */

            //Since node port is shared across all nodes, use first node
            List<NodeAddress> nodeAddressList = kubernetesClient.nodes().list().getItems().get(0).getStatus().getAddresses();

            String nodeInternalIp=null, nodeExternalIp=null;
            for (NodeAddress nodeAddress : nodeAddressList) {
                if (nodeAddress.getType().equals("ExternalIP")) {
                    nodeExternalIp = nodeAddress.getAddress();
                }
                else if(nodeAddress.getType().equals("InternalIP")) {
                    nodeInternalIp = nodeAddress.getAddress();
                }
            }
            //External IP takes precedence over internal IP
            if(nodeExternalIp!=null) {
                ip = nodeExternalIp;
                logger.debug("Using node ExternalIP: " + nodeExternalIp);
            }
            else if(nodeInternalIp!=null) {
                ip = nodeInternalIp;
                logger.debug("Using node InternalIP: " + nodeInternalIp);
            }
        }

        if (ip == null) {
            logger.info("Endpoint not found");
            return null;
        } else {
            logger.info("Endpoint found...");
            logger.info(String.format("Checking connection to endpoint IP %s and port %d", ip, port));
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

    }

    public DeploymentEndpoints.Endpoint getEndpoint() {
        return endpoint;
    }

}
