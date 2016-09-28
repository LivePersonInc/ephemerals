# Ephemerals

Ephemerals are short-lived cluster endpoints, designed to setup complex test environments on the fly, be integrated with cluster management systems and enable testing at a large scale.
Ephemerals can be deployed on various cloud providers for creating use-and-throw instances of browsers, web services, databases or anything else than can expose a connection endpoint.

The main motivation behind Ephemerals is that whole test environment is launched and destroyed during test lifecycle.

Ephemerals currently support Kuberenetes as a deployment provider. More providers will be added soon.

## Documentation

See the [Wiki](https://github.com/LivePersonInc/ephemerals/wiki/) for documentation and examples.

## Features

#### Tests Integration

Ephemerals can be integrated into system tests, integration and end-to-end tests to programmatically create required environment entities from developer's test code.

#### Plug-able Cluster Systems
 
Ephemerals were built to support multiple cluster management systems (Ephemeral's Deployment Providers). For example, you can switch from Kubernetes to Mesos almost transparently.

#### Cluster-based Scaling

Ephemerals can scale according to your servers cluster resources. The amount of parallel Ephemerals instances depends only on your cluster's nodes capacity.

#### Cloud Platforms Support

Ephemerals can be used with any cloud computing platform (Google Cloud, AWS, etc..), given it supports one of our Deployment Providers.
A perfect use-case would be using Ephemerals with Google Cloud Platform and its GKE, which is based on Kubernetes.

## Quickstart

To start using Ephemerals, you need to have an existing cluster supported by one of Deployment Provider.
Below example will assume a Kubernetes cluster is already setup.

Setup Kubernetes Client:

```java
KubernetesClient kubernetesClient = new DefaultKubernetesClient(new ConfigBuilder()
        .withMasterUrl(K8S_URL)
        .withTrustCerts(true)
        .withUsername(K8S_USERNAME)
        .withPassword(K8S_PASSWORD)
        .withNamespace(K8S_NAMESPACE)
        .build());
```

Setup deployment configuration;

```java
Deployer deployer = new KubernetesDeployer.Builder(kubernetesClient)
        .build();

DeploymentConfiguration deploymentConfiguration = new KubernetesDeploymentConfiguration.Builder()
        .build();
        
```

Initialize Nginx Ephemeral:

```java
NginxEphemeral nginxEphemeral = new NginxEphemeral.Builder(deployer,deploymentConfiguration)
        .build();
```

Deploy Ephemeral:

```java
URL url = nginxEphemeral.get();
```

Send HTTP GET to Nginx server:

````java
HttpClient httpClient = HttpClient.create().get(url).verifyResponseOk();
```

## Build

Maven is used as a build system. In order to produce a package, run maven command `mvn clean package -DskipTests`. 

Tests can be executed using command `mvn test`.

## License

MIT
