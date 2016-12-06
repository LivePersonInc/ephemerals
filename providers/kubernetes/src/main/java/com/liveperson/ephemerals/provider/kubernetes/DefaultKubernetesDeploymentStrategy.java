package com.liveperson.ephemerals.provider.kubernetes;

import com.liveperson.ephemerals.deploy.Deployment;
import com.liveperson.ephemerals.deploy.probe.*;
import com.liveperson.ephemerals.deploy.unit.ContainerizedDeploymentUnit;
import com.liveperson.ephemerals.deploy.volume.*;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.Volume;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.liveperson.ephemerals.provider.kubernetes.KubernetesDeploymentHandler.DEPLOYMENT_LABEL_KEY;

/**
 * Created by waseemh on 9/16/16.
 */
public class DefaultKubernetesDeploymentStrategy implements KubernetesDeploymentStrategy {

    @Override
    public PodSpec pod(Deployment deployment) {

        PodSpecBuilder podSpec = new PodSpecBuilder();

        ContainerizedDeploymentUnit deploymentUnit =
                (ContainerizedDeploymentUnit) deployment.getDeploymentUnit();

        List<ContainerPort> containerPortList = deployment.getDeploymentUnit().getPorts()
                .stream().map(port -> new ContainerPortBuilder()
                        .withContainerPort(port.getPort())
                        .withName(port.getName())
                        .build()).collect(Collectors.toList());

        Probe healthProbe=null;
        Probe readinessProbe=null;
        if(deploymentUnit.getHealthProbe()!=null) {
            healthProbe = DefaultKubernetesDeploymentStrategy.probe(deploymentUnit.getHealthProbe());
        }
        if(deploymentUnit.getReadinessProbe()!=null) {
            readinessProbe = DefaultKubernetesDeploymentStrategy.probe(deploymentUnit.getReadinessProbe());
        }

        ContainerBuilder containerBuilder = new ContainerBuilder();

        if(healthProbe!=null) {
            containerBuilder.withLivenessProbe(healthProbe);
        }

        if(readinessProbe!=null) {
            containerBuilder.withReadinessProbe(readinessProbe);
        }

        Container container = containerBuilder
                .withName(deployment.getId())
                .withPorts(containerPortList)
                .withImage(deploymentUnit.getImage())
                .withEnv(envVars(deploymentUnit.getEnvVars()))
                .withResources(new ResourceRequirementsBuilder()
                        .addToLimits("cpu",new QuantityBuilder()
                                .withAmount(String.valueOf(deploymentUnit.getCpu()*1000)+"m").build())
                        .addToLimits("memory",new QuantityBuilder()
                                .withAmount(String.valueOf(deploymentUnit.getMem())+"Mi").build())
                        .build())
                .build();


        container.setVolumeMounts(deploymentUnit.getVolumes().entrySet().stream()
                .map(pair ->
                        new VolumeMountBuilder()
                                .withName(pair.getKey().getName())
                                .withMountPath(pair.getKey().getMountPath())
                                .build()
                ).collect(Collectors.toList()));

        podSpec.addToContainers(container);

        podSpec.withVolumes(deploymentUnit.getVolumes().entrySet().stream()
                .filter(pair -> pair!=null)
                .map(pair -> {
                            Volume volume = volume(pair.getValue());
                            volume.setName(pair.getKey().getName());
                            return volume;
                        }
                ).collect(Collectors.toList()));

        return podSpec.build();
    }

    public static List<EnvVar> envVars(Map<String,String> envVarsMap) {

        return envVarsMap.entrySet().stream()
                .map(pair -> new EnvVarBuilder().withName(pair.getKey()).withValue(pair.getValue()).build())
                .collect(Collectors.toList());
    }

    public static Volume volume(com.liveperson.ephemerals.deploy.volume.Volume volume) {
        if(volume instanceof GitRepoVolume) {
            return new VolumeBuilder()
                    .withGitRepo(new GitRepoVolumeSourceBuilder()
                            .withRepository(((GitRepoVolume) volume).getRepository())
                            .withRevision(((GitRepoVolume) volume).getRevision())
                            .withDirectory(((GitRepoVolume) volume).getTargetDirectory())
                            .build()
                    ).build();
        }
        return null;
    }

    public static Probe probe(com.liveperson.ephemerals.deploy.probe.Probe probe) {
        if(probe instanceof HttpProbe) {
            return new ProbeBuilder()
                    .withHttpGet(
                            new HTTPGetActionBuilder()
                                    .withPath(((HttpProbe) probe).getPath())
                                    .withNewPort(((HttpProbe) probe).getPort())
                                    .build()
                    )
                    .withTimeoutSeconds(probe.getTimeout())
                    .withInitialDelaySeconds(probe.getDelay())
                    .withPeriodSeconds(probe.getPeriod())
                    .build();
        }
        else if (probe instanceof TcpProbe) {
            return new ProbeBuilder()
                    .withTcpSocket(
                            new TCPSocketActionBuilder()
                                    .withNewPort(((TcpProbe) probe).getPort())
                                    .build()
                    )
                    .withTimeoutSeconds(probe.getTimeout())
                    .withInitialDelaySeconds(probe.getDelay())
                    .withPeriodSeconds(probe.getPeriod())
                    .build();
        }
        else if (probe instanceof CommandProbe) {
            return new ProbeBuilder()
                    .withExec(
                            new ExecActionBuilder()
                                    .withCommand(((CommandProbe) probe).getCmd())
                                    .build()
                    )
                    .withTimeoutSeconds(probe.getTimeout())
                    .withInitialDelaySeconds(probe.getDelay())
                    .withPeriodSeconds(probe.getPeriod())
                    .build();
        }
        else throw new UnsupportedOperationException("Unsupported probe type " + probe.getClass().getName());
    }

    @Override
    public ReplicationController replicationController(Deployment deployment) {

        ReplicationController rc = new ReplicationControllerBuilder()
                .withNewMetadata()
                .withName(deployment.getId())
                .addToLabels(DEPLOYMENT_LABEL_KEY,deployment.getId())
                .endMetadata()
                .withNewSpec()
                .withReplicas(deployment.getDeploymentConfiguration().getReplicas())
                .addToSelector(DEPLOYMENT_LABEL_KEY,deployment.getId())
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels(DEPLOYMENT_LABEL_KEY,deployment.getId())
                .endMetadata()
                .withSpec(pod(deployment))
                .endTemplate()
                .endSpec()
                .build();

        return rc;
    }

    @Override
    public Service service(Deployment deployment) {

        KubernetesDeploymentConfiguration kubernetesDeploymentConfiguration =
                ((KubernetesDeploymentConfiguration)deployment.getDeploymentConfiguration());

        List<ServicePort> servicePorts = deployment.getDeploymentUnit().getPorts()
                .stream().map(deploymentPort -> new ServicePortBuilder()
                        .withPort(deploymentPort.getExternalPort())
                        .withTargetPort(new IntOrString(deploymentPort.getPort()))
                        .withName(deploymentPort.getName())
                        .build()).collect(Collectors.toList());

        ServiceSpec spec = new ServiceSpecBuilder()
                .withType(kubernetesDeploymentConfiguration.getKubernetesServiceType().getValue())
                .addToSelector(DEPLOYMENT_LABEL_KEY,deployment.getId())
                .withPorts(servicePorts)
                .build();

        Service service = new ServiceBuilder()
                .withNewMetadata()
                .withName(deployment.getId())
                .addToLabels(DEPLOYMENT_LABEL_KEY,deployment.getId())
                .endMetadata()
                .withSpec(spec)
                .build();

        return service;
    }


}
