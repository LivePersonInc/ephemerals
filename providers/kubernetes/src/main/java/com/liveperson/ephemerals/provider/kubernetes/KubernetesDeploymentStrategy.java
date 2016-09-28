package com.liveperson.ephemerals.provider.kubernetes;

import com.liveperson.ephemerals.deploy.Deployment;
import io.fabric8.kubernetes.api.model.*;

/**
 * Created by waseemh on 9/16/16.
 */
public interface KubernetesDeploymentStrategy {

    PodSpec pod(Deployment deployment);

    ReplicationController replicationController(Deployment deployment);

    Service service(Deployment deployment);

}