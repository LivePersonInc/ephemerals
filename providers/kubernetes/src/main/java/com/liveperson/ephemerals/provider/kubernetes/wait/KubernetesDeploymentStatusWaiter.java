package com.liveperson.ephemerals.provider.kubernetes.wait;

import com.liveperson.ephemerals.deploy.Deployment;
import com.liveperson.ephemerals.deploy.DeploymentStatus;
import com.liveperson.ephemerals.deploy.wait.DeploymentStatusWaiter;
import com.liveperson.ephemerals.provider.kubernetes.KubernetesDeploymentHandler;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by waseemh on 9/16/16.
 */
public class KubernetesDeploymentStatusWaiter extends DeploymentStatusWaiter {

    private final static Logger logger = LoggerFactory.getLogger(KubernetesDeploymentStatusWaiter.class);

    private KubernetesClient client;

    public KubernetesDeploymentStatusWaiter(KubernetesClient client, Deployment deployment, DeploymentStatus desiredStatus) {
        super(deployment, desiredStatus);
        this.client = client;
    }

    @Override
    public DeploymentStatus getDeploymentStatus() {

        PodList list = client.pods().withLabel(KubernetesDeploymentHandler.DEPLOYMENT_LABEL_KEY,deployment.getId()).list();

        Pod pod = list.getItems().get(0);
        ContainerStatus containerStatus = pod.getStatus().getContainerStatuses().get(0);

        switch (pod.getStatus().getPhase()) {

            case "Pending":
                return DeploymentStatus.IN_PROGRESS;

            case "Running":

                if (containerStatus.getReady()) {
                    return DeploymentStatus.FINISHED;
                }

                // Out of memory / CPU issues
                else if (containerStatus.getRestartCount() > deployment.getDeploymentConfiguration().getMaxDeploymentRetries() &&
                        (containerStatus.getLastState().getTerminated().getExitCode() == 137 ||
                                containerStatus.getLastState().getTerminated().getExitCode() == 143)) {
                    return DeploymentStatus.FAILED;
                }

                // General error
                else if (containerStatus.getRestartCount() > deployment.getDeploymentConfiguration().getMaxDeploymentRetries() &&
                        containerStatus.getLastState().getTerminated().getReason().contains("Error") &&
                        containerStatus.getState().getTerminated().getReason().contains("Error") &&
                        containerStatus.getLastState().getTerminated().getExitCode().equals(
                                containerStatus.getState().getTerminated().getExitCode())) {
                    return DeploymentStatus.FAILED;
                }

                // CrashLoopBackOff errors
                else if (containerStatus.getRestartCount() > deployment.getDeploymentConfiguration().getMaxDeploymentRetries() &&
                        containerStatus.getLastState().getTerminated() != null &&
                        containerStatus.getState().getWaiting().getReason().contains("CrashLoopBackOff")) {
                    return DeploymentStatus.FAILED;
                }

                // Partial deployment if it was terminated and not restarted
                else if (containerStatus.getRestartCount() == 0 &&
                        containerStatus.getState().getTerminated() != null) {
                    return DeploymentStatus.PARTIALLY;
                }

                else {
                    return DeploymentStatus.IN_PROGRESS;
                }

            case "Failed":
                return DeploymentStatus.FAILED;

            case "Unknown":
                return DeploymentStatus.UNKNOWN;

            default:
                return DeploymentStatus.UNKNOWN;

        }
    }
}
